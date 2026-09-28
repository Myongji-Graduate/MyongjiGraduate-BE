package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
final class GraduationMasterCache {
    private static final long RETRY_DELAY_NANOS = Duration.ofSeconds(5).toNanos();
    private final Cache cache;
    private final ObjectMapper mapper;
    private final Object[] locks = new Object[256];
    private final AtomicLong lastFailure = new AtomicLong();
    private final LongSupplier ticker;
    private final Counter hits;
    private final Counter misses;
    private final Counter loads;
    private final Counter errors;
    private final Counter bypasses;

    GraduationMasterCache(Cache cache, ObjectMapper mapper, MeterRegistry registry) {
        this(cache, mapper, registry, System::nanoTime);
    }

    GraduationMasterCache(Cache cache, ObjectMapper mapper, MeterRegistry registry, LongSupplier ticker) {
        this.cache = cache;
        this.mapper = mapper;
        this.ticker = ticker;
        Arrays.setAll(locks, ignored -> new Object());
        hits = counter(registry, "hit");
        misses = counter(registry, "miss");
        loads = counter(registry, "load");
        errors = counter(registry, "error");
        bypasses = counter(registry, "bypass");
    }

    private Counter counter(MeterRegistry registry, String outcome) {
        return registry.counter("graduation.master.cache", "outcome", outcome);
    }

    String key(String kind, Object... arguments) {
        try {
            return mapper.writeValueAsString(List.of(kind, Arrays.asList(arguments)));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot encode graduation master cache key", exception);
        }
    }

    <T> List<T> get(String key, Class<T> entryType, Supplier<List<T>> loader) {
        if (isCoolingDown()) {
            bypasses.increment();
            return load(loader);
        }
        // This only coalesces loads within one JVM, not across application instances.
        synchronized (locks[Math.floorMod(key.hashCode(), locks.length)]) {
            if (isCoolingDown()) {
                bypasses.increment();
                return load(loader);
            }
            try {
                Cache.ValueWrapper entry = cache.get(key);
                if (entry != null) {
                    if (!(entry.get() instanceof List<?> values)
                        || values.stream().anyMatch(value -> !entryType.isInstance(value))) {
                        throw new IllegalStateException("Unexpected graduation master cache schema");
                    }
                    List<T> result = values.stream().map(entryType::cast).toList();
                    hits.increment();
                    return result;
                }
            } catch (RuntimeException exception) {
                cacheFailure(exception);
                return load(loader);
            }
            misses.increment();
            List<T> result = load(loader);
            publishAfterCommit(key, result);
            return result;
        }
    }

    private <T> List<T> load(Supplier<List<T>> loader) {
        loads.increment();
        // DB failures must propagate, rather than being mistaken for cache failures.
        return List.copyOf(loader.get());
    }

    private void publishAfterCommit(String key, List<?> value) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            put(key, value);
        } else if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    put(key, value);
                }
            });
        }
    }

    private void put(String key, List<?> value) {
        if (isCoolingDown()) {
            return;
        }
        try {
            cache.put(key, value);
        } catch (RuntimeException exception) {
            cacheFailure(exception);
        }
    }

    private boolean isCoolingDown() {
        long failedAt = lastFailure.get();
        return failedAt != 0 && ticker.getAsLong() - failedAt < RETRY_DELAY_NANOS;
    }

    private void cacheFailure(RuntimeException exception) {
        errors.increment();
        long now = ticker.getAsLong();
        long previous = lastFailure.getAndSet(now == 0 ? 1 : now);
        if (previous == 0 || now - previous >= RETRY_DELAY_NANOS) {
            log.warn("Graduation master cache unavailable; using DB for 5s ({})",
                exception.getClass().getSimpleName());
        }
    }
}
