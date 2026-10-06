package com.plzgraduate.myongjigraduatebe.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import org.springframework.cache.Cache;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

final class SharedMasterCache {
    enum Mode { PERSONAL_ONLY, SHARED_REDIS, SHARED_CAFFEINE }

    private final Cache redis;
    private final ObjectMapper mapper;
    private final com.github.benmanes.caffeine.cache.Cache<String, List<? extends Serializable>> local =
        Caffeine.newBuilder().maximumSize(2048).expireAfterWrite(Duration.ofHours(24)).build();
    private final Striped<Lock> loadLocks = Striped.lock(256);
    private final AtomicLong version = new AtomicLong(1);
    private final LongAdder requests = new LongAdder();
    private final LongAdder loads = new LongAdder();
    volatile Mode mode = Mode.PERSONAL_ONLY;

    SharedMasterCache(Cache redis, ObjectMapper mapper) {
        this.redis = redis;
        this.mapper = mapper;
    }

    String key(Object... arguments) {
        try {
            return mapper.writeValueAsString(arguments);
        } catch (JsonProcessingException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @SuppressWarnings("unchecked")
    <T extends Serializable> List<T> get(String key, Supplier<List<T>> loader) {
        requests.increment();
        if (mode == Mode.PERSONAL_ONLY) return load(loader);
        String versionedKey = version.get() + ":" + key;
        if (mode == Mode.SHARED_CAFFEINE) {
            return (List<T>) local.get(versionedKey, ignored -> load(loader));
        }
        Cache.ValueWrapper cached = redis.get(versionedKey);
        if (cached != null) return (List<T>) cached.get();
        // Single-flight within this JVM only, not a distributed Redis lock.
        Lock lock = loadLocks.get(versionedKey);
        lock.lock();
        try {
            cached = redis.get(versionedKey);
            if (cached != null) return (List<T>) cached.get();
            List<T> value = load(loader);
            redis.put(versionedKey, value);
            return value;
        } finally {
            lock.unlock();
        }
    }

    private <T extends Serializable> List<T> load(Supplier<List<T>> loader) {
        loads.increment();
        return List.copyOf(loader.get());
    }

    void reset(Mode nextMode) {
        redis.clear();
        local.invalidateAll();
        local.cleanUp();
        version.incrementAndGet();
        mode = nextMode;
        resetCounters();
    }

    void resetCounters() { requests.reset(); loads.reset(); }

    Map<String, Long> counters() {
        return Map.of("requests", requests.sum(), "loads", loads.sum());
    }

    // A test-only, single-instance publication hook, not an operational policy update API.
    void publishAfterCommit() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Publication requires a transaction");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { version.incrementAndGet(); }
        });
    }

    Map<String, Long> localFootprint() {
        local.cleanUp();
        var serializer = new JdkSerializationRedisSerializer();
        long payloadBytes = local.asMap().values().stream()
            .mapToLong(value -> serializer.serialize(value).length).sum();
        return Map.of("entries", local.estimatedSize(), "serializedPayloadBytes", payloadBytes);
    }
}
