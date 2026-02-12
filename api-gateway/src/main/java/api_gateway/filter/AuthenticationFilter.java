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

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(new RuntimeException("Missing Authorization Header"));
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String token = (authHeader != null && authHeader.startsWith("Bearer "))
                    ? authHeader.substring(7) : authHeader;


            return webClientBuilder.build()
                    .get()
                    .uri("http://auth-service/auth/validate?token=" + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(response -> {

                        String tenantId = extractClaim(token, "tenantId");
                        String userId = extractClaim(token, "sub"); // "sub" holds the username in your JWT


                        if (tenantId == null || tenantId.isEmpty()) {
                            return Mono.error(new RuntimeException("Token missing Tenant ID"));
                        }


                        ServerHttpRequest request = exchange.getRequest().mutate()
                                .header("X-Tenant-ID", tenantId)
                                .header("X-User-ID", userId)
                                .build();

                        return chain.filter(exchange.mutate().request(request).build());
                    })
                    .onErrorResume(e -> {
                        System.out.println("Gateway Auth Error: " + e.getMessage());
                        return Mono.error(new RuntimeException("Unauthorized access: " + e.getMessage()));
                    });
        };
    }


    private String extractClaim(String token, String claimKey) {
        try {

            String[] chunks = token.split("\\.");
            if (chunks.length < 2) return null;

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            JsonNode node = objectMapper.readTree(payload);
            if (node.has(claimKey)) {
                return node.get(claimKey).asText();
            }
        } catch (Exception e) {
            System.err.println("Error extracting claim: " + e.getMessage());
        }
        return null;
    }
}