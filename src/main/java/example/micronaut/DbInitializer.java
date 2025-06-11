// package example.micronaut;

// import jakarta.annotation.PostConstruct;
// import jakarta.inject.Singleton;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;


// import java.io.InputStream;
// import java.nio.charset.StandardCharsets;
// import java.sql.Statement;
// import java.util.Scanner;
// import io.micronaut.data.jdbc.runtime.JdbcOperations;


// @Singleton
// public class DbInitializer {

//     private static final Logger LOG = LoggerFactory.getLogger(DbInitializer.class);

//     private final JdbcOperations jdbcOperations;

//     public DbInitializer(JdbcOperations jdbcOperations) {
//         this.jdbcOperations = jdbcOperations;
//     }

//     @PostConstruct
//     public void init() {
//         LOG.info("Initializing DB from tables.sql...");
//         String sql = readSqlFromClasspath("/sql/tables.sql");

//         jdbcOperations.execute(connection -> {
//             try (Statement stmt = connection.createStatement()) {
//                 stmt.execute(sql);
//             }
//             return null;
//         });

//         LOG.info("DB initialization complete.");
//     }

//     private String readSqlFromClasspath(String path) {
//         try (InputStream in = getClass().getResourceAsStream(path);
//              Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
//             return scanner.useDelimiter("\\A").next();
//         } catch (Exception e) {
//             throw new RuntimeException("Failed to read SQL from classpath: " + path, e);
//         }
//     }
// }
