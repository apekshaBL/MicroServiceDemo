package payment_service.service;

import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Service
public class TenantService {

    private final DataSource dataSource;

    public TenantService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initDatabase(String tenantId) {

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenantId);
        } catch (Exception e) {
            throw new RuntimeException("Error creating schema: " + tenantId, e);
        }


        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(tenantId)
                .locations("db/migration")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
        System.out.println("Flyway migration finished for: " + tenantId);
    }
}