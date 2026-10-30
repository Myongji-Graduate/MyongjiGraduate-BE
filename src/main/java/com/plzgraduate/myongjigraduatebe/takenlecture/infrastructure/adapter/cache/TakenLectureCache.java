package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class TakenLectureCache {
    public enum Mode { OFF, SHADOW, ON }
    public record Options(Mode mode, String namespace, Duration ttl, Duration cooldown,
                          int maxConcurrent, int localEntries) { }
    private final TakenLectureCacheStore store;
    private final ObjectMapper mapper;
    private final MeterRegistry metrics;
    private final Mode mode;
    private final String prefix;
    private final Duration ttl;
    private final long cooldownNanos;
    private final LongSupplier ticker;
    private final AtomicLong failedAt = new AtomicLong();
    private final AtomicBoolean recoveryProbe = new AtomicBoolean();
    private final Semaphore redisCapacity;
    private final int localEntries;
    private final LinkedHashMap<String, LocalEntry> local = new LinkedHashMap<>(16, 0.75f, true);
    private record LocalEntry(TakenLectureSnapshot value, long createdAt) { }

    public TakenLectureCache(TakenLectureCacheStore store, ObjectMapper mapper, MeterRegistry metrics, Options options) {
        this(store, mapper, metrics, options, System::nanoTime);
    }

    public TakenLectureCache(TakenLectureCacheStore store, ObjectMapper mapper, MeterRegistry metrics,
        Options options, LongSupplier ticker) {
        int localEntryLimit = options.localEntries();
        String namespace = options.namespace();
        Duration cacheTtl = options.ttl();
        Duration cooldown = options.cooldown();
        int maxConcurrent = options.maxConcurrent();
        if (localEntryLimit < 0 || localEntryLimit > 10000) throw new IllegalArgumentException("Invalid local cache limit");
        this.localEntries = localEntryLimit;
        if (!namespace.matches("[A-Za-z0-9._-]+") || cacheTtl.isNegative() || cacheTtl.isZero()
            || cooldown.isNegative() || cooldown.isZero() || maxConcurrent < 1) {
            throw new IllegalArgumentException("Invalid taken lecture cache configuration");
        }
        this.store = store;
        this.mapper = mapper.copy();
        this.metrics = metrics;
        this.mode = options.mode();
        this.prefix = "takenLectures:v3:" + namespace + ":";
        this.ttl = cacheTtl;
        this.cooldownNanos = cooldown.toNanos();
        this.ticker = ticker;
        this.redisCapacity = new Semaphore(maxConcurrent);
    }

    public Mode mode() { return mode; }
    public String key(Long userId, long revision, long catalogRevision) { return prefix + userId + ":" + revision + ":" + catalogRevision; }
    public void count(String outcome) { metrics.counter("takenlecture.cache", "outcome", outcome).increment(); }

    public TakenLectureSnapshot get(Long userId, long revision, long catalogRevision, Supplier<TakenLectureSnapshot> loader) {
        String key = key(userId, revision, catalogRevision);
        TakenLectureSnapshot localValue = mode == Mode.ON ? localGet(key) : null;
        if (localValue != null) {
            count("hit");
            count("local_hit");
            return localValue;
        }
        Attempt attempt = mode == Mode.OFF ? null : tryRedis();
        if (attempt == null) {
            count("bypass");
            return load(loader);
        }
        TakenLectureSnapshot cached = null;
        boolean cacheAvailable = true;
        try {
            cached = readSnapshot(key, userId, revision, catalogRevision);
            failedAt.compareAndSet(attempt.observedFailure(), 0);
        } catch (RuntimeException | JsonProcessingException exception) {
            fail();
            cacheAvailable = false;
        } finally {
            releaseRedis(attempt);
        }
        if (mode == Mode.ON && cached != null && cacheAvailable) {
            count("hit");
            localPut(key, cached);
            return cached;
        }
        count(cached == null ? "miss" : "shadow");
        if (!cacheAvailable) count("fallback");
        // Keep loader exceptions outside the Redis failure handler.
        TakenLectureSnapshot loaded = load(loader);
        if (mode == Mode.SHADOW && cached != null && !cached.equals(loaded)) count("mismatch");
        if (cacheAvailable) publishAfterCommit(key, loaded);
        return loaded;
    }

    private TakenLectureSnapshot readSnapshot(String key, Long userId, long revision, long catalogRevision)
        throws JsonProcessingException {
        String json = store.get(key);
        if (json == null) return null;
        TakenLectureSnapshot snapshot = mapper.readValue(json, TakenLectureSnapshot.class);
        if (!userId.equals(snapshot.userId()) || revision != snapshot.revision()
            || catalogRevision != snapshot.catalogRevision()) {
            throw new IllegalArgumentException("Snapshot version mismatch");
        }
        return snapshot;
    }

    private TakenLectureSnapshot load(Supplier<TakenLectureSnapshot> loader) {
        count("load");
        return metrics.timer("takenlecture.cache.load").record(loader);
    }

    private void publishAfterCommit(String key, TakenLectureSnapshot snapshot) {
        // No ambient transaction or writable transaction may publish a shared snapshot.
        if (!TransactionSynchronizationManager.isActualTransactionActive()
            || !TransactionSynchronizationManager.isCurrentTransactionReadOnly()
            || !TransactionSynchronizationManager.isSynchronizationActive()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { put(key, snapshot); }
        });
    }

    private void put(String key, TakenLectureSnapshot snapshot) {
        Attempt attempt = tryRedis();
        if (attempt == null) return;
        try {
            store.put(key, mapper.writeValueAsString(snapshot), ttl);
            failedAt.compareAndSet(attempt.observedFailure(), 0);
            count("put");
            localPut(key, snapshot);
        } catch (RuntimeException | JsonProcessingException exception) {
            // A failed cache publication must not turn a committed DB operation into an error response.
            fail();
        } finally {
            releaseRedis(attempt);
        }
    }

    private synchronized TakenLectureSnapshot localGet(String key) {
        if (localEntries == 0) return null;
        LocalEntry entry = local.get(key);
        if (entry == null) return null;
        if (ticker.getAsLong() - entry.createdAt() >= ttl.toNanos()) {
            local.remove(key);
            return null;
        }
        return entry.value();
    }

    private synchronized void localPut(String key, TakenLectureSnapshot value) {
        if (localEntries == 0 || mode != Mode.ON) return;
        local.put(key, new LocalEntry(value, ticker.getAsLong()));
        while (local.size() > localEntries) local.remove(local.keySet().iterator().next());
    }

    /** Clears only this process's bounded cache; DB revisions and Redis remain authoritative. */
    public synchronized void clearLocal() { local.clear(); }

    private record Attempt(long observedFailure, boolean recovery) { }

    private Attempt tryRedis() {
        long failure = failedAt.get();
        if (failure != 0 && ticker.getAsLong() - failure < cooldownNanos) return null;
        if (failure != 0 && !recoveryProbe.compareAndSet(false, true)) return null;
        if (!redisCapacity.tryAcquire()) {
            if (failure != 0) recoveryProbe.set(false);
            count("saturated");
            return null;
        }
        return new Attempt(failure, failure != 0);
    }

    private void releaseRedis(Attempt attempt) {
        redisCapacity.release();
        if (attempt.recovery()) recoveryProbe.set(false);
    }

    private void fail() {
        long now = ticker.getAsLong();
        failedAt.set(now == 0 ? 1 : now);
        count("error");
    }
}
