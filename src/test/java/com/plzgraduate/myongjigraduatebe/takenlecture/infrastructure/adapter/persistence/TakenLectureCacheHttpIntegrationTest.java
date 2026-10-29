package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.zaxxer.hikari.HikariDataSource;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Actual production JAR, authenticated loopback HTTP, synthetic schema and dedicated MySQL/Redis only. */
@Testcontainers
@EnabledIfEnvironmentVariable(named = "RUN_TAKEN_LECTURE_CACHE_HTTP", matches = "true")
class TakenLectureCacheHttpIntegrationTest {
    private static final String DATABASE = "synthetic_cache_http";
    private static final String PASSWORD = UUID.randomUUID().toString();
    private static final String JWT_SECRET = UUID.randomUUID().toString();
    private static final String MAJOR = "영어영문학전공";
    private static final String BASE = "SYNHTTP-BASE";
    private static final String ADDED = "SYNHTTP-ADD";
    private static final String LIST = "/api/v1/taken-lectures";
    private static final String CREDITS = "/api/v1/graduations/credits";
    private static final List<String> ENDPOINTS = List.of(LIST, CREDITS,
        "/api/v1/graduations/detail?graduationCategory=COMMON_CULTURE",
        "/api/v1/graduations/detail?graduationCategory=CORE_CULTURE",
        "/api/v1/graduations/detail?graduationCategory=PRIMARY_MANDATORY_MAJOR");
    private static final boolean STEADY = Boolean.parseBoolean(System.getenv("DEV121_HTTP_STEADY"));
    private static final int REQUESTS = STEADY ? 384 : 96;
    private static final List<String> PERFORMANCE_ENDPOINTS = STEADY ? List.of(CREDITS) : ENDPOINTS;
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final List<App> APPS = new ArrayList<>();
    @Container
    static final GenericContainer<?> MYSQL = container("mysql:8.4.8", 3306)
        .withEnv("MYSQL_DATABASE", DATABASE).withEnv("MYSQL_USER", "http_app")
        .withEnv("MYSQL_PASSWORD", PASSWORD).withEnv("MYSQL_ROOT_PASSWORD", PASSWORD)
        .waitingFor(Wait.forLogMessage(".*ready for connections.*port: 3306.*\\n", 1))
        .withStartupTimeout(Duration.ofMinutes(2));
    @Container
    static final GenericContainer<?> REDIS = container("redis:7.4.11-alpine", 6379)
        .withCommand("redis-server", "--save", "", "--appendonly", "no");
    private final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(3)).build();
    private final List<Student> students = new ArrayList<>();
    private final Map<String, JsonNode> expected = new HashMap<>();
    private final List<Map<String, Object>> probes = new ArrayList<>();
    private final List<Map<String, Object>> measurements = new ArrayList<>();
    private final List<Map<String, Object>> checks = new ArrayList<>();
    private final Map<String, Object> report = new LinkedHashMap<>();
    private final LongAdder httpRequests = new LongAdder();
    private JdbcTemplate sql;
    private static HikariDataSource probeDataSource;
    private Path output;
    private Path jar;
    private String variant = "optimized";
    private Student functional;
    private Student faultStudent;

    private record Student(long id, int size, String authId) {}
    private record Reply(int status, JsonNode body, long nanos, int bytes) {}

    @Test
    @Timeout(value = 15, unit = TimeUnit.MINUTES)
    void compareModesThroughAuthenticatedProductionJar() throws Exception {
        output = Path.of("build/dev121/http", Instant.now().toString().replace(':', '-'));
        Files.createDirectories(output);
        jar = Path.of("build/libs/MyongjiGraduate-BE-0.0.1-SNAPSHOT.jar").toAbsolutePath();
        assertThat(jar).as("Run bootJar before this opt-in test").exists();
        var variants = new LinkedHashMap<String, Path>();
        String baseline = System.getenv("DEV121_HTTP_BASELINE_JAR");
        if (baseline != null && !baseline.isBlank()) {
            Path previous = Path.of(baseline).toAbsolutePath();
            assertThat(previous).isRegularFile();
            variants.put("previous", previous);
        }
        variants.put("optimized", jar);
        var jarHashes = new LinkedHashMap<String, String>();
        for (var entry : variants.entrySet()) jarHashes.put(entry.getKey(), hash(Files.readAllBytes(entry.getValue())));
        prepareSyntheticDatabase();
        report.put("startedAt", Instant.now().toString());
        report.put("branch", command("git", "branch", "--show-current"));
        report.put("head", command("git", "rev-parse", "HEAD"));
        report.put("jarSha256", jarHashes);
        report.put("variantOrder", "previous/optimized; optimized/previous; previous/optimized");
        report.put("environment", Map.of("mysql", sql.queryForObject("select version()", String.class),
            "mysqlImageId", MYSQL.getContainerInfo().getImageId(),
            "redis", REDIS.execInContainer("redis-server", "--version").getStdout().trim(),
            "redisImageId", REDIS.getContainerInfo().getImageId(),
            "java", System.getProperty("java.version"), "appJvm", "-Xms64m -Xmx256m -XX:ActiveProcessorCount=2",
            "poolSize", 10, "sharedMasterCache", "off", "instrumentation", "external app JVM; no JaCoCo agent"));
        report.put("dataset", Map.of("source", "hand-authored synthetic students, courses and mappings; no snapshot",
            "benchmarkStudents", 12, "functionalStudents", 2, "takenCounts", List.of(30, 60, 120),
            "studentCategory", "NORMAL", "major", MAJOR, "entryYear", 20,
            "limitations", "single major/cohort; synthetic mappings are not certified academic policy"));
        report.put("endpoints", ENDPOINTS);
        report.put("probes", probes);
        report.put("measurements", measurements);
        report.put("checks", checks);
        report.put("runtimeMetricsDefinition", "Prometheus JVM compilation cumulative ms and GC pause cumulative seconds/count; per-batch deltas; labels aggregated");
        report.put("warmups", new ArrayList<Map<String, Object>>());
        report.put("protocol", Map.of("concurrency", List.of(1, 4, 8), "requestsPerBatch", REQUESTS,
            "steady", STEADY, "performanceEndpoints", PERFORMANCE_ENDPOINTS,
            "warmup", STEADY ? "at least 3072 credits GETs and 10 seconds, concurrency 4, then 1500ms quiet" : "5 passes across 12 students and 5 endpoints (60 credits GETs)",
            "rounds", 3, "order", STEADY ? "off/on; on/off; off/on" : "off/shadow/on; shadow/on/off; on/off/shadow",
            "comparison", "all fields; taken-list ordering preserved; other collection ordering normalized"));
        report.put("productionAccessed", false);
        var observerBefore = dbStats();
        var observerAfter = dbStats();
        assertThat(observerAfter.get("Com_select") - observerBefore.get("Com_select"))
            .as("status observation must not add SELECTs").isZero();
        report.put("sqlObservation", "server Com_select; warmed independent observer pool adds zero SELECTs; includes app transaction metadata SELECTs");
        writeReport();
        try {
            // Compare both actual JARs against the first OFF response, including every JSON field.
            for (var entry : variants.entrySet()) {
                variant = entry.getKey(); jar = entry.getValue();
                for (String mode : STEADY ? List.of("off", "on") : List.of("off", "shadow", "on")) {
                    try (App app = start(mode, variant + "-probe-" + mode)) {
                        loginAll(app);
                        if (expected.isEmpty()) captureExpected(app);
                        probeApiCacheSelection(app);
                    }
                }
            }
            List<List<String>> orders = STEADY
                ? List.of(List.of("off", "on"), List.of("on", "off"), List.of("off", "on"))
                : List.of(List.of("off", "shadow", "on"), List.of("shadow", "on", "off"), List.of("on", "off", "shadow"));
            report.put("modeOrder", orders);
            for (int round = 0; round < orders.size(); round++) {
                var variantOrder = new ArrayList<>(variants.keySet());
                if (round % 2 == 1) java.util.Collections.reverse(variantOrder);
                for (String selected : variantOrder) {
                    variant = selected; jar = variants.get(selected);
                    for (String mode : orders.get(round)) {
                        try (App app = start(mode, variant + "-round-" + round + "-" + mode)) {
                            loginAll(app);
                            if (STEADY) steadyWarm(app, round);
                            else for (int warm = 0; warm < 5; warm++) {
                                for (Student student : students) {
                                    if (student.size() > 1) for (String endpoint : ENDPOINTS) verify(app, student, endpoint);
                                }
                            }
                            for (int size : List.of(30, 60, 120)) {
                                for (String endpoint : PERFORMANCE_ENDPOINTS) {
                                    for (int concurrency : List.of(1, 4, 8)) measure(app, round, size, endpoint, concurrency);
                                }
                            }
                            if (round == 0 && variant.equals("optimized") && !STEADY) {
                                mutationAndRollbackChecks(app);
                                concurrentReadersAndWriter(app);
                                redisFaultChecks(app);
                            }
                        }
                    }
                }
            }
            assertThat(measurements).hasSize(orders.size() * orders.getFirst().size() * variants.size()
                * 9 * PERFORMANCE_ENDPOINTS.size());
            report.put("complete", true);
        } finally {
            report.put("httpRequests", httpRequests.sum());
            report.put("finishedAt", Instant.now().toString());
            writeReport();
            System.out.println("DEV121_HTTP_REPORT=" + output.toAbsolutePath().resolve("report.json"));
        }
    }

    private void prepareSyntheticDatabase() throws Exception {
        assertThat(MYSQL.getHost()).isIn("127.0.0.1", "localhost");
        probeDataSource = new HikariDataSource();
        probeDataSource.setJdbcUrl(jdbcUrl());
        probeDataSource.setUsername("root");
        probeDataSource.setPassword(PASSWORD);
        probeDataSource.setPoolName("dev121-http-observer");
        probeDataSource.setMaximumPoolSize(4);
        probeDataSource.setMinimumIdle(4);
        // Pre-open every observer connection so metadata SELECTs never enter a measured batch.
        var connections = new ArrayList<java.sql.Connection>();
        try {
            for (int i = 0; i < 4; i++) connections.add(probeDataSource.getConnection());
        } finally {
            for (var connection : connections) connection.close();
        }
        DataSource root = probeDataSource;
        sql = new JdbcTemplate(root);
        assertThat(sql.queryForObject("select database()", String.class)).isEqualTo(DATABASE);
        assertThat(sql.queryForObject("select count(*) from information_schema.tables where table_schema=database()", Integer.class)).isZero();
        var schema = new LocalContainerEntityManagerFactoryBean();
        schema.setDataSource(root);
        schema.setPackagesToScan("com.plzgraduate.myongjigraduatebe");
        schema.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        schema.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-only", "hibernate.show_sql", "false",
            "hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"));
        schema.afterPropertiesSet();
        schema.destroy();
        sql.execute("alter table user drop column taken_lecture_revision");
        sql.execute("drop table lecture_catalog_revision");
        var flyway = Flyway.configure().dataSource(root).locations("classpath:db/migration")
            .baselineOnMigrate(true).baselineVersion("13").load();
        assertThat(flyway.migrate().migrationsExecuted).isEqualTo(2);
        flyway.validate();
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        seed();
        sql.execute("create table dev121_http_fault_gate (user_id bigint primary key, enabled boolean not null)");
        sql.update("insert into dev121_http_fault_gate values (?,false)", functional.id());
        for (String event : List.of("INSERT", "UPDATE")) {
            sql.execute("CREATE TRIGGER dev121_http_fail_summary_" + event.toLowerCase()
                + " BEFORE " + event + " ON completed_credit FOR EACH ROW BEGIN "
                + "IF EXISTS (SELECT 1 FROM dev121_http_fault_gate WHERE user_id=NEW.user_id AND enabled=true) "
                + "THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='synthetic summary failure'; END IF; END");
        }
        sql.execute("REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'http_app'@'%'");
        sql.execute("GRANT SELECT,INSERT,UPDATE,DELETE ON " + DATABASE + ".* TO 'http_app'@'%'");
        assertThat(sql.queryForObject("select @@transaction_isolation", String.class)).isEqualTo("REPEATABLE-READ");
        assertThat(sql.queryForObject("select count(*) from information_schema.triggers where trigger_schema=database() and trigger_name like 'lecture_catalog_revision_%'", Integer.class)).isEqualTo(3);
    }

    private void seed() {
        for (int i = 0; i < 180; i++) {
            String code = code(i);
            sql.update("insert into lecture(id,name,credit,is_revoked) values (?,?,3,0)", code, "Synthetic HTTP course " + i);
            if (i < 12) {
                String category = List.of("CHRISTIAN_B", "EXPRESSION", "ENGLISH", "CAREER").get(i % 4);
                sql.update("insert into common_culture(lecture_id,common_culture_category,start_entry_year,end_entry_year) values (?,?,20,20)", code, category);
            } else if (i < 44) {
                String category = List.of("HISTORY_PHILOSOPHY", "SOCIETY_COMMUNITY", "CULTURE_ART", "SCIENCE_TECHNOLOGY").get(i % 4);
                sql.update("insert into core_culture(lecture_id,core_culture_category,start_entry_year,end_entry_year) values (?,?,20,20)", code, category);
            } else if (i < 56) {
                sql.update("insert into basic_academical_culture(lecture_id,college) values (?,?)", code, "인문대");
            } else if (i < 120) {
                // 18 mandatory credits must fit inside the domain's 63-credit major requirement.
                sql.update("insert into major(lecture_id,major,mandatory,start_entry_year,end_entry_year) values (?,?,?,20,20)", code, MAJOR, i < 62 ? 1 : 0);
            }
        }
        sql.update("insert into lecture(id,name,credit,is_revoked) values (?, 'Synthetic functional base',3,0),(?,'Synthetic functional add',3,0)", BASE, ADDED);
        String encoded = new BCryptPasswordEncoder().encode(PASSWORD);
        for (int size : List.of(30, 60, 120)) {
            for (int index = 0; index < 4; index++) {
                Student student = insertStudent(size, encoded);
                for (int i = 0; i < size; i++) insertTaken(student.id(), code((i * 7 + index) % 180), i);
            }
        }
        functional = insertStudent(1, encoded);
        faultStudent = insertStudent(1, encoded);
        insertTaken(functional.id(), BASE, 0);
        insertTaken(faultStudent.id(), BASE, 0);
    }

    private Student insertStudent(int size, String encoded) {
        int index = students.size();
        String auth = "SYN-HTTP-" + index;
        sql.update("""
            insert into user(auth_id,password,name,student_number,entry_year,major,english_level,student_category,
                transfer_credit,exchange_credit,completed_semester_count,total_credit,taken_credit,graduated,
                honors_college,created_at,updated_at)
            values (?,?,?,?,20,?,'ENG12','NORMAL','0/0/0/0','0/0/0/0/0/0/0/0/0',4,128,?,false,false,
                current_timestamp(6),current_timestamp(6))
            """, auth, encoded, "Synthetic HTTP student " + index, "SYN-HTTP-" + index, MAJOR, size * 3);
        long id = sql.queryForObject("select id from user where auth_id=?", Long.class, auth);
        Student student = new Student(id, size, auth);
        students.add(student);
        return student;
    }

    private void insertTaken(long user, String lecture, int order) {
        sql.update("insert into taken_lecture(user_id,lecture_id,year,semester,created_at,updated_at) values (?,?,2023,'FIRST',timestampadd(second,?,'2023-01-01 00:00:00'),timestampadd(second,?,'2023-01-01 00:00:00'))",
            user, lecture, order, order);
    }

    private void loginAll(App app) throws Exception {
        for (Student student : students) {
            Reply reply = request(app, null, "POST", "/api/v1/auth/sign-in",
                JSON.writeValueAsString(Map.of("authId", student.authId(), "password", PASSWORD)));
            assertThat(reply.status()).as("synthetic sign-in status").isEqualTo(200);
            assertThat(reply.body().path("accessToken").asText()).isNotBlank();
            app.tokens.put(student.id(), reply.body().path("accessToken").asText());
        }
    }

    private void captureExpected(App app) throws Exception {
        for (Student student : students) {
            for (String endpoint : ENDPOINTS) {
                Reply reply = request(app, student, "GET", endpoint, null);
                assertThat(reply.status()).as("baseline " + endpoint).isEqualTo(200);
                expected.put(key(student, endpoint), normalize(reply.body(), !endpoint.equals(LIST)));
            }
        }
    }

    private void probeApiCacheSelection(App app) throws Exception {
        Student student = students.stream().filter(s -> s.size() == 120).findFirst().orElseThrow();
        for (String endpoint : PERFORMANCE_ENDPOINTS) {
            verify(app, student, endpoint);
            Map<String, Double> before = metrics(app);
            var digestBefore = sqlDigests();
            Map<String, Long> db = dbStats();
            verify(app, student, endpoint);
            Map<String, Long> after = dbStats();
            var digestDelta = digestDifference(sqlDigests(), digestBefore);
            Map<String, Double> delta = difference(metrics(app), before);
            probes.add(Map.of("variant", variant, "mode", app.mode, "endpoint", endpoint,
                "selects", after.get("Com_select") - db.get("Com_select"), "cache", delta, "sqlProfile", digestDelta));
            assertThat(digestDelta).as("MySQL digest profiling must capture the HTTP SELECTs").isNotEmpty();
            if (variant.equals("optimized") && endpoint.equals(CREDITS)) {
                assertThat(digestDelta.stream().map(row -> row.get("sql").toString()).toList())
                    .noneMatch(statement -> statement.contains("`completed_credit`"));
                assertThat(after.get("Com_select") - db.get("Com_select"))
                    .isEqualTo(app.mode.equals("on") ? 8 : 9);
            }
            writeReport();
            if (app.mode.equals("on")) {
                assertThat(delta.getOrDefault("hit", 0.0)).as("on must use the personal cache through " + endpoint).isPositive();
            } else if (app.mode.equals("shadow")) {
                assertThat(delta.getOrDefault("shadow", 0.0)).as("shadow must compare through " + endpoint).isPositive();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void steadyWarm(App app, int round) throws Exception {
        List<Student> group = students.stream().filter(student -> student.size() > 1).toList();
        long start = System.nanoTime();
        int count = 0;
        try (var workers = Executors.newFixedThreadPool(4)) {
            do {
                var tasks = new ArrayList<Callable<Reply>>();
                for (int i = 0; i < 192; i++) {
                    Student student = group.get((count + i) % group.size());
                    tasks.add(() -> verify(app, student, CREDITS));
                }
                for (var future : workers.invokeAll(tasks)) future.get(30, TimeUnit.SECONDS);
                count += tasks.size();
            } while (count < 3072 || System.nanoTime() - start < Duration.ofSeconds(10).toNanos());
        }
        Thread.sleep(1500);
        metrics(app);
        assertThat(app.jvm.keySet()).anyMatch(name -> name.startsWith("jvm_compilation_time_"));
        ((List<Map<String, Object>>) report.get("warmups")).add(Map.of("variant", variant,
            "mode", app.mode, "round", round + 1, "requests", count,
            "elapsedMs", (System.nanoTime() - start) / 1e6, "jvm", new TreeMap<>(app.jvm)));
        writeReport();
    }

    private void measure(App app, int round, int size, String endpoint, int concurrency) throws Exception {
        List<Student> group = students.stream().filter(s -> s.size() == size).toList();
        var beforeMetrics = metrics(app);
        var beforeJvm = new TreeMap<>(app.jvm);
        var beforeDb = dbStats();
        var cpuBefore = app.process.info().totalCpuDuration();
        long start = System.nanoTime();
        List<Reply> replies = new ArrayList<>();
        try (var workers = Executors.newFixedThreadPool(concurrency)) {
            var gate = new CountDownLatch(1);
            List<Callable<Reply>> calls = new ArrayList<>();
            for (int i = 0; i < REQUESTS; i++) {
                Student student = group.get(i % group.size());
                calls.add(() -> { gate.await(); return verify(app, student, endpoint); });
            }
            var futures = calls.stream().map(workers::submit).toList();
            gate.countDown();
            for (var future : futures) replies.add(future.get(30, TimeUnit.SECONDS));
        }
        long elapsed = System.nanoTime() - start;
        var afterDb = dbStats();
        var cache = difference(metrics(app), beforeMetrics);
        Map<String, Long> sqlDelta = new TreeMap<>();
        beforeDb.forEach((name, value) -> sqlDelta.put(name, afterDb.get(name) - value));
        assertThat(sqlDelta.get("Com_insert") + sqlDelta.get("Com_update") + sqlDelta.get("Com_delete"))
            .as("GET APIs must not write academic summaries").isZero();
        assertThat(cache.getOrDefault("mismatch", 0.0)).isZero();
        assertThat(cache.getOrDefault("error", 0.0)).isZero();
        long[] times = replies.stream().mapToLong(Reply::nanos).sorted().toArray();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("variant", variant); row.put("mode", app.mode); row.put("round", round + 1); row.put("size", size);
        row.put("endpoint", endpoint); row.put("concurrency", concurrency); row.put("requests", replies.size());
        row.put("p50Ms", percentile(times, .50)); row.put("p95Ms", percentile(times, .95));
        row.put("p99Ms", percentile(times, .99)); row.put("maxMs", times[times.length - 1] / 1e6);
        row.put("throughputPerSecond", replies.size() * 1e9 / elapsed);
        row.put("selectsPerRequest", sqlDelta.get("Com_select") / (double) replies.size());
        row.put("dbStatements", sqlDelta); row.put("cache", cache);
        row.put("jvm", difference(app.jvm, beforeJvm));
        row.put("responseBytesMean", replies.stream().mapToInt(Reply::bytes).average().orElseThrow());
        var cpuAfter = app.process.info().totalCpuDuration();
        row.put("appCpuAvailable", cpuBefore.isPresent() && cpuAfter.isPresent());
        if (cpuBefore.isPresent() && cpuAfter.isPresent()) {
            row.put("appCpuMs", cpuAfter.get().minus(cpuBefore.get()).toNanos() / 1e6);
        }
        measurements.add(row);
        writeReport();
    }

    private void mutationAndRollbackChecks(App app) throws Exception {
        Reply unauthenticated = request(app, null, "GET", LIST, null);
        assertThat(unauthenticated.status()).isBetween(400, 499);
        long revision = revision(functional);
        assertTotal(app, 3);
        assertThat(add(app).status()).isEqualTo(200);
        assertTotal(app, 6);
        assertThat(revision(functional)).isEqualTo(revision + 1);
        deleteAdded(app);
        assertTotal(app, 3);
        assertThat(revision(functional)).isEqualTo(revision + 2);
        var before = academicState();
        sql.update("update dev121_http_fault_gate set enabled=true where user_id=?", functional.id());
        try {
            Reply failed = add(app);
            assertThat(failed.status()).as("failure is injected after the taken-lecture insert").isBetween(400, 599);
            assertThat(academicState()).isEqualTo(before);
        } finally {
            sql.update("update dev121_http_fault_gate set enabled=false where user_id=?", functional.id());
        }
        assertTotal(app, 3);
        for (String endpoint : ENDPOINTS) verify(app, functional, endpoint);
        long catalog = sql.queryForObject("select revision from lecture_catalog_revision where id=1", Long.class);
        sql.update("update lecture set credit=4 where id=?", BASE);
        assertTotal(app, 4);
        assertThat(sql.queryForObject("select revision from lecture_catalog_revision where id=1", Long.class)).isEqualTo(catalog + 1);
        sql.update("update lecture set credit=3 where id=?", BASE);
        assertTotal(app, 3);
        checks.add(Map.of("mode", app.mode, "case", "authentication/add/delete/summary-rollback/catalog-change", "passed", true));
        writeReport();
    }

    private void concurrentReadersAndWriter(App app) throws Exception {
        JsonNode creditsAtThree = expected.get(key(functional, CREDITS));
        JsonNode creditsAtSix = creditsAtThree.deepCopy();
        for (JsonNode row : creditsAtSix) {
            if (row.path("category").asText().equals("FREE_ELECTIVE")) ((ObjectNode) row).put("takenCredit", 6.0);
        }
        try (var workers = Executors.newFixedThreadPool(5)) {
            var gate = new CountDownLatch(1);
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int thread = 0; thread < 4; thread++) tasks.add(() -> {
                gate.await();
                for (int i = 0; i < 32; i++) {
                    Reply reply = request(app, functional, "GET", LIST, null);
                    assertThat(reply.status()).isEqualTo(200);
                    int total = reply.body().path("totalCredit").asInt();
                    int sum = 0;
                    for (JsonNode lecture : reply.body().path("takenLectures")) sum += lecture.path("credit").asInt();
                    assertThat(total).isIn(3, 6);
                    assertThat(sum).isEqualTo(total);
                    Reply credits = request(app, functional, "GET", CREDITS, null);
                    assertThat(credits.status()).isEqualTo(200);
                    assertThat(normalize(credits.body(), true)).isIn(creditsAtThree, creditsAtSix);
                }
                return null;
            });
            tasks.add(() -> {
                gate.await();
                for (int i = 0; i < 6; i++) {
                    assertThat(add(app).status()).isEqualTo(200);
                    assertTotal(app, 6);
                    deleteAdded(app);
                    assertTotal(app, 3);
                }
                return null;
            });
            var futures = tasks.stream().map(workers::submit).toList();
            gate.countDown();
            for (var future : futures) future.get(45, TimeUnit.SECONDS);
        }
        for (String endpoint : ENDPOINTS) verify(app, functional, endpoint);
        checks.add(Map.of("mode", app.mode, "case", "four HTTP readers (list + credits) and one add/delete writer", "readerRequests", 256, "writes", 12, "passed", true));
        writeReport();
    }

    private void redisFaultChecks(App app) throws Exception {
        // faultStudent has not been read in this app, so on cannot hide this fault behind L1.
        var before = metrics(app);
        List<Double> latency = new ArrayList<>();
        REDIS.getDockerClient().pauseContainerCmd(REDIS.getContainerId()).exec();
        try {
            for (int i = 0; i < 6; i++) latency.add(verify(app, faultStudent, i % 2 == 0 ? LIST : CREDITS).nanos() / 1e6);
        } finally {
            REDIS.getDockerClient().unpauseContainerCmd(REDIS.getContainerId()).exec();
        }
        var failedMetrics = difference(metrics(app), before);
        if (!app.mode.equals("off")) assertThat(failedMetrics.getOrDefault("error", 0.0)).isPositive();
        Thread.sleep(5500);
        verify(app, faultStudent, LIST);
        verify(app, faultStudent, LIST);
        var recovered = difference(metrics(app), before);
        assertThat(recovered.getOrDefault("mismatch", 0.0)).isZero();
        if (app.mode.equals("on")) assertThat(recovered.getOrDefault("hit", 0.0)).isPositive();
        checks.add(Map.of("mode", app.mode, "case", "Redis pause/cold-cache fallback/recovery", "latencyMs", latency,
            "duringFault", failedMetrics, "afterRecovery", recovered, "passed", true));
        writeReport();
    }

    private void assertTotal(App app, int expectedCredit) throws Exception {
        Reply list = request(app, functional, "GET", LIST, null);
        assertThat(list.status()).isEqualTo(200);
        assertThat(list.body().path("totalCredit").asInt()).isEqualTo(expectedCredit);
        Reply credits = request(app, functional, "GET", CREDITS, null);
        assertThat(credits.status()).isEqualTo(200);
        JsonNode free = null;
        for (JsonNode row : credits.body()) if (row.path("category").asText().equals("FREE_ELECTIVE")) free = row;
        assertThat(free).isNotNull();
        assertThat(free.path("takenCredit").asDouble()).isEqualTo(expectedCredit);
    }

    private Reply add(App app) throws Exception {
        return request(app, functional, "POST", LIST, JSON.writeValueAsString(Map.of("lectureId", ADDED)));
    }

    private void deleteAdded(App app) throws Exception {
        Long id = sql.queryForObject("select id from taken_lecture where user_id=? and lecture_id=?", Long.class, functional.id(), ADDED);
        assertThat(request(app, functional, "DELETE", LIST + "/" + id, null).status()).isEqualTo(200);
    }

    private Map<String, Object> academicState() {
        return Map.of("user", sql.queryForMap("select taken_lecture_revision,taken_credit,total_credit,graduated from user where id=?", functional.id()),
            "taken", sql.queryForList("select id,lecture_id,year,semester from taken_lecture where user_id=? order by id", functional.id()),
            "summary", sql.queryForList("select id,graduation_category,taken_credit,total_credit from completed_credit where user_id=? order by id", functional.id()));
    }

    private long revision(Student student) {
        return sql.queryForObject("select taken_lecture_revision from user where id=?", Long.class, student.id());
    }

    private Reply verify(App app, Student student, String endpoint) throws Exception {
        Reply reply = request(app, student, "GET", endpoint, null);
        assertThat(reply.status()).as("HTTP status " + app.mode + " " + endpoint).isEqualTo(200);
        assertThat(normalize(reply.body(), !endpoint.equals(LIST))).as("all-field response equality " + app.mode + " " + endpoint)
            .isEqualTo(expected.get(key(student, endpoint)));
        return reply;
    }

    private Reply request(App app, Student student, String method, String path, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + app.port + path)).timeout(Duration.ofSeconds(10));
        if (student != null) builder.header("Authorization", "Bearer " + app.tokens.get(student.id()));
        builder.header("Content-Type", "application/json");
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        long start = System.nanoTime();
        var response = client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        long elapsed = System.nanoTime() - start;
        httpRequests.increment();
        JsonNode parsed = response.body().length == 0 ? JSON.nullNode() : JSON.readTree(response.body());
        return new Reply(response.statusCode(), parsed, elapsed, response.body().length);
    }

    private App start(String mode, String namespace) throws Exception {
        int port = unusedPort();
        Path config = output.resolve(namespace + ".properties").toAbsolutePath();
        Files.createFile(config, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
        var properties = new Properties();
        properties.putAll(Map.of("spring.profiles.active", "prod", "server.address", "127.0.0.1", "server.port", String.valueOf(port),
            "spring.datasource.url", jdbcUrl(), "spring.datasource.username", "http_app", "spring.datasource.password", PASSWORD,
            "spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver", "spring.datasource.hikari.maximum-pool-size", "10",
            "spring.jpa.hibernate.ddl-auto", "none", "spring.jpa.open-in-view", "false"));
        properties.putAll(Map.of("spring.flyway.enabled", "true", "spring.flyway.baseline-on-migrate", "false",
            "spring.sql.init.mode", "never", "spring.data.redis.host", REDIS.getHost(),
            "spring.data.redis.port", REDIS.getMappedPort(6379).toString(), "cache.taken-lecture.mode", mode,
            "cache.taken-lecture.namespace", "http-" + namespace, "cache.taken-lecture.timeout", "200ms",
            "cache.taken-lecture.ttl", "10m", "cache.taken-lecture.cooldown", "5s"));
        properties.putAll(Map.of("cache.taken-lecture.max-concurrent", "16", "cache.taken-lecture.local-entries", "128",
            "cache.graduation-master.enabled", "false", "jwt.issuer", "dev121-synthetic-http", "jwt.secret-key", JWT_SECRET,
            "jwt.expiry-seconds", "3600000", "admin.api-key", "synthetic-disabled-admin", "sentry.dsn", "",
            "sentry.enabled", "false", "management.endpoints.web.exposure.include", "health,prometheus"));
        properties.putAll(Map.of("logging.level.root", "WARN", "logging.level.com.plzgraduate.myongjigraduatebe.core.EnvironmentCheck", "OFF",
            "logging.level.org.hibernate.SQL", "OFF", "logging.level.org.hibernate.orm.jdbc.bind", "OFF",
            "spring.jpa.show-sql", "false", "spring.jpa.properties.hibernate.show_sql", "false",
            "decorator.datasource.p6spy.enable-logging", "false", "decorator.datasource.p6spy.enabled", "false",
            "spring.main.banner-mode", "off", "management.endpoint.health.show-details", "never"));
        try (var writer = Files.newBufferedWriter(config)) { properties.store(writer, "Disposable synthetic HTTP environment"); }
        Path log = output.resolve(namespace + ".log").toAbsolutePath();
        Files.createFile(log, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
        var builder = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin/java").toString(),
            "-Xms64m", "-Xmx256m", "-XX:ActiveProcessorCount=2", "-jar", jar.toString(), "--spring.config.location=" + config.toUri());
        builder.environment().clear();
        builder.environment().put("LANG", "en_US.UTF-8");
        builder.environment().put("TZ", "Asia/Seoul");
        Process process = builder.redirectErrorStream(true).redirectOutput(log.toFile()).start();
        App app = new App(mode, port, process, config, log);
        APPS.add(app);
        for (int i = 0; i < 180; i++) {
            if (!process.isAlive()) throw new AssertionError("Synthetic app startup failed: " + safeLog(log));
            try {
                var response = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/actuator/health"))
                    .timeout(Duration.ofSeconds(1)).build(), HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) return app;
            } catch (java.io.IOException ignored) { }
            Thread.sleep(250);
        }
        throw new AssertionError("Synthetic app readiness timeout: " + safeLog(log));
    }

    private Map<String, Double> metrics(App app) throws Exception {
        var response = client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + app.port + "/actuator/prometheus"))
            .timeout(Duration.ofSeconds(5)).build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Double> counters = new TreeMap<>();
        var pattern = Pattern.compile("(?m)^takenlecture_cache_total\\{[^\\n]*outcome=\"([^\"]+)\"[^\\n]*}\\s+([0-9.Ee+-]+)");
        var matcher = pattern.matcher(response.body());
        while (matcher.find()) counters.merge(matcher.group(1), Double.parseDouble(matcher.group(2)), Double::sum);
        app.jvm.clear();
        var runtime = Pattern.compile("(?m)^(jvm_compilation_time_[a-z_]+|jvm_gc_pause_seconds_sum|jvm_gc_pause_seconds_count)(?:\\{[^\\n]*})?\\s+([0-9.Ee+-]+)")
            .matcher(response.body());
        while (runtime.find()) app.jvm.merge(runtime.group(1), Double.parseDouble(runtime.group(2)), Double::sum);
        return counters;
    }

    private Map<String, Map<String, Object>> sqlDigests() {
        var result = new TreeMap<String, Map<String, Object>>();
        sql.queryForList("select DIGEST,DIGEST_TEXT,COUNT_STAR,SUM_TIMER_WAIT,SUM_ROWS_SENT "
            + "from performance_schema.events_statements_summary_by_digest "
            + "where SCHEMA_NAME=? and DIGEST_TEXT like 'SELECT%' "
            + "and upper(DIGEST_TEXT) not like '%EVENTS_STATEMENTS_SUMMARY_BY_DIGEST%'", DATABASE)
            .forEach(row -> result.put(row.get("DIGEST").toString(), row));
        return result;
    }

    private List<Map<String, Object>> digestDifference(Map<String, Map<String, Object>> after,
        Map<String, Map<String, Object>> before) {
        var result = new ArrayList<Map<String, Object>>();
        after.forEach((digest, row) -> {
            var previous = before.getOrDefault(digest, Map.of());
            long count = number(row, "COUNT_STAR") - number(previous, "COUNT_STAR");
            if (count > 0) result.add(Map.of("sql", row.get("DIGEST_TEXT"), "count", count,
                "serverMs", (number(row, "SUM_TIMER_WAIT") - number(previous, "SUM_TIMER_WAIT")) / 1e9,
                "rows", number(row, "SUM_ROWS_SENT") - number(previous, "SUM_ROWS_SENT")));
        });
        return result;
    }

    private static long number(Map<String, Object> row, String key) {
        return ((Number) row.getOrDefault(key, 0L)).longValue();
    }

    private Map<String, Long> dbStats() {
        Map<String, Long> values = new TreeMap<>();
        for (String name : List.of("Com_select", "Com_insert", "Com_update", "Com_delete")) {
            var row = sql.queryForMap("show global status like '" + name + "'");
            values.put(name, Long.parseLong(row.get("Value").toString()));
        }
        return values;
    }

    private static Map<String, Double> difference(Map<String, Double> after, Map<String, Double> before) {
        Map<String, Double> result = new TreeMap<>();
        after.forEach((key, value) -> result.put(key, value - before.getOrDefault(key, 0.0)));
        return result;
    }

    private JsonNode normalize(JsonNode node, boolean sortArrays) {
        if (node.isObject()) {
            ObjectNode object = JSON.createObjectNode();
            var names = new ArrayList<String>();
            node.fieldNames().forEachRemaining(names::add);
            names.stream().sorted().forEach(name -> object.set(name, normalize(node.get(name), sortArrays)));
            return object;
        }
        if (node.isArray()) {
            var elements = new ArrayList<JsonNode>();
            node.forEach(element -> elements.add(normalize(element, sortArrays)));
            if (sortArrays) elements.sort(Comparator.comparing(JsonNode::toString));
            ArrayNode array = JSON.createArrayNode();
            elements.forEach(array::add);
            return array;
        }
        return node;
    }

    private void writeReport() throws Exception {
        JSON.writerWithDefaultPrettyPrinter().writeValue(output.resolve("report.json").toFile(), report);
    }

    private static String code(int index) { return "SYNHTTP-%03d".formatted(index); }
    private static String key(Student student, String endpoint) { return student.id() + ":" + endpoint; }
    private static double percentile(long[] sorted, double fraction) { return sorted[(int) Math.ceil(fraction * sorted.length) - 1] / 1e6; }
    private static String hash(byte[] bytes) throws Exception { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }
    private static String jdbcUrl() { return "jdbc:mysql://" + MYSQL.getHost() + ":" + MYSQL.getMappedPort(3306) + "/" + DATABASE + "?serverTimezone=Asia/Seoul&characterEncoding=UTF-8"; }
    private static String command(String... command) throws Exception { return new String(new ProcessBuilder(command).start().getInputStream().readAllBytes()).trim(); }
    private static String safeLog(Path log) throws Exception {
        String text = Files.readString(log).replace(PASSWORD, "[REDACTED]").replace(JWT_SECRET, "[REDACTED]");
        return text.substring(Math.max(0, text.length() - 6000));
    }

    private static int unusedPort() throws Exception {
        try (var socket = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))) { return socket.getLocalPort(); }
    }

    private static GenericContainer<?> container(String image, int port) {
        // Docker chooses the unused host port; these containers are paused, not restarted.
        return new GenericContainer<>(image).withExposedPorts(port).withImagePullPolicy(ignored -> false).withReuse(false)
            .withLabel("mju.test", "dev121-http")
            .withCreateContainerCmdModifier(command -> command.getHostConfig().withPortBindings(
                new PortBinding(Ports.Binding.bindIpAndPort("127.0.0.1", 0), new ExposedPort(port))));
    }

    private static final class App implements AutoCloseable {
        final String mode;
        final int port;
        final Process process;
        final Path config;
        final Path log;
        final Map<Long, String> tokens = new HashMap<>();
        final Map<String, Double> jvm = new TreeMap<>();
        App(String mode, int port, Process process, Path config, Path log) {
            this.mode = mode; this.port = port; this.process = process; this.config = config; this.log = log;
        }
        @Override
        public void close() throws Exception {
            process.destroy();
            if (!process.waitFor(10, TimeUnit.SECONDS)) { process.destroyForcibly(); process.waitFor(5, TimeUnit.SECONDS); }
            Files.deleteIfExists(config);
            String logs = Files.readString(log);
            assertThat(logs.contains(PASSWORD) || logs.contains(JWT_SECRET)).as("synthetic credentials must not be logged").isFalse();
        }
    }

    @AfterAll
    static void stopApps() throws Exception {
        try {
            for (App app : APPS) if (app.process.isAlive() || Files.exists(app.config)) app.close();
        } finally {
            if (probeDataSource != null) probeDataSource.close();
        }
    }
}
