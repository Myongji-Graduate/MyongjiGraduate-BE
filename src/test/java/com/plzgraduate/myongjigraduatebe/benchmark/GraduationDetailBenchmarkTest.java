package com.plzgraduate.myongjigraduatebe.benchmark;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import com.plzgraduate.myongjigraduatebe.core.config.RedisCacheConfig;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("benchmark")
@TestPropertySource(locations = "classpath:application-benchmark.properties")
@Import(GraduationDetailBenchmarkTest.BenchmarkConfiguration.class)
@EnabledIfEnvironmentVariable(named = "GRADUATION_BENCHMARK", matches = "true")
class GraduationDetailBenchmarkTest {
    private static final GenericContainer<?> REDIS = new GenericContainer<>("redis:latest")
        .withExposedPorts(6379);
    private static final String MAJOR = "영어영문학전공";
    private static final List<String> CATEGORIES = List.of("COMMON_CULTURE", "CORE_CULTURE",
        "PRIMARY_MANDATORY_MAJOR", "PRIMARY_ELECTIVE_MAJOR", "PRIMARY_BASIC_ACADEMICAL_CULTURE");

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        REDIS.start();
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @AfterAll
    static void stopRedis() {
        REDIS.stop();
    }

    @Autowired EntityManager entityManager;
    @Autowired PlatformTransactionManager transactionManager;
    @Autowired TokenProvider tokenProvider;
    @Autowired TestRestTemplate http;
    @Autowired ObjectMapper mapper;
    @Autowired SwitchableCacheManager caches;
    @Autowired QueryCounter queries;
    @Autowired StringRedisTemplate redis;
    @LocalServerPort int port;

    @Test
    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    void measureCurrentPersonalCacheAgainstNoCache() throws Exception {
        Path output = Path.of("build/reports/benchmarks/graduation", Instant.now().toString().replace(':', '-'));
        Files.createDirectories(output);
        Path profilesFile = Files.createTempFile("graduation-synthetic-profiles-", ".json");
        Path script = Path.of("src/test/resources/benchmark/graduation-detail.js").toAbsolutePath();
        try {
            assertThat(String.valueOf(entityManager.getEntityManagerFactory().getProperties().get("hibernate.show_sql")))
                .as("SQL console logging must be disabled during measurement").isEqualTo("false");
            List<Long> userIds = seed();
            caches.enabled = false;
            List<Map<String, Object>> profiles = new ArrayList<>();
            for (Long userId : userIds) {
                String token = tokenProvider.generateToken(userId);
                Map<String, JsonNode> expected = new LinkedHashMap<>();
                for (String category : CATEGORIES) expected.put(category, request(token, category));
                profiles.add(Map.of("token", token, "expected", expected));
            }
            mapper.writeValue(profilesFile.toFile(), profiles);

            Map<String, Object> report = new LinkedHashMap<>();
            report.put("timestamp", Instant.now().toString());
            report.put("commit", commandOutput("git", "rev-parse", "HEAD"));
            report.put("branch", commandOutput("git", "branch", "--show-current"));
            report.put("dataset", Map.of("users", 20, "lectures", 160, "takenPerUser", 47,
                "major", MAJOR, "entryYear", 20, "studentType", "NORMAL", "source", "synthetic"));
            report.put("environment", Map.of("java", System.getProperty("java.version"),
                "mysql", "8.0.29", "redis", REDIS.getDockerImageName(), "poolSize", 10,
                "vus", 100, "iterationsPerVu", 5, "application", "SpringBootTest with JaCoCo",
                "osArch", System.getProperty("os.arch"), "k6", commandOutput("k6", "version"),
                "sqlConsoleLogging", false));

            caches.enabled = true;
            report.put("sequentialSql", probeQueries(profiles));
            List<Map<String, Object>> runs = new ArrayList<>();
            // Balanced ordering reduces the effect of JVM warm-up and run order.
            boolean[] modes = {false, true, true, false, false, true};
            for (int round = 0; round < modes.length; round++) {
                caches.clear();
                caches.enabled = modes[round];
                String label = (round + 1) + (modes[round] ? "-personal-redis" : "-no-cache");
                runK6(script, profilesFile, output, label + "-warmup");
                Map<String, Long> redisBefore = redisStatistics();
                queries.reset();
                Path summary = runK6(script, profilesFile, output, label);
                Map<String, Long> sql = queries.snapshot();
                Map<String, Long> redisAfter = redisStatistics();
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("label", label);
                result.put("personalCache", modes[round]);
                result.put("summaryFile", summary.getFileName().toString());
                result.put("sql", sql);
                result.put("sqlTotal", sql.values().stream().mapToLong(Long::longValue).sum());
                result.put("redisHits", redisAfter.get("keyspace_hits") - redisBefore.get("keyspace_hits"));
                result.put("redisMisses", redisAfter.get("keyspace_misses") - redisBefore.get("keyspace_misses"));
                result.put("metrics", mapper.readTree(summary.toFile()).get("metrics"));
                runs.add(result);
                report.put("runs", runs);
                mapper.writerWithDefaultPrettyPrinter().writeValue(output.resolve("report.json").toFile(), report);
                System.out.println("BENCHMARK " + label + " SQL=" + result.get("sqlTotal"));
            }
            assertThat(runs).hasSize(6);
            System.out.println("BENCHMARK_REPORT=" + output.toAbsolutePath());
        } finally {
            Files.deleteIfExists(profilesFile);
        }
    }

    private List<Long> seed() {
        return new TransactionTemplate(transactionManager).execute(status -> {
            List<LectureJpaEntity> lectures = new ArrayList<>();
            CommonCultureCategory[] common = {CommonCultureCategory.CHRISTIAN_B,
                CommonCultureCategory.EXPRESSION, CommonCultureCategory.ENGLISH, CommonCultureCategory.CAREER};
            for (int i = 0; i < 160; i++) {
                LectureJpaEntity lecture = LectureJpaEntity.builder().id("BENCH%03d".formatted(i))
                    .name("Synthetic lecture " + i).credit(3).isRevoked(0).build();
                entityManager.persist(lecture);
                lectures.add(lecture);
                if (i < 20) {
                    entityManager.persist(CommonCultureJpaEntity.builder().lectureJpaEntity(lecture)
                        .commonCultureCategory(common[i % common.length]).startEntryYear(20).endEntryYear(22).build());
                } else if (i < 60) {
                    entityManager.persist(CoreCultureJpaEntity.builder().lectureJpaEntity(lecture)
                        .coreCultureCategory(CoreCultureCategory.values()[i % 4]).startEntryYear(20).endEntryYear(22).build());
                } else if (i < 80) {
                    entityManager.persist(BasicAcademicalCultureLectureJpaEntity.builder().lectureJpaEntity(lecture)
                        .college(College.HUMANITIES.getName()).build());
                } else {
                    entityManager.persist(MajorLectureJpaEntity.builder().lectureJpaEntity(lecture)
                        .major(MAJOR).mandatory(i < 100 ? 1 : 0).startEntryYear(20).endEntryYear(22).build());
                }
            }
            List<Long> users = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                UserJpaEntity user = UserJpaEntity.builder().authId("benchmark-" + i)
                    .password("not-a-login-credential").name("Synthetic student " + i)
                    .studentNumber("6020%04d".formatted(9000 + i)).entryYear(20).major(MAJOR)
                    .englishLevel(EnglishLevel.ENG12).studentCategory(StudentCategory.NORMAL)
                    .transferCredit("0/0/0/0").exchangeCredit("0/0/0/0/0/0/0/0/0")
                    .completedSemesterCount(4).totalCredit(134).takenCredit(141).build();
                entityManager.persist(user);
                users.add(user.getId());
                for (int j = 0; j < 47; j++) {
                    entityManager.persist(TakenLectureJpaEntity.builder().user(user)
                        .lecture(lectures.get((i * 3 + j * 3) % lectures.size()))
                        .year(2022).semester(Semester.FIRST).build());
                }
            }
            entityManager.flush();
            entityManager.clear();
            return users;
        });
    }

    private List<Map<String, Object>> probeQueries(List<Map<String, Object>> profiles) {
        List<Map<String, Object>> probes = new ArrayList<>();
        for (String category : CATEGORIES) {
            caches.clear();
            for (int attempt = 0; attempt < 3; attempt++) {
                queries.reset();
                request((String) profiles.get(attempt == 2 ? 1 : 0).get("token"), category);
                probes.add(Map.of("category", category, "phase", List.of("cold", "same-user-warm", "other-user").get(attempt),
                    "sql", queries.snapshot()));
            }
        }
        return probes;
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

    private Path runK6(Path script, Path profiles, Path output, String label) throws Exception {
        Path summary = output.resolve(label + ".json").toAbsolutePath();
        ProcessBuilder builder = new ProcessBuilder("k6", "run", "--quiet", script.toString());
        builder.environment().put("BASE_URL", "http://127.0.0.1:" + port);
        builder.environment().put("PROFILES_FILE", profiles.toString());
        builder.environment().put("SUMMARY_FILE", summary.toString());
        builder.redirectErrorStream(true).redirectOutput(output.resolve(label + ".log").toFile());
        Process process = builder.start();
        try {
            assertThat(process.waitFor(150, TimeUnit.SECONDS)).as("k6 timed out: %s", label).isTrue();
            assertThat(process.exitValue()).as("k6 failed; see %s", output.resolve(label + ".log")).isZero();
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
            }
        }
        assertThat(Files.exists(summary)).isTrue();
        return summary;
    }

    private Map<String, Long> redisStatistics() {
        return redis.execute((org.springframework.data.redis.core.RedisCallback<Map<String, Long>>) connection -> {
            var info = connection.serverCommands().info("stats");
            return Map.of("keyspace_hits", Long.parseLong(info.getProperty("keyspace_hits", "0")),
                "keyspace_misses", Long.parseLong(info.getProperty("keyspace_misses", "0")));
        });
    }

    private String commandOutput(String... command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder(command).start();
        assertThat(process.waitFor(10, TimeUnit.SECONDS)).isTrue();
        assertThat(process.exitValue()).isZero();
        return new String(process.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).strip();
    }

    @TestConfiguration
    static class BenchmarkConfiguration {
        @Bean
        @Primary
        SwitchableCacheManager benchmarkCacheManager(RedisConnectionFactory factory) {
            var manager = new RedisCacheConfig().cacheManager(factory);
            manager.afterPropertiesSet();
            return new SwitchableCacheManager(manager);
        }

        @Bean
        QueryCounter queryCounter() {
            return new QueryCounter();
        }

        @Bean
        HibernatePropertiesCustomizer queryCounting(QueryCounter counter) {
            return properties -> properties.put("hibernate.session_factory.statement_inspector", counter);
        }
    }

    static class SwitchableCacheManager implements CacheManager {
        private final CacheManager delegate;
        private final Cache disabled = new NoOpCache("takenLectures");
        volatile boolean enabled;

        SwitchableCacheManager(CacheManager delegate) {
            this.delegate = delegate;
        }

        @Override
        public Cache getCache(String name) {
            return enabled ? delegate.getCache(name) : disabled;
        }

        @Override
        public Collection<String> getCacheNames() {
            return delegate.getCacheNames();
        }

        void clear() {
            delegate.getCache("takenLectures").clear();
        }
    }

    static class QueryCounter implements StatementInspector {
        private final Map<String, LongAdder> counts = new ConcurrentHashMap<>();

        @Override
        public String inspect(String sql) {
            counts.computeIfAbsent(sql, ignored -> new LongAdder()).increment();
            return sql;
        }

        void reset() {
            counts.clear();
        }

        Map<String, Long> snapshot() {
            Map<String, Long> result = new TreeMap<>();
            counts.forEach((sql, count) -> result.put(sql, count.sum()));
            return result;
        }
    }
}
