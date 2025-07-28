package com.yoyodev.starter.Service.impl;

import com.google.gson.Gson;
import com.yoyodev.starter.AOP.Aspects.PerformanceLog.PerformanceLog;
import com.yoyodev.starter.AOP.Cache.RedisCacheService;
import com.yoyodev.starter.AOP.Jwt.JwtProvider;
import com.yoyodev.starter.Common.Constants.Constants;
import com.yoyodev.starter.Common.Constants.RedisConstants;
import com.yoyodev.starter.Common.Enumeration.Converter.EnumConverter;
import com.yoyodev.starter.Common.Enumeration.*;
import com.yoyodev.starter.Common.Utils.AlgorithmHelper;
import com.yoyodev.starter.Entities.Projection.UserAuthProjection;
import com.yoyodev.starter.Entities.Projection.UserProjection;
import com.yoyodev.starter.Entities.RefreshToken;
import com.yoyodev.starter.Entities.User;
import com.yoyodev.starter.Entities.UserOAuth2Provider;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import com.yoyodev.starter.Exception.BaseException;
import com.yoyodev.starter.Model.DTO.SimplePermission;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Request.AuthUserRequest;
import com.yoyodev.starter.Model.Response.AuthModel;
import com.yoyodev.starter.Repositories.RefreshTokenRepository;
import com.yoyodev.starter.Repositories.UserOAuth2ProviderRepository;
import com.yoyodev.starter.Repositories.UserRepository;
import com.yoyodev.starter.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final long DEFAULT_BLACKLIST_TTL_OFFSET = 300; // 5 minutes in seconds
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisCacheService redisCacheService;
    private final UserOAuth2ProviderRepository userOAuth2ProviderRepository;
    private final Gson gson;
    private final String GENERATED_PERMISSION_NAME = "Generated";
    @Value("${jwt.refresh-token.expiration-time-in-days}")
    private Integer refreshTokenExpirationTimeInDay;
    @Value("${jwt.expiration-time-in-seconds}")
    private Integer jwtExpirationTimeInSecond;

    @Override
    @Transactional(readOnly = true)
    @PerformanceLog(logType = {PerformanceLogType.TIME})
    public UserPrincipal getUserPrincipalByUsername(String username) {
        /*
        User user = userRepository.findUserByIdentity(username)
                .orElseThrow(() -> new BaseAuthenticationException(ErrorCode.AUTH_USER_NOT_FOUND.getValue()));
        Map<String, SimplePermission> userPermissions = user.getUserPermissions().stream()
                .map(up -> new SimplePermission(up.getPermission().getName(),
                        up.getPermission().getModuleId(),
                        up.getPermission().getFunctionId(),
                        EnumConverter.convert(up.getLevel(), PermissionLevel.class),
                        EnumConverter.convert(up.getPermission().getEnabledFlag(), EnabledStatus.class)))
                .collect(Collectors.toMap(SimplePermission::getEffectiveName, permission -> permission));

        Map<String, SimplePermission> rolePermissions = user.getUserRoles().stream()
                .flatMap(ur -> ur.getRole().getRolePermissions().stream()
                        .map(rp -> new SimplePermission(rp.getPermission().getName(),
                                rp.getPermission().getModuleId(),
                                rp.getPermission().getFunctionId(),
                                EnumConverter.convert(rp.getLevel(), PermissionLevel.class),
                                EnumConverter.convert(rp.getPermission().getEnabledFlag(), EnabledStatus.class))))
                .collect(Collectors.toMap(SimplePermission::getEffectiveName,
                        permission -> permission,
                        (p1, p2) -> p1.level().getValue() >= p2.level().getValue() ? p1 : p2));
        */

        List<UserAuthProjection> userAuthWithPermissions = userRepository.findUserAuthByUsername(username);
        if (userAuthWithPermissions.isEmpty())
            throw new BaseAuthenticationException(ErrorCode.AUTH_USER_NOT_FOUND.getValue());

        UserAuthProjection user = userAuthWithPermissions.getFirst();
        validateUserActiveStatus(user.getVerifiedAt(), user.getUserStatus());

        Set<SimplePermission> permissions = userAuthWithPermissions.stream()
                .filter(row -> row.getUserId().equals(user.getUserId()))
                .map(row -> new SimplePermission((row.getPermissionName() == null || row.getPermissionName().isEmpty()) ? GENERATED_PERMISSION_NAME : row.getPermissionName(),
                        row.getModuleId(),
                        row.getFunctionId(),
                        EnumConverter.convert(row.getPermissionLevel(), PermissionLevel.class),
                        EnumConverter.convert(row.getEnabledFlag(), EnabledStatus.class)))
                .collect(Collectors.toSet());

        return new UserPrincipal(user.getUserId(), user.getUsername(), UserStatus.Active, true, permissions);
    }

    @Override
    @Transactional
    public AuthModel login(AuthUserRequest request) {
        UserProjection user = userRepository.findUserByIdentity(request.identity())
                .orElseThrow(() -> new BaseAuthenticationException(ErrorCode.AUTH_USER_NOT_FOUND.getValue()));
        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new BaseAuthenticationException(ErrorCode.AUTH_INVALID_CREDENTIAL, "Invalid credentials");

        validateUserActiveStatus(user.getVerifiedAt(), user.getStatus());

        try {
            String accessToken = jwtProvider.generateToken(user.getUsername());
            String refreshToken = getRefreshToken(request.rememberMe(), user.getId());
            return new AuthModel(accessToken, refreshToken);
        } catch (BaseAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.AUTH_JWT_PROCESSING_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PerformanceLog(logType = {PerformanceLogType.TIME})
    public String getAccessTokenByRefreshToken(AuthModel authModel) {
        String key = RedisConstants.BLACKLIST_TOKEN_KEY_PREFIX + RedisConstants.REDIS_KEY_SEPARATOR + authModel.accessToken();
        if (redisCacheService.existsByKey(key)) {
            throw new BaseAuthenticationException(ErrorCode.AUTH_TOKEN_BLACKLISTED, "Token is blacklisted");
        }

        try {
            String username = jwtProvider.parseSubject(authModel.accessToken());

            List<UserAuthProjection> userAuthWithPermissions = userRepository.findUserAuthByUsername(username);
            if (userAuthWithPermissions.isEmpty())
                throw new BaseAuthenticationException(ErrorCode.AUTH_USER_NOT_FOUND, "User not found");

            UserAuthProjection user = userAuthWithPermissions.getFirst();
            validateUserActiveStatus(user.getVerifiedAt(), user.getUserStatus());

            RefreshToken refreshToken = refreshTokenRepository.findAccessTokenByUserId(user.getUserId()).orElse(null);

            if (refreshToken == null || refreshToken.getExpirationTime().before(Timestamp.valueOf(LocalDateTime.now().plusSeconds(jwtExpirationTimeInSecond))))
                throw new BaseAuthenticationException(ErrorCode.AUTH_INVALID_TOKEN, "Invalid token");
            if (!AlgorithmHelper.validateMD5(authModel.refreshToken(), refreshToken.getToken()))
                throw new BaseAuthenticationException(ErrorCode.AUTH_INVALID_TOKEN, "Invalid token");

            return jwtProvider.generateToken(user.getUsername());

        } catch (BaseAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.AUTH_JWT_PROCESSING_ERROR, e.getMessage());
        }
    }

    private String getRefreshToken(Boolean rememberMe, Long userId) {
        if (rememberMe == null || !rememberMe) {
            return Constants.EMPTY_STRING;
        }

        RefreshToken refreshToken = refreshTokenRepository.findAccessTokenByUserId(userId).orElse(null);
        if (refreshToken == null) {
            refreshToken = new RefreshToken();
            refreshToken.setUserId(userId);
            updateTokenToCurrentTime(refreshToken);
        } else if (refreshToken.getExpirationTime().before(Timestamp.valueOf(LocalDateTime.now().plusSeconds(jwtExpirationTimeInSecond)))) {
            updateTokenToCurrentTime(refreshToken);
        }

        return AlgorithmHelper.generateMD5(refreshToken.getToken());
    }

    private void updateTokenToCurrentTime(RefreshToken token) {
        String randomToken = UUID.randomUUID().toString();
        token.setToken(randomToken);
        token.setExpirationTime(Timestamp.valueOf(LocalDateTime.now().plusDays(refreshTokenExpirationTimeInDay)));
        refreshTokenRepository.save(token);
    }

    private void validateUserActiveStatus(Timestamp verifiedAt, Integer userStatus) {
        boolean isVerified = verifiedAt != null && verifiedAt.before(Timestamp.valueOf(LocalDateTime.now()));

        UserStatus status = EnumConverter.convert(userStatus, UserStatus.class);
        if (status == UserStatus.Pending || !isVerified)
            throw new BaseAuthenticationException(ErrorCode.AUTH_USER_NOT_VERIFIED.getValue());
        if (status == UserStatus.Locked) throw new BaseAuthenticationException(ErrorCode.AUTH_USER_LOCKED.getValue());
        if (status == UserStatus.Deactivated)
            throw new BaseAuthenticationException(ErrorCode.AUTH_USER_DEACTIVATED.getValue());
    }

    @Override
    public void blacklistToken(String token) {
        try {
            long expirationTime = jwtProvider.getExpirationTimeFromToken(token);
            long currentTime = System.currentTimeMillis();
            long ttl = (expirationTime - currentTime) / 1000 + DEFAULT_BLACKLIST_TTL_OFFSET;

            if (ttl > 0) {
                String key = RedisConstants.BLACKLIST_TOKEN_KEY_PREFIX + RedisConstants.REDIS_KEY_SEPARATOR + token;
                redisCacheService.saveOne(key, Constants.EMPTY_STRING, (int) ttl);
            }
        } catch (Exception e) {
            log.error("Error blacklisting token: {}", e.getMessage());
            throw new BaseException(ErrorCode.AUTH_JWT_PROCESSING_ERROR, "Failed to blacklist token");
        }
    }

    @Override
    @Transactional
    public UserPrincipal processOAuth2User(String provider, String providerId, String email, String name,
                                           Map<String, Object> attributes) {
        log.info("Processing OAuth2 user: provider={}, providerId={}, email={}", provider, providerId, email);

        // Check if we already have this OAuth2 user
        UserOAuth2Provider oauth2Provider = userOAuth2ProviderRepository
                .findByProviderNameAndProviderId(provider, providerId)
                .orElse(null);

        User user;

        if (oauth2Provider != null) {
            // User exists, update provider information
            log.info("Found existing OAuth2 user: userId={}", oauth2Provider.getUser().getId());
            user = oauth2Provider.getUser();

            if (email != null && !email.isEmpty()) {
                oauth2Provider.setProviderEmail(email);
            }

            // Update attributes
            oauth2Provider.setProviderAttributes(gson.toJson(attributes));
            userOAuth2ProviderRepository.save(oauth2Provider);
        } else {
            // Check if we have a user with the same email
            if (email != null && !email.isEmpty()) {
                user = userRepository.findByEmail(email).orElse(null);

                if (user != null) {
                    // Link existing user with this OAuth2 provider
                    log.info("Linking existing user with OAuth2 provider: userId={}", user.getId());
                    createOAuth2ProviderForUser(user, provider, providerId, email, attributes);
                } else {
                    // Create new user and OAuth2 provider
                    log.info("Creating new user for OAuth2 provider");
                    user = createUserFromOAuth2(email, name);
                    createOAuth2ProviderForUser(user, provider, providerId, email, attributes);
                }
            } else {
                // No email provided, create new user
                log.info("Creating new user for OAuth2 provider (no email)");
                user = createUserFromOAuth2(null, name);
                createOAuth2ProviderForUser(user, provider, providerId, null, attributes);
            }
        }

        // Return UserPrincipal for the user
        return getUserPrincipalByUsername(user.getUsername());
    }

    @Override
    public AuthModel generateTokenForOAuth2User(Authentication authentication) {
        log.info("Generating token for OAuth2 user");

        try {
            // Get the OAuth2 user details
            Object principal = authentication.getPrincipal();
            String email = null;
            String name = null;
            String providerId = null;
            String provider = null;

            if (principal instanceof OidcUser oidcUser) {
                email = oidcUser.getEmail();
                name = oidcUser.getFullName();
                providerId = oidcUser.getSubject();
                provider = oidcUser.getIdToken().getIssuer().toString();
            } else if (principal instanceof OAuth2User oauth2User) {
                email = oauth2User.getAttribute("email");
                name = oauth2User.getAttribute("name");
                providerId = oauth2User.getAttribute("id") != null
                        ? oauth2User.getAttribute("id").toString()
                        : oauth2User.getAttribute("sub");
                provider = oauth2User.getAttribute("provider");
            }

            if (email == null && providerId == null) {
                throw new BaseAuthenticationException(ErrorCode.AUTH_USER_NOT_FOUND, "Could not extract user details from OAuth2 authentication");
            }

            // Process the OAuth2 user
            UserPrincipal userPrincipal = processOAuth2User(provider, providerId, email, name,
                    authentication.getDetails() instanceof Map
                            ? (Map<String, Object>) authentication.getDetails()
                            : new java.util.HashMap<>());

            // Generate JWT token
            String accessToken = jwtProvider.generateToken(userPrincipal.username());
            String refreshToken = getRefreshToken(true, userPrincipal.id());

            return new AuthModel(accessToken, refreshToken);
        } catch (BaseAuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating token for OAuth2 user: {}", e.getMessage(), e);
            throw new BaseException(ErrorCode.AUTH_JWT_PROCESSING_ERROR, "Failed to generate token for OAuth2 user");
        }
    }

    /**
     * Create a new user from OAuth2 information.
     */
    private User createUserFromOAuth2(String email, String name) {
        User user = new User();

        // Generate a username if name is provided, otherwise use a random UUID
        String username = name != null && !name.isEmpty()
                ? name.replaceAll("\\s+", "").toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 4)
                : "user_" + UUID.randomUUID().toString().substring(0, 6);

        // Set user properties
        user.setUsername(username);
        user.setEmail(email);
        // Generate a random password (user will not use this for login)
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setStatus(UserStatus.Active.getValue());
        user.setVerifiedAt(Timestamp.valueOf(LocalDateTime.now()));

        // Todo: there should be a procedure call to add basic role or permissions for the user, or use a database trigger to do that

        return userRepository.save(user);
    }

    /**
     * Create a new OAuth2 provider entry for a user.
     */
    private UserOAuth2Provider createOAuth2ProviderForUser(User user, String provider, String providerId,
                                                           String email, Map<String, Object> attributes) {
        UserOAuth2Provider oauth2Provider = new UserOAuth2Provider();
        oauth2Provider.setUser(user);
        oauth2Provider.setProviderName(provider);
        oauth2Provider.setProviderId(providerId);
        oauth2Provider.setProviderEmail(email);

        // Store attributes as JSON
        oauth2Provider.setProviderAttributes(gson.toJson(attributes));

        return userOAuth2ProviderRepository.save(oauth2Provider);
    }
}
