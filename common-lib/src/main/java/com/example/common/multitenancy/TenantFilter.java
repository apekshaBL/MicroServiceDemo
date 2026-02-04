package com.example.common.multitenancy;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            // Default to 'public' schema if no header is sent
            TenantContext.setCurrentTenant("public");
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // Always clear the tenant after the request finishes to prevent memory leaks
            TenantContext.clear();
        }
    }
}