package auth_service.common.config;


import auth_service.common.context.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;
//
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    // Define your default schema (must exist in Postgres)
    private static final String DEFAULT_TENANT = "public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        // Fallback to "public" if no tenant is set (important for health checks)
        return (tenant != null) ? tenant : DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
