package user_service.common.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<Object> {

    private final DataSource dataSource;

    // Direct Constructor Injection - No more static Holder!
    public MultiTenantConnectionProviderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        Connection connection = getAnyConnection();
        String tenantId = (tenantIdentifier != null) ? tenantIdentifier.toString() : "public";

        // Use a try-with-resources or Statement to ensure it's executed
        try (var stmt = connection.createStatement()) {
            stmt.execute("SET search_path TO " + tenantId);
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
        return connection;
    }

    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("SET search_path TO public");
        } finally {
            connection.close();
        }
    }

    @Override public boolean supportsAggressiveRelease() { return false; }
    @Override public boolean isUnwrappableAs(Class<?> unwrapType) { return false; }
    @Override public <T> T unwrap(Class<T> unwrapType) { return null; }
}