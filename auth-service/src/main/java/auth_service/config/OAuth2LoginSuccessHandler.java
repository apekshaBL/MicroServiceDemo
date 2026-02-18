package auth_service.config;


import auth_service.common.context.TenantContext;
import auth_service.entity.UserCredential;
import auth_service.repository.UserCredentialRepository;
import auth_service.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserCredentialRepository repository;

    @Override

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            TenantContext.setCurrentTenant("public"); //

            UserCredential user = repository.findByEmail(email).orElseGet(() -> {
                UserCredential newUser = new UserCredential();
                newUser.setEmail(email);
                newUser.setUsername(email.split("@")[0]);
                newUser.setTenantId("public");
                newUser.setRoleName("ROLE_USER");
                newUser.setAuthProvider("GOOGLE"); // Ensure this field exists in DB
                newUser.setActive(true);
                newUser.setPassword("OAUTH2_USER_" + UUID.randomUUID());
                return repository.save(newUser);
            });

            String jwtToken = jwtService.generateToken(user.getUsername(), "public", user.getRoleName());

            // Redirect to an endpoint that exists!
            response.sendRedirect("http://localhost:8089/auth/token?sso_token=" + jwtToken);

        } catch (Exception e) {
            e.printStackTrace(); // This prints the REAL error in your IntelliJ terminal
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Auth Error: " + e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }  }