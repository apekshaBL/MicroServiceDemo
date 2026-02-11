package api_gateway.filter;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RestTemplate template; // To call auth-service

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // ✅ Public endpoints (NO TOKEN REQUIRED)
            if (path.contains("/auth/login") ||
                    path.contains("/users/signup") ||
                    path.contains("/users/signin-check") ||
                    path.contains("/users/forgot-password") ||
                    path.contains("/users/reset-password")) {

                return chain.filter(exchange);
            }

            // 🔐 Protected endpoints
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("Missing Authorization Header");
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }

            try {
                template.getForObject(
                        "http://auth-service/auth/validate?token=" + authHeader,
                        String.class
                );
            } catch (Exception e) {
                throw new RuntimeException("Unauthorized access to application");
            }

            return chain.filter(exchange);
        });
    }

}