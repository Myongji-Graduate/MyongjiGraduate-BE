package com.plzgraduate.myongjigraduatebe.benchmark;

import static org.assertj.core.api.Assertions.assertThat;
import static com.plzgraduate.myongjigraduatebe.benchmark.RepositoryBenchmarkDataset.CATEGORIES;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import com.plzgraduate.myongjigraduatebe.benchmark.GraduationDetailBenchmarkTest.QueryCounter;
import com.plzgraduate.myongjigraduatebe.benchmark.GraduationDetailBenchmarkTest.SwitchableCacheManager;
import com.plzgraduate.myongjigraduatebe.benchmark.SharedMasterCache.Mode;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.FindOptionalMandatoryPolicyPort;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import jakarta.persistence.EntityManager;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("benchmark")
@TestPropertySource(locations = "classpath:application-benchmark.properties")
@Import({GraduationDetailBenchmarkTest.BenchmarkConfiguration.class, SharedMasterCacheConfiguration.class})
@EnabledIfEnvironmentVariable(named = "SHARED_CACHE_BENCHMARK", matches = "true")
class SharedMasterCacheBenchmarkTest {
    private static final GenericContainer<?> REDIS = new GenericContainer<>("redis:latest").withExposedPorts(6379);

    @DynamicPropertySource
    static void isolatedDatabases(DynamicPropertyRegistry registry) {
        REDIS.start();
        registry.add("spring.datasource.url", () -> "jdbc:tc:mysql:8.0.29:///shared_cache_benchmark");
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @AfterAll
    static void stopRedis() { REDIS.stop(); }

    @Autowired EntityManager em;
    @Autowired PlatformTransactionManager transactions;
    @Autowired TokenProvider tokens;
    @Autowired TestRestTemplate http;
    @Autowired ObjectMapper mapper;
    @Autowired SwitchableCacheManager personal;
    @Autowired SharedMasterCache shared;
    @Autowired QueryCounter queries;
    @Autowired StringRedisTemplate redis;
    @Autowired FindOptionalMandatoryPolicyPort policies;
    @Autowired FindTakenLectureUseCase takenLectures;
    @LocalServerPort int port;

    @Test
    @Timeout(value = 15, unit = TimeUnit.MINUTES)
    void compareSharedCachesThroughActualGraduationHttpApi() throws Exception {
        String workload = System.getenv().getOrDefault("SHARED_CACHE_WORKLOAD", "steady");
        assertThat(workload).isIn("steady", "burst");
        boolean steady = workload.equals("steady");
        assertThat(String.valueOf(em.getEntityManagerFactory().getProperties().get("hibernate.show_sql"))).isEqualTo("false");
        Path output = Path.of("build/reports/benchmarks/shared-master", Instant.now().toString().replace(':', '-'));
        Files.createDirectories(output);
        Path profilesFile = Files.createTempFile("shared-cache-synthetic-", ".json");
        try {
            var dataset = RepositoryBenchmarkDataset.seed(em, transactions);
            personal.enabled = false;
            List<Map<String, Object>> profiles = new ArrayList<>();
            for (Long id : dataset.users()) {
                String token = tokens.generateToken(id);
                Map<String, JsonNode> expected = new LinkedHashMap<>();
                for (String category : CATEGORIES) expected.put(category, request(token, category));
                profiles.add(Map.of("token", token, "expected", expected));
            }
            mapper.writeValue(profilesFile.toFile(), profiles);
            personal.enabled = true;
            Map<String, Object> report = new LinkedHashMap<>();
            report.put("commit", command("git", "rev-parse", "HEAD"));
            report.put("branch", command("git", "branch", "--show-current"));
            report.put("workload", workload);
            report.put("dataset", dataset.metadata());
            report.put("environment", Map.of("java", System.getProperty("java.version"),
                "arch", System.getProperty("os.arch"), "k6", command("k6", "version"),
                "mysql", "8.0.29", "redisImageId", REDIS.getContainerInfo().getImageId(),
                "poolSize", 10, "rate", 200, "durationSeconds", 20, "maxVus", 100,
                "instrumentation", "JaCoCo; Hibernate StatementInspector; SQL logging disabled"));

            // Exercise every implementation before measuring either cold or warm caches.
            for (Mode mode : Mode.values()) {
                reset(mode);
                k6(profilesFile, output, "prime-" + mode, false);
            }
            List<Map<String, Object>> cold = new ArrayList<>();
            for (Mode mode : Mode.values()) {
                reset(mode);
                cold.add(measure(profilesFile, output, "cold-" + mode, false));
            }
            report.put("coldRuns", cold);
            writeReport(output, report);
            List<Map<String, Object>> runs = new ArrayList<>();
            report.put(steady ? "steadyRuns" : "warmBurstRuns", runs);
            // Latin-square ordering gives each mode each position once.
            Mode[][] orders = {{Mode.PERSONAL_ONLY, Mode.SHARED_REDIS, Mode.SHARED_CAFFEINE},
                {Mode.SHARED_REDIS, Mode.SHARED_CAFFEINE, Mode.PERSONAL_ONLY},
                {Mode.SHARED_CAFFEINE, Mode.PERSONAL_ONLY, Mode.SHARED_REDIS}};
            int number = 0;
            for (Mode[] order : orders) {
                for (Mode mode : order) {
                    reset(mode);
                    String label = String.format("%02d-%s", ++number, mode);
                    k6(profilesFile, output, label + "-warmup", false);
                    runs.add(measure(profilesFile, output, label, steady));
                    writeReport(output, report);
                }
            }
            report.put("correctness", verifyUpdates(dataset));
            report.put("complete", true);
            writeReport(output, report);
            assertThat(runs).hasSize(9);
            System.out.println("SHARED_CACHE_REPORT=" + output.toAbsolutePath());
        } finally {
            Files.deleteIfExists(profilesFile);
        }
    }

    private void reset(Mode mode) {
        personal.clear();
        shared.reset(mode);
        queries.reset();
    }

    private Map<String, Object> measure(Path profiles, Path output, String label, boolean steady) throws Exception {
        shared.resetCounters();
        queries.reset();
        long cpuBefore = processCpuNanos();
        long gcBefore = gcCount();
        long started = System.nanoTime();
        Path summary = k6(profiles, output, label, steady);
        long elapsed = System.nanoTime() - started;
        long cpu = processCpuNanos() - cpuBefore;
        Map<String, Long> sql = queries.snapshot();
        JsonNode metrics = mapper.readTree(summary.toFile()).path("metrics");
        long requests = metrics.path("http_reqs").path("values").path("count").asLong();
        long sqlTotal = sql.values().stream().mapToLong(Long::longValue).sum();
        if (!label.startsWith("cold-") && shared.mode != Mode.PERSONAL_ONLY) {
            assertThat(shared.counters().get("loads")).as("warm master loads").isZero();
            assertThat(sqlTotal).as("warm shared mode should only query users").isEqualTo(requests);
        }
        Map<String, Object> run = new LinkedHashMap<>();
        run.put("label", label);
        run.put("mode", shared.mode);
        run.put("metrics", metrics);
        run.put("sql", sql);
        run.put("sqlTotal", sqlTotal);
        run.put("masterCache", shared.counters());
        run.put("redisMemory", redisFootprint());
        run.put("caffeineFootprint", shared.localFootprint());
        run.put("processCpuMillis", cpu / 1_000_000.0);
        run.put("elapsedMillis", elapsed / 1_000_000.0);
        run.put("gcCollections", gcCount() - gcBefore);
        run.put("heapUsedAfterBytes", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
        System.out.println("SHARED_CACHE " + label + " avg=" + metrics.path("graduation_duration").path("values").path("avg")
            + " p95=" + metrics.path("graduation_duration").path("values").path("p(95)") + " SQL=" + sqlTotal);
        return run;
    }

    private List<Map<String, Object>> verifyUpdates(RepositoryBenchmarkDataset.Dataset dataset) {
        List<Map<String, Object>> checks = new ArrayList<>();
        TransactionTemplate tx = new TransactionTemplate(transactions);
        for (Mode mode : List.of(Mode.SHARED_REDIS, Mode.SHARED_CAFFEINE)) {
            reset(mode);
            assertThat(policyCount(tx)).isEqualTo(4);
            tx.executeWithoutResult(status -> {
                updatePolicy(3);
                shared.publishAfterCommit();
                assertThat(policyCount(tx)).as("old published policy before commit").isEqualTo(4);
            });
            assertThat(policyCount(tx)).as("new policy after commit").isEqualTo(3);
            tx.executeWithoutResult(status -> {
                updatePolicy(2);
                shared.publishAfterCommit();
                status.setRollbackOnly();
            });
            assertThat(policyCount(tx)).as("rollback preserves cache").isEqualTo(3);
            Integer stored = tx.execute(status -> ((Number) em.createNativeQuery(
                "select required_count from optional_mandatory_policy where policy_key='benchmark-trade'")
                .getSingleResult()).intValue());
            assertThat(stored).isEqualTo(3);
            tx.executeWithoutResult(status -> { updatePolicy(4); shared.publishAfterCommit(); });
            assertThat(policyCount(tx)).isEqualTo(4);

            Long id = dataset.users().getFirst();
            Long other = dataset.users().get(1);
            var original = List.copyOf(takenLectures.findTakenLectures(id).getTakenLectures());
            assertThat(original).hasSize(30);
            assertThat(takenLectures.findTakenLectures(other).getTakenLectures()).hasSize(30);
            tx.executeWithoutResult(status -> {
                em.createNativeQuery("delete from taken_lecture where user_id=:user")
                    .setParameter("user", id).executeUpdate();
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() { personal.getCache("takenLectures").evict(id); }
                });
            });
            assertThat(takenLectures.findTakenLectures(id).getTakenLectures()).isEmpty();
            assertThat(takenLectures.findTakenLectures(other).getTakenLectures()).hasSize(30);
            String token = tokens.generateToken(id);
            Map<String, String> cached = new TreeMap<>();
            for (String category : CATEGORIES) cached.put(category, canonical(request(token, category)));
            shared.mode = Mode.PERSONAL_ONLY;
            personal.enabled = false;
            try {
                for (String category : CATEGORIES) assertThat(canonical(request(token, category))).isEqualTo(cached.get(category));
            } finally {
                personal.enabled = true;
                shared.mode = mode;
            }
            tx.executeWithoutResult(status -> original.forEach(taken -> em.persist(TakenLectureJpaEntity.builder()
                .user(em.getReference(UserJpaEntity.class, id))
                .lecture(em.getReference(LectureJpaEntity.class, taken.getLecture().getId()))
                .year(taken.getYear()).semester(taken.getSemester()).build())));
            personal.getCache("takenLectures").evict(id);
            assertThat(takenLectures.findTakenLectures(id).getTakenLectures()).hasSize(30);
            checks.add(Map.of("mode", mode, "policyCommit", true, "policyRollback", true,
                "personalCommitEviction", true, "otherStudentUnchanged", true,
                "updatedHttpEqualsUncached", true,
                "scope", "test-only publication/eviction hooks; not existing operational update paths"));
        }
        return checks;
    }

    private int policyCount(TransactionTemplate tx) {
        return tx.execute(status -> policies.findActivePolicies(RepositoryBenchmarkDataset.TRADE, 20, MajorType.PRIMARY)
            .getFirst().getRequiredCount());
    }

    private void updatePolicy(int count) {
        em.createNativeQuery("update optional_mandatory_policy set required_count=:count where policy_key='benchmark-trade'")
            .setParameter("count", count).executeUpdate();
    }

    private JsonNode request(String token, String category) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var response = http.exchange("/api/v1/graduations/detail?graduationCategory=" + category,
            HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
        assertThat(response.getStatusCode().value()).as("category %s: %s", category, response.getBody()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }

    private String canonical(JsonNode node) {
        return normalized(node).toString();
    }

    private JsonNode normalized(JsonNode node) {
        if (node.isArray()) {
            List<JsonNode> values = new ArrayList<>();
            node.forEach(value -> values.add(normalized(value)));
            values.sort(java.util.Comparator.comparing(JsonNode::toString));
            return mapper.createArrayNode().addAll(values);
        }
        if (node.isObject()) {
            Map<String, JsonNode> values = new TreeMap<>();
            node.fields().forEachRemaining(field -> values.put(field.getKey(), normalized(field.getValue())));
            return mapper.createObjectNode().setAll(values);
        }
        return node;
    }

    private Path k6(Path profiles, Path output, String label, boolean steady) throws Exception {
        Path summary = output.resolve(label + ".json").toAbsolutePath();
        ProcessBuilder builder = new ProcessBuilder("k6", "run", "--quiet",
            Path.of("src/test/resources/benchmark/shared-master.js").toAbsolutePath().toString());
        builder.environment().putAll(Map.of("BASE_URL", "http://127.0.0.1:" + port,
            "PROFILES_FILE", profiles.toString(), "SUMMARY_FILE", summary.toString(),
            "WORKLOAD", steady ? "steady" : "burst", "RATE", "200",
            "DURATION_SECONDS", "20", "ITERATIONS", "800"));
        builder.redirectErrorStream(true).redirectOutput(output.resolve(label + ".log").toFile());
        Process process = builder.start();
        try {
            assertThat(process.waitFor(150, TimeUnit.SECONDS)).as("k6 timeout: %s", label).isTrue();
            assertThat(process.exitValue()).as("See %s", output.resolve(label + ".log")).isZero();
        } finally {
            if (process.isAlive()) { process.destroyForcibly(); process.waitFor(5, TimeUnit.SECONDS); }
        }
        return summary;
    }

    private Map<String, Long> redisFootprint() {
        return redis.execute((RedisCallback<Map<String, Long>>) connection -> {
            Map<String, Long> result = new TreeMap<>();
            for (String cache : List.of("benchmarkMaster", "takenLectures")) {
                long count = 0;
                long bytes = 0;
                try (var keys = connection.keyCommands().scan(ScanOptions.scanOptions().match(cache + "::*").count(128).build())) {
                    while (keys.hasNext()) {
                        byte[] key = keys.next();
                        Number memory = connection.scriptingCommands().eval(
                            "return redis.call('MEMORY','USAGE',KEYS[1])".getBytes(StandardCharsets.UTF_8),
                            ReturnType.INTEGER, 1, key);
                        if (memory != null) bytes += memory.longValue();
                        count++;
                    }
                }
                result.put(cache + "Keys", count);
                result.put(cache + "AllocatedBytes", bytes);
            }
            return result;
        });
    }

    private static long processCpuNanos() {
        return ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuTime();
    }

    private static long gcCount() {
        return ManagementFactory.getGarbageCollectorMXBeans().stream().mapToLong(bean -> Math.max(0, bean.getCollectionCount())).sum();
    }

    private String command(String... arguments) throws Exception {
        Process process = new ProcessBuilder(arguments).redirectErrorStream(true).start();
        try {
            assertThat(process.waitFor(10, TimeUnit.SECONDS)).isTrue();
            assertThat(process.exitValue()).isZero();
            return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).strip();
        } finally {
            if (process.isAlive()) { process.destroyForcibly(); process.waitFor(5, TimeUnit.SECONDS); }
        }
    }

    private void writeReport(Path output, Map<String, Object> report) throws Exception {
        mapper.writerWithDefaultPrettyPrinter().writeValue(output.resolve("report.json").toFile(), report);
    }
}
