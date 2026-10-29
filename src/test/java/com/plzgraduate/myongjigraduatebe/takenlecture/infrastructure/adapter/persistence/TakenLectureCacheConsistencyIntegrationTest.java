package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.*;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.*;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper.CompletedCreditPersistenceMapper;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository.CompletedCreditRepository;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.FindOptionalMandatoryPolicyPort;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.*;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LecturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.*;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.*;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.*;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.*;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache.*;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.mapper.TakenLectureMapper;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.*;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.UserPersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper.UserMapper;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Synthetic-only, actual MySQL/Redis, transaction proxies, calculator and summary persistence. */
@Testcontainers
@EnabledIfEnvironmentVariable(named = "RUN_TAKEN_LECTURE_CACHE_REPRODUCTION", matches = "true")
@Execution(ExecutionMode.SAME_THREAD)
@Timeout(40)
class TakenLectureCacheConsistencyIntegrationTest {
    static final String BASE = "SYN-CACHE-BASE";
    static final String ADDED = "SYN-CACHE-ADDED";
    static final String THIRD = "SYN-CACHE-THIRD";
    static final String PASSWORD = UUID.randomUUID().toString();
    @Container
    static final GenericContainer<?> MYSQL = loopbackContainer(
        System.getenv().getOrDefault("DEV121_MYSQL_IMAGE", "mysql:8.4.8"), 3306)
        .withEnv("MYSQL_DATABASE", "synthetic_cache_probe").withEnv("MYSQL_USER", "cache_probe")
        .withEnv("MYSQL_PASSWORD", PASSWORD).withEnv("MYSQL_ROOT_PASSWORD", PASSWORD)
        .waitingFor(Wait.forLogMessage(".*ready for connections.*port: 3306.*\\n", 1))
        .withStartupTimeout(Duration.ofMinutes(2));
    @Container
    static final GenericContainer<?> REDIS = loopbackContainer("redis:7.4-alpine", 6379)
        .withCommand("redis-server", "--save", "", "--appendonly", "no");
    static AnnotationConfigApplicationContext context;
    static AnnotationConfigApplicationContext other;
    static int localEntryLimit;
    static String ddl = "create-only";
    static TakenLectureCache.Mode mode = TakenLectureCache.Mode.ON;
    static String namespace = "dev121-synthetic";
    Long userId;
    FindTakenLectureUseCase reader;
    GenerateCustomizedTakenLectureUseCase writer;
    CalculationProbe probe;
    JdbcTemplate jdbc;
    StringRedisTemplate redis;
    TakenLectureCache cache;
    FaultStore store;

    static GenericContainer<?> loopbackContainer(String image, int port) {
        // Docker reallocates a host port specified as 0 on restart. Keep the selected
        // loopback-only endpoint stable so the test exercises Redis reconnect, not endpoint changes.
        final int hostPort;
        try (var socket = new java.net.ServerSocket(0, 1, java.net.InetAddress.getLoopbackAddress())) {
            hostPort = socket.getLocalPort();
        } catch (java.io.IOException exception) { throw new IllegalStateException(exception); }
        return new GenericContainer<>(image).withExposedPorts(port).withImagePullPolicy(ignored -> false)
            .withReuse(false).withLabel("mju.test", "dev121-cache")
            .withCreateContainerCmdModifier(command -> command.getHostConfig().withPortBindings(
                new PortBinding(Ports.Binding.bindIpAndPort("127.0.0.1", hostPort), new ExposedPort(port))));
    }

    @BeforeAll
    static void open() throws Exception { openWithLocalEntries(0); }

    static void openWithLocalEntries(int entries) throws Exception {
        localEntryLimit = entries;
        ddl = "create-only";
        mode = TakenLectureCache.Mode.ON;
        assertThat(MYSQL.getHost()).isIn("localhost", "127.0.0.1");
        context = newContext();
        var sql = new JdbcTemplate(context.getBean(DataSource.class));
        assertThat(sql.queryForObject("select database()", String.class)).isEqualTo("synthetic_cache_probe");
        assertThat(sql.queryForObject("select @@transaction_isolation", String.class)).isEqualTo("REPEATABLE-READ");
        // Exercise V14/V15 through Flyway against a pre-existing synthetic schema and row.
        // Baseline 13 is only for this fixture; production baseline configuration is unchanged.
        sql.execute("alter table user drop column taken_lecture_revision");
        sql.update("insert into user(auth_id,password,student_number,honors_college,entry_year,graduated,taken_credit,total_credit,created_at,updated_at) values (?,?,?,false,20,false,0,0,current_timestamp(6),current_timestamp(6))",
            "SYN-LEGACY", "disabled-synthetic-login", "SYN-LEGACY");
        sql.execute("drop table lecture_catalog_revision");
        var migrationSource = new DriverManagerDataSource(
            context.getBean(HikariDataSource.class).getJdbcUrl(), "root", PASSWORD);
        var flyway = Flyway.configure().dataSource(migrationSource).locations("classpath:db/migration")
            .baselineOnMigrate(true).baselineVersion("13").load();
        assertThat(flyway.migrate().migrationsExecuted).isEqualTo(2);
        flyway.validate();
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(sql.queryForList("select version from flyway_schema_history where type='SQL' and success=1 order by installed_rank", String.class))
            .containsExactly("14", "15");
        assertThat(sql.queryForObject("select taken_lecture_revision from user where auth_id='SYN-LEGACY'", Long.class)).isZero();
        sql.update("delete from user where auth_id='SYN-LEGACY'");
        assertThat(sql.queryForList("select engine from information_schema.tables where table_schema=database() and table_name in ('lecture','lecture_catalog_revision')", String.class))
            .hasSize(2).containsOnly("InnoDB");
        assertThat(sql.queryForObject("select count(*) from information_schema.triggers where trigger_schema=database() and trigger_name in ('lecture_catalog_revision_insert','lecture_catalog_revision_update','lecture_catalog_revision_delete')", Integer.class))
            .isEqualTo(3);
        byte[] migration = Files.readAllBytes(Path.of("src/main/resources/db/migration/V15__version_lecture_catalog.sql"));
        System.out.println("DEV121 Flyway migrations: synthetic baseline 13 -> V14/V15; validate and repeat migrate passed");
        System.out.println("DEV121 catalog migration SHA256=" + java.util.HexFormat.of().formatHex(
            java.security.MessageDigest.getInstance("SHA-256").digest(migration)));
        tx(context, false).executeWithoutResult(status -> {
            var em = context.getBean("entityManager", EntityManager.class);
            var codes = new ArrayList<>(List.of(BASE, ADDED, THIRD));
            for (int i = 0; i < 120; i++) codes.add("SYN-BENCH-" + i);
            for (String code : codes) {
                em.persist(LectureJpaEntity.builder().id(code).name("Synthetic " + code).credit(3).build());
            }
        });
        ddl = "validate";
        other = newContext();
        System.out.println("DEV121 environment: disposable MySQL8.4.8/Redis7.4, migration applied, two independent app contexts");
    }

    static AnnotationConfigApplicationContext newContext() throws ClassNotFoundException {
        var app = new AnnotationConfigApplicationContext();
        app.register(Config.class, Class.forName("com.plzgraduate.myongjigraduatebe.parsing.application.service.ParsingTextService"));
        app.refresh();
        return app;
    }

    @AfterAll
    static void close() {
        if (other != null) other.close();
        if (context != null) context.close();
    }

    @BeforeEach
    void seed() {
        context.getBean(AtomicLong.class).addAndGet(Duration.ofSeconds(6).toNanos());
        other.getBean(AtomicLong.class).addAndGet(Duration.ofSeconds(6).toNanos());
        reader = context.getBean(FindTakenLectureUseCase.class);
        writer = context.getBean(GenerateCustomizedTakenLectureUseCase.class);
        probe = context.getBean(CalculationProbe.class);
        probe.before = ignored -> { };
        probe.after = ignored -> { };
        other.getBean(CalculationProbe.class).before = ignored -> { };
        other.getBean(CalculationProbe.class).after = ignored -> { };
        store = context.getBean(FaultStore.class);
        store.beforeGet = () -> { };
        store.beforePut = () -> { };
        store.failGet = false;
        store.failPut = false;
        cache = context.getBean(TakenLectureCache.class);
        jdbc = new JdbcTemplate(context.getBean(DataSource.class));
        redis = new StringRedisTemplate(context.getBean(LettuceConnectionFactory.class));
        userId = seedStudent(1);
        assertThat(AopUtils.isAopProxy(reader)).isTrue();
        assertThat(AopUtils.isAopProxy(writer)).isTrue();
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
        // A previous injected failure may be cooling down; the other context can populate the same key.
        other.getBean(FindTakenLectureUseCase.class).findTakenLectures(userId);
    }

    static Long seedStudent(int courses) {
        return tx(context, false).execute(status -> {
            var em = context.getBean("entityManager", EntityManager.class);
            String id = "SYN-CACHE-" + UUID.randomUUID();
            var user = UserJpaEntity.builder().authId(id).studentNumber(id).name("Synthetic cache student")
                .password("disabled-synthetic-login").major("철학과").entryYear(20).englishLevel(EnglishLevel.ENG34)
                .transferCredit("0/0/0/0").exchangeCredit("0/0/0/0/0/0/0/0/0").studentCategory(StudentCategory.NORMAL).build();
            em.persist(user);
            for (int i = 0; i < courses; i++) {
                em.persist(TakenLectureJpaEntity.builder().user(user)
                    .lecture(em.getReference(LectureJpaEntity.class, courses == 1 ? BASE : "SYN-BENCH-" + i)).year(2020).semester(Semester.FIRST).build());
            }
            return user.getId();
        });
    }

    @Test
    void sequentialCommitAndDeleteKeepActualCalculationAndSavedCreditsAligned() {
        writer.generateCustomizedTakenLecture(userId, ADDED);
        assertState(6, 1);
        assertSavedCredits(6);
        Long addedId = jdbc.queryForObject("select id from taken_lecture where user_id=? and lecture_id=?", Long.class, userId, ADDED);
        context.getBean(DeleteTakenLectureUseCase.class).deleteTakenLecture(userId, addedId);
        assertState(3, 2);
        assertSavedCredits(3);
    }

    @RepeatedTest(5)
    void concurrentReadCannotPoisonCalculationOrSubsequentRead() throws Exception {
        var inserted = new CountDownLatch(1);
        var resume = new CountDownLatch(1);
        probe.before = user -> { inserted.countDown(); await(resume); };
        try (var executor = Executors.newSingleThreadExecutor()) {
            var write = executor.submit(() -> writer.generateCustomizedTakenLecture(userId, ADDED));
            try {
                await(inserted);
                assertThat(other.getBean(FindTakenLectureUseCase.class).findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
                resume.countDown();
                write.get(10, TimeUnit.SECONDS);
                assertState(6, 1);
                assertThat(probe.observedCredits.get()).isEqualTo(6);
                assertSavedCredits(6);
                System.out.println("DEV121 concurrent: DB=6, subsequentRead=6, calculation=6, savedUserCredit=6");
            } finally { resume.countDown(); }
        }
    }

    @RepeatedTest(5)
    void rollbackCannotPublishUncommittedDataAndRevisionCanBeReused() {
        probe.after = user -> { throw new PlannedRollback(); };
        assertThatThrownBy(() -> writer.generateCustomizedTakenLecture(userId, ADDED)).isInstanceOf(PlannedRollback.class);
        assertState(3, 0);
        assertThat(redis.hasKey(cache.key(userId, 1, catalogRevision()))).isFalse();
        assertThat(jdbc.queryForObject("select count(*) from completed_credit where user_id=?", Integer.class, userId)).isZero();
        probe.after = ignored -> { };
        writer.generateCustomizedTakenLecture(userId, THIRD);
        assertState(6, 1);
        assertThat(reader.findTakenLectures(userId).getTakenLectures()).extracting(t -> t.getLecture().getId())
            .containsExactlyInAnyOrder(BASE, THIRD);
        assertSavedCredits(6);
        System.out.println("DEV121 rollback: rolledBackDB=3, noRevision1Publication; reuseRevision1=BASE+THIRD");
    }

    @Test
    void lateReaderPublicationStaysInOldRevisionAfterWriterCommits() throws Exception {
        redis.delete(cache.key(userId, 0, catalogRevision()));
        var publishing = new CountDownLatch(1);
        var resume = new CountDownLatch(1);
        store.beforePut = () -> { publishing.countDown(); await(resume); };
        try (var executor = Executors.newSingleThreadExecutor()) {
            var read = executor.submit(() -> reader.findTakenLectures(userId));
            try {
                await(publishing);
                other.getBean(GenerateCustomizedTakenLectureUseCase.class).generateCustomizedTakenLecture(userId, ADDED);
                resume.countDown();
                assertThat(read.get(10, TimeUnit.SECONDS).calculateTotalCredit()).isEqualTo(3);
                store.beforePut = () -> { };
                assertState(6, 1);
                assertThat(redis.hasKey(cache.key(userId, 0, catalogRevision()))).isTrue();
                assertThat(redis.hasKey(cache.key(userId, 1, catalogRevision()))).isTrue();
            } finally { resume.countDown(); }
        }
    }

    @Test
    void twoAppWritersSerializeBeforeReadingAndSaveCombinedSummary() throws Exception {
        var firstInserted = new CountDownLatch(1);
        var resume = new CountDownLatch(1);
        probe.before = user -> { firstInserted.countDown(); await(resume); };
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> writer.generateCustomizedTakenLecture(userId, ADDED));
            try {
                await(firstInserted);
                var second = executor.submit(() -> other.getBean(GenerateCustomizedTakenLectureUseCase.class)
                    .generateCustomizedTakenLecture(userId, THIRD));
                resume.countDown();
                first.get(10, TimeUnit.SECONDS);
                second.get(10, TimeUnit.SECONDS);
                assertState(9, 2);
                assertSavedCredits(9);
            } finally { resume.countDown(); }
        }
    }

    @Test
    void cacheHitUsesCurrentCatalogAndUserWithoutStoringCredentials() {
        String key = cache.key(userId, 0, catalogRevision());
        String payload = redis.opsForValue().get(key);
        assertThat(payload).doesNotContain("password", "authId", "studentNumber", "disabled-synthetic-login", "Synthetic cache student");
        assertThat(redis.getExpire(key, TimeUnit.SECONDS)).isBetween(1L, 600L);
        jdbc.update("update user set name='Synthetic renamed' where id=?", userId);
        jdbc.update("update lecture set credit=4 where id=?", BASE);
        try {
            var result = reader.findTakenLectures(userId);
            assertThat(result.calculateTotalCredit()).isEqualTo(4);
            assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(4);
            assertThat(result.getTakenLectures()).allSatisfy(t -> assertThat(t.getUser().getName()).isEqualTo("Synthetic renamed"));
            assertThat(context.getBean(SimpleMeterRegistry.class).get("takenlecture.cache").tag("outcome", "hit").counter().count()).isPositive();
        } finally { jdbc.update("update lecture set credit=3 where id=?", BASE); }
    }

    @Test
    void readOnlyGraduationAndCreditRequestsDoNotChangeAcademicRows() {
        var before = jdbc.queryForMap("select taken_credit,total_credit,graduated,updated_at,taken_lecture_revision from user where id=?", userId);
        var user = context.getBean(FindUserUseCase.class).findUserById(userId);
        assertThat(context.getBean(CalculateGraduationUseCase.class).calculateGraduation(user).getTakenCredit()).isEqualTo(3);
        assertThat(context.getBean(FindCompletedCreditUseCase.class).findCompletedCredits(userId)).isNotEmpty();
        assertThat(jdbc.queryForMap("select taken_credit,total_credit,graduated,updated_at,taken_lecture_revision from user where id=?", userId)).isEqualTo(before);
        assertThat(jdbc.queryForObject("select count(*) from completed_credit where user_id=?", Integer.class, userId)).isZero();
    }

    @Test
    void ownershipFailureCannotChangeAnotherStudentsRevisionOrList() {
        Long another = seedStudent(1);
        Long foreignId = jdbc.queryForObject("select id from taken_lecture where user_id=?", Long.class, another);
        assertThatThrownBy(() -> context.getBean(DeleteTakenLectureUseCase.class).deleteTakenLecture(userId, foreignId))
            .isInstanceOf(IllegalArgumentException.class);
        assertState(3, 0);
        assertThat(jdbc.queryForObject("select taken_lecture_revision from user where id=?", Long.class, another)).isZero();
    }

    @Test
    void malformedOrForeignRevisionFallsBackWithoutPoisoningOtherStudents() {
        redis.opsForValue().set(cache.key(userId, 0, catalogRevision()), "{invalid");
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
        redis.delete(cache.key(userId, 0, catalogRevision()));
    }

    @Test
    void publicationFailureDoesNotFailReadAndOtherAppCanRebuild() {
        redis.delete(cache.key(userId, 0, catalogRevision()));
        store.failPut = true;
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
        assertThat(redis.hasKey(cache.key(userId, 0, catalogRevision()))).isFalse();
        store.failPut = false;
        assertThat(other.getBean(FindTakenLectureUseCase.class).findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
        assertThat(redis.hasKey(cache.key(userId, 0, catalogRevision()))).isTrue();
    }

    @Test
    void actualRedisDelayHasBoundedTimeoutAndDatabaseFallback() throws Exception {
        REDIS.execInContainer("redis-cli", "CLIENT", "PAUSE", "1200", "ALL");
        long start = System.nanoTime();
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
        assertThat(Duration.ofNanos(System.nanoTime() - start)).isLessThan(Duration.ofSeconds(2));
        // Recovery is bounded by the Redis pause, not an application sleep.
        try (var connection = redis.getConnectionFactory().getConnection()) { connection.ping(); }
        assertThat(other.getBean(FindTakenLectureUseCase.class).findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
    }

    @Test
    void actualReuploadReplacesWholeListAndFailureRollsBackListRevisionAndSummary() {
        jdbc.update("update user set student_number='SY000001' where id=?", userId);
        var parsing = context.getBean(com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextUseCase.class);
        parsing.enrollParsingText(userId, transcript(ADDED, THIRD), false, null);
        assertState(6, 2);
        assertSavedCredits(6);
        assertThat(reader.findTakenLectures(userId).getTakenLectures()).extracting(t -> t.getLecture().getId())
            .containsExactlyInAnyOrder(ADDED, THIRD);
        probe.after = ignored -> { throw new PlannedRollback(); };
        assertThatThrownBy(() -> parsing.enrollParsingText(userId, transcript(BASE), false, null))
            .isInstanceOf(com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException.class);
        assertState(6, 2);
        assertSavedCredits(6);
        assertThat(redis.hasKey(cache.key(userId, 4, catalogRevision()))).isFalse();
        assertThatThrownBy(() -> parsing.enrollParsingText(userId, transcript(BASE).replace("SY000001", "SY999999"), false, null))
            .isInstanceOf(com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException.class);
        assertState(6, 2);
    }

    static String transcript(String... codes) {
        var parts = new ArrayList<>(List.of("출력일자 : synthetic", "1/1",
            "인문대학 철학과, Synthetic(SY000001), 현학적 - 재학, 이수 - 2, 입학 - 신입학(2020/03/02)",
            "토익 - 0, 영어교과목면제 - 면제없음, 최종학적변동 - 없음",
            "편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0",
            "교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0",
            "공통교양 0", "총 취득학점 - 0", "이수구분", "수강년도/학기", "한글코드", "과목코드", "과목명", "학점", "등급", "중복"));
        for (String code : codes) parts.addAll(List.of("자유선택", "2026년 1학기", "합성001", code, "Synthetic course", "3", "P"));
        return String.join("|", parts);
    }

    @Test
    void unsafeTransactionBoundariesAlwaysBypassPersonalCache() {
        var user = context.getBean(FindUserUseCase.class).findUserById(userId);
        var port = context.getBean(FindTakenLecturePort.class);
        store.beforeGet = () -> { throw new AssertionError("Unsafe transaction read Redis"); };
        store.beforePut = () -> { throw new AssertionError("Unsafe transaction published Redis"); };
        assertThat(port.findTakenLecturesByUser(user)).hasSize(1);
        tx(context, false).executeWithoutResult(status -> assertThat(port.findTakenLecturesByUser(user)).hasSize(1));
        var readCommitted = tx(context, true);
        readCommitted.setIsolationLevel(org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED);
        readCommitted.executeWithoutResult(status -> assertThat(port.findTakenLecturesByUser(user)).hasSize(1));
        var defaultIsolation = tx(context, true);
        defaultIsolation.setIsolationLevel(org.springframework.transaction.TransactionDefinition.ISOLATION_DEFAULT);
        defaultIsolation.executeWithoutResult(status -> {
            assertThat(port.findReadOnlySnapshotByUserId(userId)).isEmpty();
            assertThat(port.findTakenLecturesByUser(user)).hasSize(1);
        });
    }

    @Test
    void oldWritersMustBeDrainedAndFreshNamespaceSelectedBeforeActivation() {
        jdbc.update("insert into taken_lecture(user_id,lecture_id,year,created_at,updated_at) values (?,?,2099,current_timestamp(6),current_timestamp(6))", userId, ADDED);
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
        var delegate = context.getBean(TakenLecturePersistenceAdapter.class);
        var shadowCache = new TakenLectureCache(store, context.getBean(ObjectMapper.class),
            context.getBean(SimpleMeterRegistry.class), TakenLectureCache.Mode.SHADOW,
            namespace, Duration.ofMinutes(10), Duration.ofSeconds(5), 16);
        var shadow = new CachedTakenLectureAdapter(delegate, delegate,
            shadowCache);
        tx(context, true).executeWithoutResult(status -> {
            var current = context.getBean(FindUserUseCase.class).findUserById(userId);
            assertThat(TakenLectureInventory.from(new HashSet<>(shadow.findTakenLecturesByUser(current))).calculateTotalCredit()).isEqualTo(6);
        });
        assertThat(context.getBean(SimpleMeterRegistry.class).get("takenlecture.cache").tag("outcome", "mismatch").counter().count()).isPositive();
        var freshCache = new TakenLectureCache(store, context.getBean(ObjectMapper.class),
            context.getBean(SimpleMeterRegistry.class), TakenLectureCache.Mode.ON,
            namespace + "-cutover", Duration.ofMinutes(10), Duration.ofSeconds(5), 16);
        var fresh = new CachedTakenLectureAdapter(delegate, delegate,
            freshCache);
        tx(context, true).executeWithoutResult(status -> {
            var current = context.getBean(FindUserUseCase.class).findUserById(userId);
            assertThat(TakenLectureInventory.from(new HashSet<>(fresh.findTakenLecturesByUser(current))).calculateTotalCredit()).isEqualTo(6);
        });
        assertThat(redis.hasKey(freshCache.key(userId, 0, catalogRevision()))).isTrue();
    }

    @Test
    void abruptWriterProcessExitAfterCommitStillAllowsOtherAppToRebuild() throws Exception {
        var classpath = new LinkedHashSet<String>(Arrays.asList(System.getProperty("java.class.path").split(java.io.File.pathSeparator)));
        for (ClassLoader loader = getClass().getClassLoader(); loader != null; loader = loader.getParent()) {
            if (loader instanceof java.net.URLClassLoader urls) {
                for (var url : urls.getURLs()) classpath.add(Path.of(url.toURI()).toString());
            }
        }
        Path log = Path.of("build/dev121/crash-writer.log");
        Files.createDirectories(log.getParent());
        var builder = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
            "-Ddev121.mysqlPort=" + MYSQL.getMappedPort(3306), "-Ddev121.redisPort=" + REDIS.getMappedPort(6379),
            "-cp", String.join(java.io.File.pathSeparator, classpath), CrashWriter.class.getName(), userId.toString())
            .redirectErrorStream(true).redirectOutput(log.toFile());
        builder.environment().put("DEV121_SYNTHETIC_PASSWORD", PASSWORD);
        Process process = builder.start();
        try {
            assertThat(process.waitFor(25, TimeUnit.SECONDS)).as("isolated writer exits without shutdown hooks").isTrue();
            assertThat(process.exitValue()).as("child log: %s", log).isZero();
        } finally { if (process.isAlive()) process.destroyForcibly().waitFor(5, TimeUnit.SECONDS); }
        assertThat(redis.hasKey(cache.key(userId, 1, catalogRevision()))).isFalse();
        assertState(6, 1);
        assertSavedCredits(6);
        assertThat(other.getBean(FindTakenLectureUseCase.class).findTakenLectures(userId).calculateTotalCredit()).isEqualTo(6);
        assertThat(redis.hasKey(cache.key(userId, 1, catalogRevision()))).isTrue();
    }

    public static class CrashWriter {
        public static void main(String[] args) throws Exception {
            ddl = "validate";
            var app = newContext();
            app.getBean(GenerateCustomizedTakenLectureUseCase.class).generateCustomizedTakenLecture(Long.valueOf(args[0]), ADDED);
            Runtime.getRuntime().halt(0);
        }
    }

    @Test
    void redisRestartAndMissingPostCommitPublicationAreRecoverable() throws Exception {
        writer.generateCustomizedTakenLecture(userId, ADDED);
        // The writer never publishes a personal snapshot; a process loss here cannot expose old revision 0.
        assertThat(redis.hasKey(cache.key(userId, 1, catalogRevision()))).isFalse();
        REDIS.getDockerClient().restartContainerCmd(REDIS.getContainerId()).withTimeout(1).exec();
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(6);
        long deadline = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        boolean ready = false;
        while (!ready && System.nanoTime() < deadline) {
            try (var connection = redis.getConnectionFactory().getConnection()) { ready = "PONG".equals(connection.ping()); }
            catch (RuntimeException ignored) { Thread.sleep(20); }
        }
        assertThat(ready).isTrue();
        // Each app owns an independent connection; the shared probe being ready does not imply both reconnected.
        long recoveryDeadline = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        while (!Boolean.TRUE.equals(redis.hasKey(cache.key(userId, 1, catalogRevision())))
            && System.nanoTime() < recoveryDeadline) {
            other.getBean(AtomicLong.class).addAndGet(Duration.ofSeconds(6).toNanos());
            assertThat(other.getBean(FindTakenLectureUseCase.class).findTakenLectures(userId).calculateTotalCredit()).isEqualTo(6);
            Thread.sleep(20);
        }
        assertThat(redis.hasKey(cache.key(userId, 1, catalogRevision()))).isTrue();
        assertSavedCredits(6);
    }

    @Test
    void warmSnapshotUsesOneSqlAndNeverQueriesCourseRows() {
        reader.findTakenLectures(userId);
        var statistics = context.getBean(EntityManagerFactory.class).unwrap(SessionFactory.class).getStatistics();
        long before = statistics.getPrepareStatementCount();
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
        assertThat(statistics.getPrepareStatementCount() - before).isEqualTo(1);
        tx(context, true).executeWithoutResult(status -> {
            // SHOW STATUS does not increment Com_select. Exclude common transaction-begin controls.
            long selectsBefore = Long.parseLong(jdbc.queryForMap("show session status like 'Com_select'").get("Value").toString());
            assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
            long selectsAfter = Long.parseLong(jdbc.queryForMap("show session status like 'Com_select'").get("Value").toString());
            assertThat(selectsAfter - selectsBefore).as("actual server SELECTs, including JDBC metadata").isEqualTo(1);
        });
    }

    @Test
    void catalogInsertUpdateDeleteAndRollbackAdvanceAtomically() {
        long version = catalogRevision();
        jdbc.update("insert into lecture(id,name,credit,is_revoked) values ('SYN-CATALOG-TEMP','Synthetic catalog',3,0)");
        assertThat(catalogRevision()).isEqualTo(version + 1);
        jdbc.update("update lecture set name='Synthetic catalog renamed',duplicate_code='SYN-ALIAS',is_revoked=1 where id='SYN-CATALOG-TEMP'");
        assertThat(catalogRevision()).isEqualTo(version + 2);
        assertThatThrownBy(() -> tx(context, false).executeWithoutResult(status -> {
            jdbc.update("update lecture set credit=9 where id=?", BASE);
            assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(9);
            throw new PlannedRollback();
        })).isInstanceOf(PlannedRollback.class);
        assertThat(catalogRevision()).isEqualTo(version + 2);
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(3);
        jdbc.update("delete from lecture where id='SYN-CATALOG-TEMP'");
        assertThat(catalogRevision()).isEqualTo(version + 3);
    }

    @Test
    void oldCatalogReaderCannotOverwriteNewCatalogSnapshot() throws Exception {
        long oldVersion = catalogRevision();
        redis.delete(cache.key(userId, 0, oldVersion));
        var entered = new CountDownLatch(1);
        var resume = new CountDownLatch(1);
        store.beforePut = () -> { entered.countDown(); await(resume); };
        try (var executor = Executors.newSingleThreadExecutor()) {
            var old = executor.submit(() -> reader.findTakenLectures(userId));
            try {
                assertThat(entered.await(10, TimeUnit.SECONDS)).isTrue();
                jdbc.update("update lecture set credit=4 where id=?", BASE);
                assertThat(other.getBean(FindTakenLectureUseCase.class).findTakenLectures(userId).calculateTotalCredit()).isEqualTo(4);
            } finally { resume.countDown(); }
            assertThat(old.get(10, TimeUnit.SECONDS).calculateTotalCredit()).isEqualTo(3);
            assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(4);
            assertThat(redis.hasKey(cache.key(userId, 0, oldVersion))).isTrue();
            assertThat(redis.hasKey(cache.key(userId, 0, oldVersion + 1))).isTrue();
        } finally {
            store.beforePut = () -> { };
            jdbc.update("update lecture set credit=3 where id=?", BASE);
        }
    }

    @Test
    void readContextUsesScalarRevisionEvenWhenUserEntityIsManaged() {
        var delegate = context.getBean(TakenLecturePersistenceAdapter.class);
        assertThatThrownBy(() -> tx(context, false).executeWithoutResult(status -> {
            var initial = delegate.findReadContext(userId);
            var managed = context.getBean("entityManager", EntityManager.class).find(UserJpaEntity.class, userId);
            assertThat(managed.getTakenLectureRevision()).isZero();
            delegate.saveTakenLecture(TakenLecture.custom(initial.user(), context.getBean(FindLecturePort.class).findLectureById(ADDED)));
            assertThat(delegate.findReadContext(userId).revision()).isEqualTo(1);
            assertThat(managed.getTakenLectureRevision()).isZero();
            throw new PlannedRollback();
        })).isInstanceOf(PlannedRollback.class);
        assertState(3, 0);
    }

    @Test
    void missingCatalogVersionFailsClosedInsteadOfServingCachedData() {
        long version = catalogRevision();
        jdbc.update("delete from lecture_catalog_revision where id=1");
        try {
            assertThatThrownBy(() -> reader.findTakenLectures(userId)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not initialized");
        } finally { jdbc.update("insert into lecture_catalog_revision(id,revision) values (1,?)", version); }
    }

    @Test
    void combinedReadKeepsUserInSnapshotAndRefreshesUserOnLocalHit() {
        var local = new TakenLectureCache(store, context.getBean(ObjectMapper.class), context.getBean(SimpleMeterRegistry.class),
            TakenLectureCache.Mode.ON, namespace, Duration.ofMinutes(10), Duration.ofSeconds(5), 16, 8,
            context.getBean(AtomicLong.class)::get);
        var persistence = context.getBean(TakenLecturePersistenceAdapter.class);
        var adapter = new CachedTakenLectureAdapter(persistence, persistence, local);
        var external = new JdbcTemplate(other.getBean(DataSource.class));
        String original = jdbc.queryForObject("select name from user where id=?", String.class, userId);
        try {
            tx(context, true).executeWithoutResult(status -> {
                var first = adapter.findReadOnlySnapshotByUserId(userId).orElseThrow();
                external.update("update user set name='synthetic-updated' where id=?", userId);
                var sameSnapshot = adapter.findReadOnlySnapshotByUserId(userId).orElseThrow();
                assertThat(first.user().getName()).isEqualTo(original);
                assertThat(sameSnapshot.user().getName()).isEqualTo(original);
                assertThat(sameSnapshot.inventory().calculateTotalCredit()).isEqualTo(3);
            });
            var redisReads = new AtomicInteger();
            store.beforeGet = redisReads::incrementAndGet;
            var next = tx(context, true).execute(status -> adapter.findReadOnlySnapshotByUserId(userId).orElseThrow());
            assertThat(redisReads).hasValue(0);
            assertThat(next.user().getName()).isEqualTo("synthetic-updated");
            assertThat(next.inventory().calculateTotalCredit()).isEqualTo(3);
            assertThat(persistence.findRevision(userId)).isZero();
        } finally {
            store.beforeGet = () -> { };
            external.update("update user set name=? where id=?", original, userId);
        }
    }

    @Test
    void localHitStillChecksDatabaseAndRefreshesAfterOtherWriterAndCatalogChange() {
        var local = new TakenLectureCache(store, context.getBean(ObjectMapper.class), context.getBean(SimpleMeterRegistry.class),
            TakenLectureCache.Mode.ON, namespace, Duration.ofMinutes(10), Duration.ofSeconds(5), 16, 8,
            context.getBean(AtomicLong.class)::get);
        var persistence = context.getBean(TakenLecturePersistenceAdapter.class);
        var adapter = new CachedTakenLectureAdapter(persistence, persistence, local);
        java.util.function.Supplier<TakenLectureInventory> read = () -> tx(context, true).execute(status ->
            adapter.findReadOnlySnapshotByUserId(userId).orElseThrow().inventory());
        assertThat(read.get().calculateTotalCredit()).isEqualTo(3);
        var redisReads = new AtomicInteger();
        store.beforeGet = redisReads::incrementAndGet;
        assertThat(read.get().calculateTotalCredit()).isEqualTo(3);
        assertThat(redisReads).hasValue(0);
        other.getBean(GenerateCustomizedTakenLectureUseCase.class).generateCustomizedTakenLecture(userId, ADDED);
        assertThat(read.get().calculateTotalCredit()).isEqualTo(6);
        assertThat(redisReads).hasValue(1);
        jdbc.update("update lecture set credit=4 where id=?", BASE);
        try {
            assertThat(read.get().calculateTotalCredit()).isEqualTo(7);
            assertThat(redisReads).hasValue(2);
            long version = catalogRevision();
            jdbc.update("delete from lecture_catalog_revision where id=1");
            try { assertThatThrownBy(read::get).isInstanceOf(IllegalStateException.class); }
            finally { jdbc.update("insert into lecture_catalog_revision(id,revision) values (1,?)", version); }
        } finally { jdbc.update("update lecture set credit=3 where id=?", BASE); }
    }

    static long catalogRevision() {
        return new JdbcTemplate(context.getBean(DataSource.class)).queryForObject(
            "select revision from lecture_catalog_revision where id=1", Long.class);
    }

    void assertState(int credits, long revision) {
        assertThat(jdbc.queryForObject("select coalesce(sum(l.credit),0) from taken_lecture t join lecture l on l.id=t.lecture_id where t.user_id=?", Integer.class, userId)).isEqualTo(credits);
        assertThat(jdbc.queryForObject("select taken_lecture_revision from user where id=?", Long.class, userId)).isEqualTo(revision);
        assertThat(reader.findTakenLectures(userId).calculateTotalCredit()).isEqualTo(credits);
    }

    void assertSavedCredits(double credits) {
        assertThat(jdbc.queryForObject("select taken_credit from user where id=?", Double.class, userId)).isEqualTo(credits);
        assertThat(jdbc.queryForObject("select sum(taken_credit) from completed_credit where user_id=?", Double.class, userId)).isEqualTo(credits);
    }

    static TransactionTemplate tx(AnnotationConfigApplicationContext app, boolean readOnly) {
        var tx = new TransactionTemplate(app.getBean(PlatformTransactionManager.class));
        tx.setReadOnly(readOnly);
        tx.setIsolationLevel(org.springframework.transaction.TransactionDefinition.ISOLATION_REPEATABLE_READ);
        return tx;
    }

    static void await(CountDownLatch latch) {
        try { if (!latch.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Synthetic barrier timed out"); }
        catch (InterruptedException exception) { Thread.currentThread().interrupt(); throw new IllegalStateException(exception); }
    }
    static class PlannedRollback extends RuntimeException { }

    static class CalculationProbe implements GenerateOrModifyCompletedCreditUseCase {
        final GenerateOrModifyCompletedCreditUseCase delegate;
        final FindTakenLectureUseCase reader;
        final AtomicInteger observedCredits = new AtomicInteger();
        volatile Consumer<User> before = ignored -> { };
        volatile Consumer<User> after = ignored -> { };
        CalculationProbe(GenerateOrModifyCompletedCreditUseCase delegate, FindTakenLectureUseCase reader) {
            this.delegate = delegate; this.reader = reader;
        }
        public void generateOrModifyCompletedCredit(User user) {
            before.accept(user);
            observedCredits.set(reader.findTakenLectures(user.getId()).calculateTotalCredit());
            delegate.generateOrModifyCompletedCredit(user);
            after.accept(user);
        }
    }

    static class FaultStore implements TakenLectureCacheStore {
        final TakenLectureRedisStore delegate;
        volatile boolean failGet, failPut;
        volatile Runnable beforeGet = () -> { }, beforePut = () -> { };
        FaultStore(LettuceConnectionFactory factory) { delegate = new TakenLectureRedisStore(factory, Duration.ofMillis(200)); }
        public String get(String key) { beforeGet.run(); if (failGet) throw new IllegalStateException("synthetic read failure"); return delegate.get(key); }
        public void put(String key, String value, Duration ttl) { beforePut.run(); if (failPut) throw new IllegalStateException("synthetic publication failure"); delegate.put(key, value, ttl); }
        public void close() { delegate.close(); }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement(proxyTargetClass = true)
    @EnableJpaAuditing
    @EnableJpaRepositories(basePackageClasses = {TakenLectureRepository.class, UserRepository.class, CompletedCreditRepository.class},
        repositoryImplementationPostfix = "UnusedCacheProbeImpl")
    @ComponentScan(basePackages = {
        "com.plzgraduate.myongjigraduatebe.takenlecture.application.service",
        "com.plzgraduate.myongjigraduatebe.user.application.service.find",
        "com.plzgraduate.myongjigraduatebe.user.application.service.update",
        "com.plzgraduate.myongjigraduatebe.graduation.application.service",
        "com.plzgraduate.myongjigraduatebe.graduation.domain.service",
        "com.plzgraduate.myongjigraduatebe.completedcredit.application.service"})
    @Import({TakenLecturePersistenceAdapter.class, TakenLectureMapper.class, UserPersistenceAdapter.class, UserMapper.class,
        LecturePersistenceAdapter.class, LectureMapper.class, FindCompletedCreditAdapter.class,
        GenerateOrModifyCompletedCreditsAdapter.class, CompletedCreditPersistenceMapper.class})
    static class Config {
        @Bean(destroyMethod = "close")
        HikariDataSource dataSource(SimpleMeterRegistry metrics) {
            var ds = new HikariDataSource();
            ds.setJdbcUrl("jdbc:mysql://127.0.0.1:" + Integer.getInteger("dev121.mysqlPort", MYSQL.isRunning() ? MYSQL.getMappedPort(3306) : 0)
                + "/synthetic_cache_probe?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            ds.setUsername("cache_probe"); ds.setPassword(System.getenv().getOrDefault("DEV121_SYNTHETIC_PASSWORD", PASSWORD));
            ds.setMetricsTrackerFactory(new com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory(metrics));
            ds.setMaximumPoolSize(8); ds.setMinimumIdle(0); ds.setConnectionTimeout(2000);
            return ds;
        }
        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            var factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource); factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setManagedTypes(PersistenceManagedTypes.of(UserJpaEntity.class.getName(), LectureJpaEntity.class.getName(),
                TakenLectureJpaEntity.class.getName(), CompletedCreditJpaEntity.class.getName(),
                com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureCatalogRevisionJpaEntity.class.getName()));
            factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", ddl, "hibernate.show_sql", "false",
                "hibernate.hbm2ddl.halt_on_error", "true", "hibernate.generate_statistics", "true",
                "hibernate.session.events.log", "false", "hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"));
            return factory;
        }
        @Bean EntityManager entityManager(EntityManagerFactory factory) { return SharedEntityManagerCreator.createSharedEntityManager(factory); }
        @Bean PlatformTransactionManager transactionManager(EntityManagerFactory factory) { return new JpaTransactionManager(factory); }
        @Bean LettuceConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory(new RedisStandaloneConfiguration("127.0.0.1", Integer.getInteger("dev121.redisPort", REDIS.isRunning() ? REDIS.getMappedPort(6379) : 0)),
                LettuceClientConfiguration.builder().commandTimeout(Duration.ofSeconds(2)).build());
        }
        @Bean(destroyMethod = "close") FaultStore faultStore(LettuceConnectionFactory factory) { return new FaultStore(factory); }
        @Bean SimpleMeterRegistry meterRegistry() { return new SimpleMeterRegistry(); }
        @Bean ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }
        @Bean AtomicLong ticker() { return new AtomicLong(System.nanoTime()); }
        @Bean TakenLectureCache takenLectureCache(FaultStore store, ObjectMapper mapper, SimpleMeterRegistry metrics, AtomicLong ticker) {
            return new TakenLectureCache(store, mapper, metrics, mode, namespace, Duration.ofMinutes(10), Duration.ofSeconds(5), 16, localEntryLimit, ticker::get);
        }
        @Bean @Primary FindTakenLecturePort cachedTakenLectures(TakenLecturePersistenceAdapter delegate, FindLecturePort lectures,
            TakenLectureCache cache, DataSource ds) { return new CachedTakenLectureAdapter(delegate, delegate, cache); }
        @Bean LectureRepository lectureRepository(@Qualifier("entityManager") EntityManager em) { return new JpaRepositoryFactory(em).getRepository(LectureRepository.class); }
        @Bean LectureQueryRepository lectureQueryRepository() { return mock(LectureQueryRepository.class); }
        @Bean FindCommonCulturePort common() { return mock(FindCommonCulturePort.class); }
        @Bean FindCoreCulturePort core() { return mock(FindCoreCulturePort.class); }
        @Bean FindBasicAcademicalCulturePort basic() { return mock(FindBasicAcademicalCulturePort.class); }
        @Bean FindMajorPort major() { var port = mock(FindMajorPort.class); when(port.findMajor(anyString())).thenAnswer(i -> new HashSet<>()); return port; }
        @Bean FindOptionalMandatoryPolicyPort policies() { return mock(FindOptionalMandatoryPolicyPort.class); }
        @Bean FusionMajorMembershipPort fusion() { return mock(FusionMajorMembershipPort.class); }
        @Bean com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesUseCase findLectures(FindLecturePort port) {
            return new com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesUseCase() {
                public List<com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture> findLecturesByIds(List<String> ids) { return port.findLecturesByIds(ids); }
                public List<com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture> findLecturesByLectureCodes(List<String> ids) { return port.findLecturesByLectureCodes(ids); }
            };
        }
        @Bean com.plzgraduate.myongjigraduatebe.log.application.usecase.LogInvalidLectureUseCase invalidLectureLog() {
            return mock(com.plzgraduate.myongjigraduatebe.log.application.usecase.LogInvalidLectureUseCase.class);
        }
        @Bean @Primary CalculationProbe calculationProbe(
            @Qualifier("generateOrModifyCompletedCreditService") GenerateOrModifyCompletedCreditUseCase delegate,
            FindTakenLectureUseCase reader) { return new CalculationProbe(delegate, reader); }
    }
}
