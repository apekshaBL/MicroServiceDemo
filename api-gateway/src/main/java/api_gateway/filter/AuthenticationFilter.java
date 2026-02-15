package api_gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Base64;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationFilter() { super(Config.class); }
    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(new RuntimeException("Missing Authorization Header"));
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            String token = (authHeader != null && authHeader.startsWith("Bearer "))
                    ? authHeader.substring(7) : authHeader;

            System.out.println("DEBUG: Sending token to Auth-Service: [" + token + "]");

            return webClientBuilder.build()
                    .get()
                    .uri("http://auth-service/auth/validate?token=" + token)
                    .retrieve()
                    .onStatus(status -> status.isError(), response -> {
                        System.out.println("GATEWAY ERROR: Auth-Service returned " + response.statusCode());
                        return Mono.error(new RuntimeException("Token validation failed in Auth-Service"));
                    })
                    // Use toBodilessEntity() to avoid the "Empty Body" flatMap skip
                    .toBodilessEntity()
                    .flatMap(response -> {
                        String tenantId = extractClaim(token, "tenantId");
                        String username = extractClaim(token, "sub");

                        if (tenantId == null) {
                            return Mono.error(new RuntimeException("Invalid Tenant in Token"));
                        }

                        System.out.println("GATEWAY SUCCESS: Injecting Tenant: " + tenantId + ", User: " + username);

                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-Tenant-ID", tenantId)
                                .header("X-User-Name", username)
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    })
                    .onErrorResume(e -> {
                        System.err.println("GATEWAY CRITICAL ERROR: " + e.getMessage());
                        return Mono.error(e);
                    });
        };
    }
    private String extractClaim(String token, String claimKey) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) return null;
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            JsonNode node = objectMapper.readTree(payload);
            return node.has(claimKey) ? node.get(claimKey).asText() : null;
        } catch (Exception e) { return null; }
    }
}