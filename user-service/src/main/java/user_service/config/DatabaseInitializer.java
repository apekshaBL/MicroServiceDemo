package user_service.config;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DataSource dataSource;
    // Ensure these names match what you expect in your multi-tenant setup
    private final List<String> defaultTenants = List.of("public", "engineering", "physics", "biology");

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        for (String tenant : defaultTenants) {
            try (Connection connection = dataSource.getConnection()) {

                try (Statement statement = connection.createStatement()) {
                    // 1. Create schema if it doesn't exist
                    statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenant);
                    // 2. IMPORTANT: Set search path so Liquibase creates the 'databasechangelog'
                    // tables INSIDE the tenant schema, not in public.
                    statement.execute("SET search_path TO " + tenant);
                }

                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                // Tell Liquibase where to look/write
                database.setDefaultSchemaName(tenant);
                database.setLiquibaseSchemaName(tenant);

                Liquibase liquibase = new Liquibase(
                        "db/changelog/user-master.xml", // Check if this name is correct for user-service
                        new ClassLoaderResourceAccessor(),
                        database
                );
                liquibase.clearCheckSums();

                liquibase.update(new Contexts(), new LabelExpression());
                System.out.println("✅ Schema '" + tenant + "' updated with user-service tables.");

            } catch (Exception e) {
                System.err.println("❌ Failed to migrate tenant: " + tenant);
                e.printStackTrace();
            }
        }
    }
}