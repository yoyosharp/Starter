package com.yoyodev.starter.Service;

import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Request.AuthUserRequest;
import com.yoyodev.starter.Model.Response.AuthModel;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface AuthenticationService {

    UserPrincipal getUserPrincipalByUsername(String username);

    AuthModel login(AuthUserRequest request);

    String getAccessTokenByRefreshToken(AuthModel authModel);

    void blacklistToken(String token);

    /**
     * Process an OAuth2 user, creating or updating the local user as needed.
     *
     * @param provider   The OAuth2 provider (e.g., "google", "github")
     * @param providerId The user ID from the provider
     * @param email      The user's email
     * @param name       The user's name
     * @param attributes Additional attributes from the OAuth2 provider
     * @return The UserPrincipal for the processed user
     */
    UserPrincipal processOAuth2User(String provider, String providerId, String email, String name,
                                    Map<String, Object> attributes);

    /**
     * Generate a JWT token for an OAuth2 authenticated user.
     *
     * @param authentication The authentication object from Spring Security
     * @return An AuthModel containing the JWT token and refresh token
     */
    AuthModel generateTokenForOAuth2User(Authentication authentication);
}
