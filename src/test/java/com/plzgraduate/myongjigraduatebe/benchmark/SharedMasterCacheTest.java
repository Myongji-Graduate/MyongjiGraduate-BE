package com.plzgraduate.myongjigraduatebe.benchmark;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class SharedMasterCacheTest {
    private SharedMasterCache cache(SharedMasterCache.Mode mode) {
        var result = new SharedMasterCache(new ConcurrentMapCache("unit-test"), new ObjectMapper());
        result.reset(mode);
        return result;
    }

    @ParameterizedTest
    @EnumSource(value = SharedMasterCache.Mode.class, names = "PERSONAL_ONLY", mode = EnumSource.Mode.EXCLUDE)
    void cachesEmptyResultsAndSeparatesKeys(SharedMasterCache.Mode mode) {
        var cache = cache(mode);
        AtomicInteger calls = new AtomicInteger();
        String key = cache.key("policy", "major", 20, "PRIMARY");
        assertThat(cache.get(key, () -> { calls.incrementAndGet(); return List.<String>of(); })).isEmpty();
        assertThat(cache.get(key, () -> { throw new AssertionError("Unexpected reload"); })).isEmpty();
        assertThat(calls.get()).isEqualTo(1);
        assertThat(cache.key("common", 20, "ENG12")).isNotEqualTo(cache.key("common", 20, "ENG34"));
        assertThat(cache.key("policy", "major", 20, "PRIMARY")).isNotEqualTo(cache.key("policy", "major", 17, "PRIMARY"));
        assertThat(cache.key("major", "a,b")).isNotEqualTo(cache.key("major", "a", "b"));
    }

    @ParameterizedTest
    @EnumSource(value = SharedMasterCache.Mode.class, names = "PERSONAL_ONLY", mode = EnumSource.Mode.EXCLUDE)
    @Timeout(10)
    void coalescesConcurrentLoadsWithinOneJvm(SharedMasterCache.Mode mode) throws Exception {
        var cache = cache(mode);
        AtomicInteger calls = new AtomicInteger();
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<List<String>>> futures = new ArrayList<>();
            for (int i = 0; i < 32; i++) futures.add(executor.submit(() -> {
                start.await();
                return cache.get("same-key", () -> { calls.incrementAndGet(); return List.of("value"); });
            }));
            start.countDown();
            for (var future : futures) assertThat(future.get(5, TimeUnit.SECONDS)).containsExactly("value");
        }
        assertThat(calls.get()).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(value = SharedMasterCache.Mode.class, names = "PERSONAL_ONLY", mode = EnumSource.Mode.EXCLUDE)
    void failedLoadsAreNotCached(SharedMasterCache.Mode mode) {
        var cache = cache(mode);
        assertThatThrownBy(() -> cache.get("failure", () -> { throw new IllegalStateException("DB unavailable"); }))
            .isInstanceOf(IllegalStateException.class);
        assertThat(cache.get("failure", () -> List.of("recovered"))).containsExactly("recovered");
    }

    @ParameterizedTest
    @EnumSource(value = SharedMasterCache.Mode.class, names = "PERSONAL_ONLY", mode = EnumSource.Mode.EXCLUDE)
    @Timeout(10)
    void inFlightOldVersionCannotOverwritePublishedVersion(SharedMasterCache.Mode mode) throws Exception {
        var cache = cache(mode);
        CountDownLatch loading = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var old = executor.submit(() -> cache.get("versioned", () -> {
                loading.countDown();
                try {
                    if (!release.await(5, TimeUnit.SECONDS)) throw new IllegalStateException("Timed out");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(exception);
                }
                return List.of("old");
            }));
            try {
                assertThat(loading.await(5, TimeUnit.SECONDS)).isTrue();
                TransactionSynchronizationManager.initSynchronization();
                try {
                    cache.publishAfterCommit();
                    TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
                } finally {
                    TransactionSynchronizationManager.clearSynchronization();
                }
                assertThat(cache.get("versioned", () -> List.of("new"))).containsExactly("new");
            } finally {
                release.countDown();
            }
            assertThat(old.get(5, TimeUnit.SECONDS)).containsExactly("old");
            assertThat(cache.get("versioned", () -> List.of("wrong"))).containsExactly("new");
        }
    }

    @Test
    void snapshotsSurviveSerializationWithoutSharingMutableMajorState() {
        var original = MajorLecture.of(Lecture.of("TEST", "Test", 3, 0, null), "major", 1, 16, 17);
        var snapshot = MasterCacheSnapshots.Major.from(original);
        var serializer = new JdkSerializationRedisSerializer();
        var restored = (MasterCacheSnapshots.Major) serializer.deserialize(serializer.serialize(snapshot));
        original.changeMandatoryToElectiveByEntryYearRange(20);
        var first = restored.toDomain();
        first.changeMandatoryToElectiveByEntryYearRange(20);
        assertThat(first.getIsMandatory()).isZero();
        assertThat(restored.toDomain().getIsMandatory()).isEqualTo(1);
        var cache = cache(SharedMasterCache.Mode.SHARED_CAFFEINE);
        List<String> source = new ArrayList<>(List.of("original"));
        List<String> stored = cache.get("list", () -> source);
        source.clear();
        assertThat(stored).containsExactly("original");
        assertThatThrownBy(stored::clear).isInstanceOf(UnsupportedOperationException.class);
    }
}
