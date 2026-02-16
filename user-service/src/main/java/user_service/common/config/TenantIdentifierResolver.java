package user_service.common.config;

// FIX: Import the TenantContext from YOUR user_service package, not auth_service
import user_service.common.context.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        // FIX: Use the method name you defined in your TenantContext (getTenantId)
        String tenantId = TenantContext.getTenantId();
        return (tenantId != null) ? tenantId : "public";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}