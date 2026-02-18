package auth_service.config;
import auth_service.common.context.TenantContext;
import auth_service.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private auth_service.repository.TokenBlacklistRepository blacklistRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 1. Check if Header is valid first (Safety Check)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 2. BLACKLIST CHECK (New Code) - Must happen before parsing
            if (blacklistRepository.existsByToken(token)) {
                System.out.println(" BLOCKED: Token is blacklisted/logged out.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // Stop processing immediately. Do NOT continue the chain.
            }

            try {
                // 3. Parse Token
                Claims claims = Jwts.parser()
                        .verifyWith(jwtService.getSignKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                String tenantId = claims.get("tenantId", String.class); // Extract Tenant

                // 4. Debug Logs
                System.out.println(" FILTER DEBUG: User=" + username + ", Role=" + role + ", Tenant=" + tenantId);

                // 5. Validate Role & Set Security Context
                if (username != null && role != null) {

                    // Set Tenant Context for DB operations
                    if (tenantId != null) {
                        TenantContext.setCurrentTenant(tenantId);
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    List.of(new SimpleGrantedAuthority(role))
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    System.err.println(" FILTER ERROR: Role is NULL. Check Token Generation.");
                }

            } catch (Exception e) {
                System.err.println(" FILTER EXCEPTION: " + e.getMessage());
                // Don't throw, just let the chain continue (Authentication will remain null -> 403)
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 6. Cleanup Tenant Context (Important!)
            TenantContext.clear();
        }
    }
}