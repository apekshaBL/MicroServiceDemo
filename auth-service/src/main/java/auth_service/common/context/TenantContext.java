package auth_service.common.context;
//
public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static void setCurrentTenant(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get() != null ? CURRENT_TENANT.get() : "public";
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
