package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class GraduationMasterCacheTest {
    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    private final AtomicLong clock = new AtomicLong(1);
    private final Cache storage = spy(new ConcurrentMapCache("test"));
    private final GraduationMasterCache cache = new GraduationMasterCache(
        storage, new ObjectMapper(), registry, clock::get);

    @Test
    void cachesEmptyResultsAndMeasuresHitsMissesAndLoads() {
        assertThat(cache.get("empty", String.class, List::of)).isEmpty();
        assertThat(cache.get("empty", String.class, () -> { throw new AssertionError(); })).isEmpty();
        assertThat(count("miss")).isEqualTo(1);
        assertThat(count("hit")).isEqualTo(1);
        assertThat(count("load")).isEqualTo(1);
    }

    @Test
    void structuredKeysSeparateAllLookupArguments() {
        assertThat(cache.key("common", 20, "ENG12")).isNotEqualTo(cache.key("common", 20, "ENG34"));
        assertThat(cache.key("core", 20)).isNotEqualTo(cache.key("core", 21));
        assertThat(cache.key("basic", "major", 20)).isNotEqualTo(cache.key("basic", "other", 20));
        assertThat(cache.key("policy-major", "major", 20, "PRIMARY"))
            .isNotEqualTo(cache.key("policy-major", "major", 20, "DUAL"))
            .isNotEqualTo(cache.key("policy-basic", "major", 20, "PRIMARY"));
        assertThat(cache.key("major", "a,b")).isNotEqualTo(cache.key("major", "a", "b"));
    }

    @Test
    @Timeout(10)
    void concurrentLoadsAreCoalescedWithinOneJvm() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<List<String>>> futures = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                futures.add(executor.submit(() -> {
                    start.await();
                    return cache.get("same", String.class, () -> {
                        calls.incrementAndGet();
                        return List.of("value");
                    });
                }));
            }
            start.countDown();
            for (var future : futures) {
                assertThat(future.get(5, TimeUnit.SECONDS)).containsExactly("value");
            }
        }
        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    void readFailureFallsBackWithoutRepeatedRedisCallsAndThenRecovers() {
        doThrow(new IllegalStateException("Redis unavailable")).when(storage).get("key");
        assertThat(cache.get("key", String.class, () -> List.of("db"))).containsExactly("db");
        assertThat(cache.get("key", String.class, () -> List.of("db"))).containsExactly("db");
        verify(storage, times(1)).get("key");
        verify(storage, never()).put(any(), any());
        assertThat(count("error")).isEqualTo(1);
        assertThat(count("bypass")).isEqualTo(1);

        doCallRealMethod().when(storage).get("key");
        clock.addAndGet(Duration.ofSeconds(6).toNanos());
        cache.get("key", String.class, () -> List.of("recovered"));
        assertThat(cache.get("key", String.class, () -> { throw new AssertionError(); }))
            .containsExactly("recovered");
    }

    @Test
    void writeFailureDoesNotFailSuccessfulDbRead() {
        doThrow(new IllegalStateException("Redis unavailable")).when(storage).put(any(), any());
        assertThat(cache.get("key", String.class, () -> List.of("db"))).containsExactly("db");
        assertThat(count("error")).isEqualTo(1);
    }

    @Test
    void databaseFailurePropagatesOnceAndIsNotCached() {
        AtomicInteger calls = new AtomicInteger();
        assertThatThrownBy(() -> cache.get("key", String.class, () -> {
            calls.incrementAndGet();
            throw new IllegalStateException("DB unavailable");
        })).hasMessage("DB unavailable");
        assertThat(calls.get()).isEqualTo(1);
        assertThat(count("error")).isZero();
        assertThat(cache.get("key", String.class, () -> List.of("recovered"))).containsExactly("recovered");
    }

    @Test
    void incompatibleCachedValueFallsBackToDb() {
        storage.put("key", List.of(123));
        assertThat(cache.get("key", String.class, () -> List.of("db"))).containsExactly("db");
        assertThat(count("error")).isEqualTo(1);
    }

    @Test
    void publishesOnlyAfterCommit() {
        beginTransaction();
        try {
            cache.get("key", String.class, () -> List.of("committed"));
            assertThat(storage.get("key")).isNull();
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
            assertThat(storage.get("key").get()).isEqualTo(List.of("committed"));
        } finally {
            TransactionSynchronizationManager.clear();
        }
    }

    @Test
    void rollbackDoesNotPublish() {
        beginTransaction();
        try {
            cache.get("key", String.class, () -> List.of("uncommitted"));
            TransactionSynchronizationManager.getSynchronizations().forEach(
                synchronization -> synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
        } finally {
            TransactionSynchronizationManager.clear();
        }
        assertThat(storage.get("key")).isNull();
    }

    @Test
    void snapshotsAndListsDoNotShareMutableState() {
        var original = MajorLecture.of(Lecture.of("TEST", "Test", 3, 0, null), "major", 1, 16, 17);
        var snapshot = MasterCacheSnapshots.Major.from(original);
        var serializer = new JdkSerializationRedisSerializer();
        var restored = (MasterCacheSnapshots.Major) serializer.deserialize(serializer.serialize(snapshot));
        original.changeMandatoryToElectiveByEntryYearRange(20);
        restored.toDomain().changeMandatoryToElectiveByEntryYearRange(20);
        assertThat(restored.toDomain().getIsMandatory()).isEqualTo(1);

        List<String> source = new ArrayList<>(List.of("original"));
        var result = cache.get("key", String.class, () -> source);
        source.clear();
        assertThat(result).containsExactly("original");
        assertThatThrownBy(result::clear).isInstanceOf(UnsupportedOperationException.class);
    }

    private void beginTransaction() {
        TransactionSynchronizationManager.setActualTransactionActive(true);
        TransactionSynchronizationManager.initSynchronization();
    }

    private double count(String outcome) {
        return registry.get("graduation.master.cache").tag("outcome", outcome).counter().count();
    }
}
