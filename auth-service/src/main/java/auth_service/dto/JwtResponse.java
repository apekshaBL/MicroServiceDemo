package auth_service.dto;


public class JwtResponse {
    private String accessToken;
    private String refreshToken;


    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


    public static JwtResponseBuilder builder() {
        return new JwtResponseBuilder();
    }

    public static class JwtResponseBuilder {
        private String accessToken;
        private String refreshToken;

        public JwtResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public JwtResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public JwtResponse build() {
            return new JwtResponse(accessToken, refreshToken);
        }
    }

    // Getters
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
}