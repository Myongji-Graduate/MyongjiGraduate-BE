// Standalone metadata probe, never a Spring app. Credentials come only from DEV121_PROBE_* environment.
// Invoke with logging disabled and the release JAR library classpath; never invokes migrate/repair/baseline/clean.
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.*;
import java.util.*;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.datasource.AbstractDataSource;

public class FlywayReadOnlyCheck {
    public static void main(String[] args) throws Exception {
        var out = new LinkedHashMap<String, Object>();
        var ds = new AbstractDataSource() {
            public Connection getConnection() throws SQLException {
                var c = DriverManager.getConnection(System.getenv("DEV121_PROBE_URL"),
                    System.getenv("DEV121_PROBE_USERNAME"), System.getenv("DEV121_PROBE_PASSWORD"));
                c.setReadOnly(true);
                try (var s = c.createStatement()) { s.execute("SET SESSION TRANSACTION READ ONLY"); }
                return c;
            }
            public Connection getConnection(String u, String p) throws SQLException { return getConnection(); }
        };
        try {
            try (var c = ds.getConnection()) {
                out.put("databaseVersion", c.getMetaData().getDatabaseProductVersion());
                out.put("driverVersion", c.getMetaData().getDriverVersion());
                out.put("jdbcReadOnly", c.isReadOnly());
            }
            var flyway = Flyway.configure().dataSource(ds).locations("filesystem:src/main/resources/db/migration")
                .cleanDisabled(true).baselineOnMigrate(false).outOfOrder(false)
                .ignoreMigrationPatterns("*:pending").load();
            var migrations = new ArrayList<Map<String, String>>();
            for (var m : flyway.info().all()) {
                migrations.add(Map.of("version", m.getVersion().toString(), "state", m.getState().name(), "script", m.getScript()));
            }
            out.put("migrations", migrations);
            var validation = flyway.validateWithResult();
            out.put("validationSuccessful", validation.validationSuccessful);
            out.put("validationErrors", validation.invalidMigrations.stream()
                .map(m -> Map.of("version", m.version, "code", m.errorDetails.errorCode.name())).toList());
            out.put("operations", List.of("jdbc metadata", "flyway info", "flyway validate"));
            out.put("migrateInvoked", false);
        } catch (Exception e) {
            out.put("errorClass", e.getClass().getName());
            if (e instanceof SQLException s) out.put("sqlState", String.valueOf(s.getSQLState()));
        }
        System.out.println(new ObjectMapper().writeValueAsString(out));
    }
}
