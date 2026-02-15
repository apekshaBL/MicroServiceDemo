package user_service.config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
//
@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<Object> {

    @Autowired
    private DataSource dataSource;

    public MultiTenantConnectionProviderImpl() {}

    @Override
    public Connection getAnyConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource has not been initialized yet!");
        }

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
        connection.createStatement().execute("SET search_path TO " + tenantId);
        return connection;
    }

    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        connection.createStatement().execute("SET search_path TO public");
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() { return false; }
    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) { return false; }
    @Override
    public <T> T unwrap(Class<T> unwrapType) { return null; }
}