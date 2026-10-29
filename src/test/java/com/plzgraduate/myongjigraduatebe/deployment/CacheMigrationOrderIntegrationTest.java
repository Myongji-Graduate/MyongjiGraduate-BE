package com.plzgraduate.myongjigraduatebe.deployment;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/** Hand-authored schema only. Does not use the snapshot, production, or Spring startup. */
@EnabledIfEnvironmentVariable(named = "RUN_DEV121_MIGRATION_REHEARSAL", matches = "true")
class CacheMigrationOrderIntegrationTest {
    @TempDir
    Path migrations;

    @Test
    void appliesCacheFromVersion11AndSupportsLaterHigherVersions() throws Exception {
        var sources = new LinkedHashMap<String, String>();
        for (String filename : List.of("V14__add_taken_lecture_revision.sql",
            "V15__version_lecture_catalog.sql")) {
            byte[] sql = Files.readAllBytes(Path.of("src/main/resources/db/migration", filename));
            Files.write(migrations.resolve(filename), sql);
            sources.put(filename, HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(sql)));
        }
        String password = UUID.randomUUID().toString();
        String image = System.getenv().getOrDefault("DEV121_MYSQL_IMAGE", "mysql:8.4.8");
        assertThat(image).isIn("mysql:8.4.8", "mysql:26.7.0");
        try (var mysql = new GenericContainer<>(image)
            .withExposedPorts(3306).withImagePullPolicy(ignored -> false).withReuse(false)
            .withLabel("mju.test", "dev121-migration")
            .withEnv("MYSQL_DATABASE", "synthetic_migration_order").withEnv("MYSQL_ROOT_PASSWORD", password)
            .withCreateContainerCmdModifier(command -> command.getHostConfig().withPortBindings(
                new PortBinding(Ports.Binding.bindIpAndPort("127.0.0.1", 0), new ExposedPort(3306))))
            .waitingFor(Wait.forLogMessage(".*ready for connections.*port: 3306.*\\n", 1))
            .withStartupTimeout(Duration.ofMinutes(2))) {
            mysql.start();
            assertThat(mysql.getHost()).isIn("localhost", "127.0.0.1");
            var dataSource = new DriverManagerDataSource("jdbc:mysql://127.0.0.1:" + mysql.getMappedPort(3306)
                + "/synthetic_migration_order?useSSL=false&allowPublicKeyRetrieval=true", "root", password);
            var sql = new JdbcTemplate(dataSource);
            assertThat(sql.queryForObject("select database()", String.class)).isEqualTo("synthetic_migration_order");
            sql.execute("create table user (id bigint primary key) engine=InnoDB");
            sql.execute("create table lecture (id varchar(40) primary key, credit int not null) engine=InnoDB");
            sql.update("insert into user values (1)");
            // Baseline 11 represents only this minimal synthetic schema, never production configuration.
            var flyway = Flyway.configure().dataSource(dataSource).locations("filesystem:" + migrations)
                .baselineOnMigrate(true).baselineVersion("11").outOfOrder(false).load();
            assertThat(flyway.migrate().migrationsExecuted).isEqualTo(2);
            flyway.validate();
            assertThat(flyway.migrate().migrationsExecuted).isZero();
            assertThat(sql.queryForList("select version from flyway_schema_history where type='SQL' order by installed_rank", String.class))
                .containsExactly("14", "15");
            assertThat(sql.queryForObject("select taken_lecture_revision from user where id=1", Long.class)).isZero();
            assertThat(sql.queryForObject("select count(*) from information_schema.triggers where trigger_schema=database()", Integer.class)).isEqualTo(3);
            assertThat(sql.queryForObject("select count(*) from information_schema.tables where table_schema=database() and table_name like 'graduation_policy_%'", Integer.class)).isZero();
            try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
                connection.setAutoCommit(false);
                statement.executeUpdate("insert into lecture values ('SYN-ROLLBACK',3)");
                connection.rollback();
            }
            assertThat(sql.queryForObject("select revision from lecture_catalog_revision where id=1", Long.class)).isZero();
            sql.update("insert into lecture values ('SYN-COMMIT',3)");
            sql.update("update lecture set credit=4 where id='SYN-COMMIT'");
            sql.update("delete from lecture where id='SYN-COMMIT'");
            assertThat(sql.queryForObject("select revision from lecture_catalog_revision where id=1", Long.class)).isEqualTo(3L);

            // Synthetic stand-ins only: never read, copy, or rename DEV-116 source files.
            Path late12 = migrations.resolve("V12__synthetic_future_table.sql");
            Path late13 = migrations.resolve("V13__synthetic_future_row.sql");
            Files.writeString(late12, "create table synthetic_future (id int primary key) engine=InnoDB;\n");
            Files.writeString(late13, "insert into synthetic_future values (1);\n");
            var lateFlyway = Flyway.configure().dataSource(dataSource).locations("filesystem:" + migrations)
                .outOfOrder(false).load();
            var lateValidation = lateFlyway.validateWithResult();
            assertThat(lateValidation.validationSuccessful).isFalse();
            assertThat(lateValidation.invalidMigrations).extracting(m -> m.version)
                .containsExactlyInAnyOrder("12", "13");
            assertThat(sql.queryForObject("select count(*) from information_schema.tables where table_schema=database() and table_name='synthetic_future'", Integer.class)).isZero();
            Files.move(late12, migrations.resolve("V16__synthetic_future_table.sql"));
            Files.move(late13, migrations.resolve("V17__synthetic_future_row.sql"));
            var futureFlyway = Flyway.configure().dataSource(dataSource).locations("filesystem:" + migrations)
                .outOfOrder(false).load();
            assertThat(futureFlyway.migrate().migrationsExecuted).isEqualTo(2);
            futureFlyway.validate();
            assertThat(futureFlyway.migrate().migrationsExecuted).isZero();
            assertThat(sql.queryForList("select version from flyway_schema_history where type='SQL' order by installed_rank", String.class))
                .containsExactly("14", "15", "16", "17");
            assertThat(sql.queryForObject("select count(*) from synthetic_future", Integer.class)).isEqualTo(1);
            var evidence = new LinkedHashMap<String, Object>();
            evidence.put("databaseVersion", sql.queryForObject("select version()", String.class));
            evidence.put("imageId", mysql.getContainerInfo().getImageId());
            evidence.put("sourceSha256", sources);
            evidence.put("cacheReleaseAppliedVersions", List.of("14", "15"));
            evidence.put("checks", Map.of("validate", true, "repeatMigrateCount", 0, "triggers", 3,
                "existingUserRevision", 0, "triggerRollback", true, "triggerInsertUpdateDelete", true,
                "policyTablesCreated", 0));
            evidence.put("futureMigrationSimulation", Map.of("late12And13Rejected", true,
                "renumberedAppliedVersions", List.of("16", "17"), "validate", true, "repeatMigrateCount", 0));
            evidence.put("dev116SourceAccessed", false);
            evidence.put("productionAccessed", false);
            evidence.put("scope", "minimal synthetic schema baseline 11; not a full production upgrade or DEV-116 application test");
            Path report = Path.of("build/dev121/cache-first-migration-rehearsal.json");
            Files.createDirectories(report.getParent());
            new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(report.toFile(), evidence);
        }
    }
}
