package com.yoyodev.starter.Config;

import com.yoyodev.starter.Model.Response.AuthModel;
import com.yoyodev.starter.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Configuration for OAuth2 authentication with external providers like Google and GitHub.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class OAuth2Config {

    private final AuthenticationService authenticationService;

    /**
     * Configures the OAuth2 login process and success handler.
     * This method should be called from the main SecurityConfig class.
     */
    public void configureOAuth2Login(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(this.oauth2UserService())
                        .oidcUserService(this.oidcUserService())
                )
                .successHandler(this.oauth2AuthenticationSuccessHandler())
        );
    }

    /**
     * Custom OAuth2 user service to process non-OIDC providers like GitHub.
     */
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            // Get the user from the default service
            OAuth2User oauth2User = delegate.loadUser(userRequest);

            // Extract provider details
            String provider = userRequest.getClientRegistration().getRegistrationId();
            String providerId = oauth2User.getAttribute("id") != null
                    ? oauth2User.getAttribute("id").toString()
                    : oauth2User.getAttribute("sub");

            // Extract common user attributes
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");

            log.info("OAuth2 login from provider: {}, user email: {}", provider, email);

            // Process the OAuth2 user (create or update local user)
            authenticationService.processOAuth2User(provider, providerId, email, name, oauth2User.getAttributes());

            return oauth2User;
        };
    }

    /**
     * Custom OIDC user service to process OIDC providers like Google.
     */
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcUserService delegate = new OidcUserService();

        return userRequest -> {
            // Get the user from the default service
            OidcUser oidcUser = delegate.loadUser(userRequest);

            // Extract provider details
            String provider = userRequest.getClientRegistration().getRegistrationId();
            String providerId = oidcUser.getSubject();

            // Extract common user attributes
            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName();

            log.info("OIDC login from provider: {}, user email: {}", provider, email);

            // Process the OIDC user (create or update local user)
            authenticationService.processOAuth2User(provider, providerId, email, name, oidcUser.getClaims());

            return oidcUser;
        };
    }

    /**
     * Success handler for OAuth2 authentication.
     * Redirects to the appropriate page after successful authentication.
     */
    @Bean
    public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            try {
                // Generate JWT token for the authenticated user
                AuthModel authModel = authenticationService.generateTokenForOAuth2User(authentication);

                // Redirect to frontend with token
                String redirectUrl = "/oauth2/success?token=" + authModel.accessToken();
                if (authModel.refreshToken() != null && !authModel.refreshToken().isEmpty()) {
                    redirectUrl += "&refreshToken=" + authModel.refreshToken();
                }

                response.sendRedirect(redirectUrl);
            } catch (Exception e) {
                log.error("Error in OAuth2 authentication success handler: {}", e.getMessage(), e);
                response.sendRedirect("/oauth2/error?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
            }
        };
    }
}
