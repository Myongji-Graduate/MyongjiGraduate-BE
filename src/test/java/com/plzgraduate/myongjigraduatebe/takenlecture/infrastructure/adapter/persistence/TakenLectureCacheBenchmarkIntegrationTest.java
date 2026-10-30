package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import static com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.TakenLectureCacheConsistencyIntegrationTest.*;
import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.core.config.RedisCacheConfig;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache.*;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.persistence.EntityManagerFactory;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.cache.Cache;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/** Bounded local experiment, not a production load/capacity claim. */
@EnabledIfEnvironmentVariable(named = "RUN_TAKEN_LECTURE_CACHE_BENCHMARK", matches = "true")
@Timeout(180)
class TakenLectureCacheBenchmarkIntegrationTest {
    @BeforeAll
    static void start() throws Exception {
        MYSQL.start();
        REDIS.start();
        openWithLocalEntries(128);
    }

    @AfterAll
    static void stop() {
        close();
        REDIS.stop();
        MYSQL.stop();
    }

    @Test
    void compareLegacyMembershipCacheAndDatabaseWithSyntheticInputs() throws Exception {
        var records = new ArrayList<Map<String, Object>>();
        var payloads = new ArrayList<Map<String, Object>>();
        var mapper = context.getBean(ObjectMapper.class);
        var factory = context.getBean(LettuceConnectionFactory.class);
        var manager = new RedisCacheConfig().cacheManager(factory);
        manager.afterPropertiesSet();
        Cache legacyCache = manager.getCache("dev121SyntheticLegacy");
        var redis = new StringRedisTemplate(factory);
        var cache = context.getBean(TakenLectureCache.class);
        var delegate = context.getBean(TakenLecturePersistenceAdapter.class);
        var users = context.getBean(FindUserUseCase.class);
        var reader = context.getBean(FindTakenLectureUseCase.class);
        var registry = context.getBean(SimpleMeterRegistry.class);
        var store = context.getBean(FaultStore.class);
        var shadowCache = new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(TakenLectureCache.Mode.SHADOW, namespace, Duration.ofMinutes(10), Duration.ofSeconds(5), 16, 0));
        var shadow = new CachedTakenLectureAdapter(delegate, delegate, shadowCache);
        var redisOnlyCache = new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(TakenLectureCache.Mode.ON, namespace, Duration.ofMinutes(10), Duration.ofSeconds(5), 16, 0));
        var redisOnly = new CachedTakenLectureAdapter(delegate, delegate, redisOnlyCache);
        var previous = new PreviousMembershipCacheReader(context, redis, mapper);
        for (int size : List.of(30, 60, 120)) {
            Long userId = seedStudent(size);
            Supplier<TakenLectureInventory> database = () -> tx(context, true).execute(status ->
                TakenLectureInventory.from(new HashSet<>(delegate.findTakenLecturesByUser(users.findUserById(userId)))));
            Supplier<TakenLectureInventory> improved = () -> reader.findTakenLectures(userId);
            Supplier<TakenLectureInventory> legacy = () -> {
                var hit = legacyCache.get(userId, TakenLectureInventory.class);
                if (hit != null) return hit;
                var result = database.get();
                legacyCache.put(userId, result);
                return result;
            };
            Supplier<TakenLectureInventory> shadowRead = () -> tx(context, true).execute(status ->
                shadow.findReadOnlySnapshotByUserId(userId).orElseThrow().inventory());
            String expected = canonical(database.get());
            var arms = new LinkedHashMap<String, Supplier<TakenLectureInventory>>();
            arms.put("legacy_warm", legacy); arms.put("revision_warm", improved);
            arms.put("redis_v3", () -> tx(context, true).execute(status ->
                redisOnly.findReadOnlySnapshotByUserId(userId).orElseThrow().inventory()));
            arms.put("membership_v2", () -> previous.find(userId)); arms.put("database", database); arms.put("shadow", shadowRead);
            for (var operation : arms.values()) for (int i = 0; i < 8; i++) operation.get();
            for (int concurrency : List.of(1, 4, 8)) {
                for (int repeat = 0; repeat < 3; repeat++) {
                    var order = new ArrayList<>(arms.keySet());
                    Collections.rotate(order, repeat);
                    for (String arm : order) {
                        records.add(measure(arm, size, concurrency, repeat, expected, arms.get(arm), 96));
                    }
                }
                cache.clearLocal();
                redis.delete(cache.key(userId, 0, catalogRevision()));
                records.add(measure("cold_burst", size, concurrency, 0, expected, improved, concurrency));
                context.getBean(AtomicLong.class).addAndGet(Duration.ofSeconds(6).toNanos());
                cache.clearLocal();
                store.failGet = true;
                try { records.add(measure("redis_failure_fallback", size, concurrency, 0, expected, improved, 32)); }
                finally {
                    store.failGet = false;
                    context.getBean(AtomicLong.class).addAndGet(Duration.ofSeconds(6).toNanos());
                    reader.findTakenLectures(userId);
                }
            }
            long bytes = Objects.requireNonNull(redis.opsForValue().get(cache.key(userId, 0, catalogRevision()))).getBytes(java.nio.charset.StandardCharsets.UTF_8).length;
            long legacyBytes = legacyCache.getNativeCache() == null ? 0 : new org.springframework.data.redis.serializer.JdkSerializationRedisSerializer().serialize(database.get()).length;
            payloads.add(Map.of("courses", size, "revisionJsonBytes", bytes, "legacyJdkBytes", legacyBytes,
                "revisionRedisMemoryBytes", redisMemory(redis, cache.key(userId, 0, catalogRevision())),
                "legacyRedisMemoryBytes", redisMemory(redis, "dev121SyntheticLegacy::" + userId)));
            for (int concurrency : List.of(1, 4, 8)) {
                for (int repeat = 0; repeat < 3; repeat++) records.add(measureMixed(size, concurrency, repeat));
            }
        }
        Path target = Path.of("build/dev121/synthetic-cache-benchmark.json");
        Files.createDirectories(target.getParent());
        mapper.writerWithDefaultPrettyPrinter().writeValue(target.toFile(), Map.of(
            "fixture", "hand-authored synthetic students and distinct courses; no source snapshot",
            "legacy", "equivalent cache-aside reader using the previous JDK-serialized inventory format; warm hits bypass DB",
            "scope", "in-process use-case reads, standalone local MySQL/Redis; excludes HTTP/auth and production capacity",
            "stopCriteria", "correctness/error count=0; each request<2s; DB connection wait timeout=2s",
            "cacheFormat", "v3; DB catalog triggers; local limit 128 keys / 10min TTL",
            "workload", "six warm arms, courses30/60/120, concurrency1/4/8, three rotations of96requests; cold/fault one round; mixed three rounds",
            "arms", Map.of("legacy_warm", "previous JDK cache-aside warm", "membership_v2", "previous membership-only JSON, 3SQL",
                "redis_v3", "v3 local disabled, 1SQL + Redis", "revision_warm", "v3 local enabled, 1SQL + local hit",
                "database", "direct DB, 2SQL", "shadow", "v3 DB truth with Redis comparison, 2SQL"),
            "payloads", payloads, "rows", records));
        assertThat(records).hasSize(207);
    }

    static Map<String, Object> measure(String arm, int courses, int concurrency, int repeat,
        String expected, Supplier<TakenLectureInventory> operation, int requests) throws Exception {
        var statistics = context.getBean(EntityManagerFactory.class).unwrap(SessionFactory.class).getStatistics();
        var pool = context.getBean(HikariDataSource.class).getHikariPoolMXBean();
        var registry = context.getBean(SimpleMeterRegistry.class);
        Timer acquire = registry.find("hikaricp.connections.acquire").timer();
        Map<String, Double> countersBefore = counters(registry);
        long sqlBefore = statistics.getPrepareStatementCount();
        long acquireCount = acquire == null ? 0 : acquire.count();
        double acquireTime = acquire == null ? 0 : acquire.totalTime(TimeUnit.NANOSECONDS);
        var os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long cpuBefore = os.getProcessCpuTime();
        var memory = ManagementFactory.getMemoryMXBean();
        long heapBefore = memory.getHeapMemoryUsage().getUsed();
        var maxWaiters = new AtomicInteger();
        var durations = new ConcurrentLinkedQueue<Long>();
        var begin = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(concurrency)) {
            var pending = new ArrayList<Future<?>>();
            for (int thread = 0; thread < concurrency; thread++) {
                int count = requests / concurrency;
                pending.add(executor.submit(() -> {
                    await(begin);
                    for (int i = 0; i < count; i++) {
                        maxWaiters.accumulateAndGet(pool.getThreadsAwaitingConnection(), Math::max);
                        long start = System.nanoTime();
                        TakenLectureInventory result = operation.get();
                        long elapsed = System.nanoTime() - start;
                        assertThat(elapsed).as("local request stop criterion").isLessThan(Duration.ofSeconds(2).toNanos());
                        durations.add(elapsed);
                        assertThat(canonical(result)).isEqualTo(expected);
                    }
                }));
            }
            begin.countDown();
            for (var future : pending) future.get(30, TimeUnit.SECONDS);
        }
        long[] sorted = durations.stream().mapToLong(Long::longValue).sorted().toArray();
        assertThat(sorted).hasSize(requests);
        var result = new LinkedHashMap<String, Object>();
        result.put("arm", arm); result.put("courses", courses); result.put("concurrency", concurrency); result.put("repeat", repeat);
        result.put("requests", requests); result.put("errors", 0); result.put("mismatches", 0);
        result.put("p50Ms", sorted[sorted.length / 2] / 1e6);
        result.put("p95Ms", sorted[Math.max(0, (int) Math.ceil(sorted.length * 0.95) - 1)] / 1e6);
        result.put("maxMs", sorted[sorted.length - 1] / 1e6);
        result.put("sqlPerRequest", (statistics.getPrepareStatementCount() - sqlBefore) / (double) requests);
        result.put("sampledMaxDbWaiters", maxWaiters.get());
        long acquired = acquire == null ? 0 : acquire.count() - acquireCount;
        result.put("meanDbAcquireMs", acquired == 0 ? 0 : (acquire.totalTime(TimeUnit.NANOSECONDS) - acquireTime) / acquired / 1e6);
        var counterDelta = counters(registry);
        counterDelta.replaceAll((key, value) -> value - countersBefore.getOrDefault(key, 0.0));
        result.put("cacheCounters", counterDelta);
        if (arm.equals("revision_warm") || arm.equals("redis_v3")) {
            assertThat(result).as("one versioned context query per warm read").containsEntry("sqlPerRequest", 1.0);
        }
        if (arm.equals("revision_warm")) {
            assertThat(counterDelta).containsEntry("local_hit", (double) requests);
        }
        result.put("processCpuMs", (os.getProcessCpuTime() - cpuBefore) / 1e6);
        result.put("heapUsedDeltaBytes", memory.getHeapMemoryUsage().getUsed() - heapBefore);
        System.out.printf(Locale.ROOT, "DEV121 benchmark: %s size=%d concurrency=%d round=%d p95=%.3fms sql/request=%.2f%n",
            arm, courses, concurrency, repeat, result.get("p95Ms"), result.get("sqlPerRequest"));
        return result;
    }

    static Map<String, Double> counters(SimpleMeterRegistry registry) {
        var result = new TreeMap<String, Double>();
        registry.find("takenlecture.cache").counters().forEach(counter ->
            result.put(counter.getId().getTag("outcome"), counter.count()));
        return result;
    }

    static long redisMemory(StringRedisTemplate redis, String key) {
        return redis.execute(new org.springframework.data.redis.core.script.DefaultRedisScript<>(
            "return redis.call('MEMORY', 'USAGE', KEYS[1])", Long.class), List.of(key));
    }

    static Map<String, Object> measureMixed(int courses, int concurrency, int repeat) throws Exception {
        Long id = seedStudent(courses);
        var reader = context.getBean(FindTakenLectureUseCase.class);
        var writer = context.getBean(com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.GenerateCustomizedTakenLectureUseCase.class);
        var delete = context.getBean(com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase.class);
        var sql = new org.springframework.jdbc.core.JdbcTemplate(context.getBean(HikariDataSource.class));
        reader.findTakenLectures(id);
        var baseIds = new HashSet<String>();
        for (int i = 0; i < courses; i++) baseIds.add("SYN-BENCH-" + i);
        var begin = new CountDownLatch(1);
        var reads = new ConcurrentLinkedQueue<Long>();
        var writes = new ConcurrentLinkedQueue<Long>();
        try (var executor = Executors.newFixedThreadPool(concurrency + 1)) {
            var pending = new ArrayList<Future<?>>();
            pending.add(executor.submit(() -> {
                await(begin);
                for (int i = 0; i < 4; i++) {
                    long start = System.nanoTime();
                    writer.generateCustomizedTakenLecture(id, ADDED);
                    writes.add(System.nanoTime() - start);
                    Long row = sql.queryForObject("select id from taken_lecture where user_id=? and lecture_id=?", Long.class, id, ADDED);
                    start = System.nanoTime();
                    delete.deleteTakenLecture(id, row);
                    writes.add(System.nanoTime() - start);
                }
            }));
            for (int t = 0; t < concurrency; t++) pending.add(executor.submit(() -> {
                await(begin);
                for (int i = 0; i < 32 / concurrency; i++) {
                    long start = System.nanoTime();
                    var inventory = reader.findTakenLectures(id);
                    reads.add(System.nanoTime() - start);
                    var ids = new HashSet<>(inventory.getTakenLectures().stream().map(v -> v.getLecture().getId()).toList());
                    boolean added = ids.remove(ADDED);
                    assertThat(ids).isEqualTo(baseIds);
                    assertThat(inventory.calculateTotalCredit()).isEqualTo((courses + (added ? 1 : 0)) * 3);
                }
            }));
            begin.countDown();
            for (var future : pending) future.get(30, TimeUnit.SECONDS);
        }
        assertThat(reader.findTakenLectures(id).calculateTotalCredit()).isEqualTo(courses * 3);
        assertThat(sql.queryForObject("select taken_lecture_revision from user where id=?", Long.class, id)).isEqualTo(8);
        assertThat(sql.queryForObject("select sum(taken_credit) from completed_credit where user_id=?", Double.class, id)).isEqualTo(courses * 3.0);
        long[] readTimes = reads.stream().mapToLong(Long::longValue).sorted().toArray();
        long[] writeTimes = writes.stream().mapToLong(Long::longValue).sorted().toArray();
        assertThat(readTimes).hasSize(32);
        assertThat(writeTimes).hasSize(8);
        assertThat(Math.max(readTimes[31], writeTimes[7])).isLessThan(Duration.ofSeconds(2).toNanos());
        return Map.of("arm", "mixed_read_write", "courses", courses, "concurrency", concurrency,
            "repeat", repeat, "requests", 40, "errors", 0, "mismatches", 0,
            "readP95Ms", readTimes[30] / 1e6, "writeP95Ms", writeTimes[7] / 1e6,
            "maxMs", Math.max(readTimes[31], writeTimes[7]) / 1e6);
    }

    static String canonical(TakenLectureInventory inventory) {
        return inventory.getTakenLectures().stream().map(t -> List.of(t.getId(), t.getUser().getId(), t.getLecture().getId(),
            t.getLecture().getName(), t.getLecture().getCredit(), t.getLecture().getIsRevoked(),
            Objects.toString(t.getLecture().getDuplicateCode(), ""), t.getYear(), Objects.toString(t.getSemester(), ""),
            Objects.toString(t.getCreatedAt(), "")).toString()).sorted().collect(java.util.stream.Collectors.joining(";"));
    }
}
