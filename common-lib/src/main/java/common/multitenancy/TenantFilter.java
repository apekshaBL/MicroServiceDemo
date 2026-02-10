package common.multitenancy;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String tenantId = req.getHeader("X-TenantID");

        if (tenantId != null && !tenantId.isEmpty()) {
            TenantContext.setCurrentTenant(tenantId);
        } else {

            TenantContext.setCurrentTenant("public");
        }

        try {
            chain.doFilter(request, response);
        } finally {

            TenantContext.clear();
        }
    }
}