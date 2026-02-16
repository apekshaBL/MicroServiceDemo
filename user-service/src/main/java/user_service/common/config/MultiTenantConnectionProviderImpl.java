package user_service.common.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<Object> {

    // Hibernate needs this empty constructor to start up
    public MultiTenantConnectionProviderImpl() {}

    @Override
    public Connection getAnyConnection() throws SQLException {
        // We use the holder to get the bean since Hibernate creates this class manually
        DataSource dataSource = ApplicationContextHolder.getBean(DataSource.class);
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
        // This command physically switches the DB schema for the current request
        connection.createStatement().execute("SET search_path TO " + tenantId);
        return connection;
    }

    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        try {
            connection.createStatement().execute("SET search_path TO public");
        } finally {
            connection.close();
        }
    }

    @Override public boolean supportsAggressiveRelease() { return false; }
    @Override public boolean isUnwrappableAs(Class<?> unwrapType) { return false; }
    @Override public <T> T unwrap(Class<T> unwrapType) { return null; }
}