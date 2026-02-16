package user_service.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
// 1. Add <String> to fix "Raw use of parameterized class"
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;

    @Autowired
    public MultiTenantConnectionProviderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    // 2. Added @NonNull and used String parameter to fix "does not override"
    @Override
    public Connection getConnection(@NonNull String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        // For PostgreSQL: switching search_path to the specific schema
        connection.createStatement().execute("SET search_path TO " + tenantIdentifier);
        return connection;
    }

    // 3. Added @NonNull and used String parameter
    @Override
    public void releaseConnection(@NonNull String tenantIdentifier, @NonNull Connection connection) throws SQLException {
        connection.createStatement().execute("SET search_path TO public");
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(@NonNull Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(@NonNull Class<T> unwrapType) {
        return null;
    }
}