package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Timeout(10)
class TakenLectureCacheTest {
    final TakenLectureCacheStore store = mock(TakenLectureCacheStore.class);
    final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    final AtomicLong clock = new AtomicLong(1000);
    final TakenLectureSnapshot value = new TakenLectureSnapshot(1L, 7, 9, List.of(
        new TakenLectureSnapshot.Entry(1L, "SYN-ONE", 2026, null, Instant.EPOCH, new TakenLectureSnapshot.Course("Synthetic course", 3, null, 0))));

    TakenLectureCache cache(TakenLectureCache.Mode mode) {
        return new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(mode, "synthetic", Duration.ofMinutes(10), Duration.ofSeconds(5), 1, 0),
            clock::get);
    }

    @AfterEach
    void clean() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) TransactionSynchronizationManager.clearSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(false);
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);
        registry.close();
    }

    void begin(boolean readOnly) {
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(readOnly);
    }

    void commit() {
        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
    }

    @Test
    void immutableMembershipOnlyIsPublishedAfterSuccessfulReadTransaction() throws Exception {
        begin(true);
        var cache = cache(TakenLectureCache.Mode.ON);
        assertThat(cache.get(1L, 7, 9, () -> value)).isEqualTo(value);
        verify(store, never()).put(anyString(), anyString(), any());
        commit();
        verify(store).put(cache.key(1L, 7, 9), mapper.writeValueAsString(value), Duration.ofMinutes(10));
        assertThat(mapper.writeValueAsString(value)).doesNotContain("password", "authId", "studentNumber");
    }

    @Test
    void rollbackDoesNotPublishAndNoTransactionDoesNotPublish() {
        begin(true);
        var cache = cache(TakenLectureCache.Mode.ON);
        cache.get(1L, 7, 9, () -> value);
        TransactionSynchronizationManager.getSynchronizations().forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
        clean();
        cache.get(1L, 7, 9, () -> value);
        verify(store, never()).put(anyString(), anyString(), any());
    }

    @Test
    void writeTransactionNeverRegistersPublication() {
        begin(false);
        cache(TakenLectureCache.Mode.ON).get(1L, 7, 9, () -> value);
        commit();
        verify(store, never()).put(anyString(), anyString(), any());
    }

    @Test
    void cacheHitDoesNotCallDatabaseLoader() throws Exception {
        var cache = cache(TakenLectureCache.Mode.ON);
        when(store.get(cache.key(1L, 7, 9))).thenReturn(mapper.writeValueAsString(value));
        assertThat(cache.get(1L, 7, 9, () -> { throw new AssertionError("DB loader called on cache hit"); })).isEqualTo(value);
    }

    @Test
    void malformedAndForeignUserPayloadsFallBack() throws Exception {
        var cache = cache(TakenLectureCache.Mode.ON);
        when(store.get(anyString())).thenReturn("broken JSON", mapper.writeValueAsString(new TakenLectureSnapshot(2L, 7, 9, value.entries())));
        assertThat(cache.get(1L, 7, 9, () -> value)).isEqualTo(value);
        clock.addAndGet(Duration.ofSeconds(6).toNanos());
        assertThat(cache.get(1L, 7, 9, () -> value)).isEqualTo(value);
        assertThat(registry.get("takenlecture.cache").tag("outcome", "error").counter().count()).isEqualTo(2);
    }

    @Test
    void publicationFailureDoesNotEscapeCommittedRequest() {
        begin(true);
        var cache = cache(TakenLectureCache.Mode.ON);
        doThrow(new IllegalStateException("synthetic")).when(store).put(anyString(), anyString(), any());
        cache.get(1L, 7, 9, () -> value);
        assertThatCode(this::commit).doesNotThrowAnyException();
        assertThat(registry.get("takenlecture.cache").tag("outcome", "error").counter().count()).isEqualTo(1);
    }

    @Test
    void databaseFailureIsNotHiddenAsCacheFailure() {
        var cache = cache(TakenLectureCache.Mode.ON);
        assertThatThrownBy(() -> cache.get(1L, 7, 9, () -> { throw new IllegalStateException("DB unavailable"); }))
            .hasMessage("DB unavailable");
        assertThat(registry.find("takenlecture.cache").tag("outcome", "error").counter()).isNull();
    }

    @Test
    void failuresOpenCooldownAndOneRecoveryReadRestoresHits() throws Exception {
        var cache = cache(TakenLectureCache.Mode.ON);
        when(store.get(anyString())).thenThrow(new IllegalStateException("synthetic"))
            .thenReturn(mapper.writeValueAsString(value));
        cache.get(1L, 7, 9, () -> value);
        cache.get(1L, 7, 9, () -> value);
        verify(store, times(1)).get(anyString());
        clock.addAndGet(Duration.ofSeconds(6).toNanos());
        assertThat(cache.get(1L, 7, 9, () -> { throw new AssertionError(); })).isEqualTo(value);
        verify(store, times(2)).get(anyString());
    }

    @Test
    void redisConcurrencyBudgetFallsBackWithoutQueuing() throws Exception {
        var cache = cache(TakenLectureCache.Mode.ON);
        var entered = new CountDownLatch(1);
        var resume = new CountDownLatch(1);
        when(store.get(anyString())).thenAnswer(invocation -> {
            entered.countDown();
            assertThat(resume.await(5, TimeUnit.SECONDS)).isTrue();
            return null;
        });
        try (var executor = Executors.newSingleThreadExecutor()) {
            var first = executor.submit(() -> cache.get(1L, 7, 9, () -> value));
            try {
                assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(cache.get(1L, 7, 9, () -> value)).isEqualTo(value);
                verify(store, times(1)).get(anyString());
                assertThat(registry.get("takenlecture.cache").tag("outcome", "saturated").counter().count()).isEqualTo(1);
            } finally { resume.countDown(); }
            first.get(5, TimeUnit.SECONDS);
        }
    }

    @Test
    void earlierSuccessCannotCloseCircuitOpenedByConcurrentFailure() throws Exception {
        var cache = new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(TakenLectureCache.Mode.ON, "synthetic", Duration.ofMinutes(10), Duration.ofSeconds(5), 2, 0),
            clock::get);
        var entered = new CountDownLatch(1);
        var resume = new CountDownLatch(1);
        var calls = new java.util.concurrent.atomic.AtomicInteger();
        when(store.get(anyString())).thenAnswer(invocation -> {
            if (calls.incrementAndGet() == 1) {
                entered.countDown();
                assertThat(resume.await(5, TimeUnit.SECONDS)).isTrue();
                return null;
            }
            throw new IllegalStateException("concurrent Redis failure");
        });
        try (var executor = Executors.newSingleThreadExecutor()) {
            var first = executor.submit(() -> cache.get(1L, 7, 9, () -> value));
            try {
                assertThat(entered.await(5, TimeUnit.SECONDS)).isTrue();
                cache.get(1L, 7, 9, () -> value);
            } finally { resume.countDown(); }
            first.get(5, TimeUnit.SECONDS);
            cache.get(1L, 7, 9, () -> value);
            verify(store, times(2)).get(anyString());
            assertThat(registry.get("takenlecture.cache").tag("outcome", "bypass").counter().count()).isEqualTo(1);
        }
    }

    @Test
    void shadowReturnsDatabaseTruthAndReportsMismatch() throws Exception {
        begin(true);
        var cache = cache(TakenLectureCache.Mode.SHADOW);
        when(store.get(anyString())).thenReturn(mapper.writeValueAsString(new TakenLectureSnapshot(1L, 7, 9, List.of())));
        assertThat(cache.get(1L, 7, 9, () -> value)).isEqualTo(value);
        assertThat(registry.get("takenlecture.cache").tag("outcome", "mismatch").counter().count()).isEqualTo(1);
        commit();
        verify(store).put(cache.key(1L, 7, 9), mapper.writeValueAsString(value), Duration.ofMinutes(10));
    }

    @Test
    void boundedLocalCacheReusesOnlyTheExactTwoRevisionsAndExpires() throws Exception {
        var cache = new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(TakenLectureCache.Mode.ON, "synthetic", Duration.ofMinutes(10), Duration.ofSeconds(5), 2, 1),
            clock::get);
        when(store.get(anyString())).thenReturn(mapper.writeValueAsString(value));
        cache.get(1L, 7, 9, () -> value);
        cache.get(1L, 7, 9, () -> { throw new AssertionError("local hit loaded DB"); });
        verify(store, times(1)).get(anyString());
        assertThat(registry.get("takenlecture.cache").tag("outcome", "local_hit").counter().count()).isEqualTo(1);
        var next = new TakenLectureSnapshot(1L, 8, 10, value.entries());
        when(store.get(cache.key(1L, 8, 10))).thenReturn(mapper.writeValueAsString(next));
        assertThat(cache.get(1L, 8, 10, () -> next)).isEqualTo(next);
        cache.get(1L, 7, 9, () -> value); // The one-entry budget evicted the earlier key.
        verify(store, times(2)).get(cache.key(1L, 7, 9));
        clock.addAndGet(Duration.ofMinutes(11).toNanos());
        cache.get(1L, 7, 9, () -> value);
        verify(store, times(3)).get(cache.key(1L, 7, 9));
    }

    @Test
    void localCacheDoesNotPublishDatabaseMissUntilCommitAndNotAfterRollback() {
        var cache = new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(TakenLectureCache.Mode.ON, "synthetic", Duration.ofMinutes(10), Duration.ofSeconds(5), 2, 8),
            clock::get);
        begin(true);
        cache.get(1L, 7, 9, () -> value);
        cache.get(1L, 7, 9, () -> value);
        verify(store, times(2)).get(anyString());
        TransactionSynchronizationManager.getSynchronizations().forEach(synchronization ->
            synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
        TransactionSynchronizationManager.clearSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(false);
        cache.get(1L, 7, 9, () -> value);
        verify(store, times(3)).get(anyString());
        verify(store, never()).put(anyString(), anyString(), any());
    }

    @Test
    void committedMissPopulatesLocalAndShadowStillReadsDatabase() {
        var cache = new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(TakenLectureCache.Mode.ON, "synthetic", Duration.ofMinutes(10), Duration.ofSeconds(5), 2, 8),
            clock::get);
        begin(true);
        cache.get(1L, 7, 9, () -> value);
        commit();
        assertThat(cache.get(1L, 7, 9, () -> { throw new AssertionError(); })).isEqualTo(value);
        verify(store, times(1)).get(anyString());
        var shadow = new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(TakenLectureCache.Mode.SHADOW, "synthetic", Duration.ofMinutes(10), Duration.ofSeconds(5), 2, 8),
            clock::get);
        shadow.get(1L, 7, 9, () -> value);
        shadow.get(1L, 7, 9, () -> value);
        verify(store, times(3)).get(anyString());
    }

    @Test
    void offModeUsesNoRedisAndKeysSeparateNamespacesAndRevisions() {
        var cache = cache(TakenLectureCache.Mode.OFF);
        assertThat(cache.get(1L, 7, 9, () -> value)).isEqualTo(value);
        verifyNoInteractions(store);
        assertThat(cache.key(1L, 7, 9)).isNotEqualTo(cache.key(1L, 8, 9)).isNotEqualTo(cache.key(2L, 7, 9));
        assertThat(cache.key(1L, 7, 9)).isEqualTo("takenLectures:v3:synthetic:1:7:9");
    }
}
