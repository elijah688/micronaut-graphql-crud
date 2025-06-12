package example.micronaut;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

@Singleton
public class DbInitializer {

    private final DataSource dataSource;

    public DbInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            var rs = connection.getMetaData().getTables(null, null, "authors", null);
            if (rs.next()) {
                System.out.println("Table 'authors' already exists. Skipping initialization.");
                return;
            }

            System.out.println("Initializing DB from tables.sql...");
            String sql = readSqlFromClasspath("/sql/tables.sql");
            statement.execute(sql);
            System.out.println("DB initialization complete.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private String readSqlFromClasspath(String path) {
        try (InputStream in = getClass().getResourceAsStream(path);
             Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read SQL from classpath: " + path, e);
        }
    }
}
