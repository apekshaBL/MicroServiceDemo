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

    private final List<String> defaultTenants = List.of("public", "engineering", "physics", "biology");

    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("STARTING AUTOMATIC DATABASE MIGRATION...");

        for (String tenant : defaultTenants) {

            try (Connection connection = dataSource.getConnection()) {

                System.out.println("🛠️ Processing Tenant: " + tenant);


                try (Statement statement = connection.createStatement()) {
                    statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenant);
                }


                connection.setSchema(tenant);

                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                database.setDefaultSchemaName(tenant);
                database.setLiquibaseSchemaName(tenant);

                Liquibase liquibase = new Liquibase(
                        "db/changelog/user-master.xml", // Changed from master.xml
                        new ClassLoaderResourceAccessor(),
                        database
                );

                liquibase.update(new Contexts(), new LabelExpression());
                System.out.println("✅ Tenant '" + tenant + "' is ready!");

            } catch (Exception e) {
                System.err.println(" Error migrating tenant: " + tenant);
                e.printStackTrace();

            }
        }
        System.out.println(" ALL DATABASES MIGRATED SUCCESSFULLY!");
    }
}