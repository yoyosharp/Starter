package com.yoyodev.starter.Service.impl;

import com.yoyodev.starter.AOP.Jwt.JwtProvider;
import com.yoyodev.starter.Common.Constants.Constants;
import com.yoyodev.starter.Common.Enumerate.Converter.EnumConverter;
import com.yoyodev.starter.Common.Enumerate.EnabledStatus;
import com.yoyodev.starter.Common.Enumerate.ErrorCode;
import com.yoyodev.starter.Common.Enumerate.PermissionLevel;
import com.yoyodev.starter.Common.Enumerate.UserStatus;
import com.yoyodev.starter.Common.Utils.AlgorithmHelper;
import com.yoyodev.starter.Entities.Projection.UserAuthProjection;
import com.yoyodev.starter.Entities.Projection.UserProjection;
import com.yoyodev.starter.Entities.RefreshToken;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import com.yoyodev.starter.Exception.BaseException;
import com.yoyodev.starter.Model.DTO.SimplePermission;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Request.AuthUserRequest;
import com.yoyodev.starter.Model.Response.AuthModel;
import com.yoyodev.starter.Repositories.RefreshTokenRepository;
import com.yoyodev.starter.Repositories.UserRepository;
import com.yoyodev.starter.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final String GENERATED_PERMISSION_NAME = "Generated";
    @Value("${jwt.refresh-token.expiration-time-in-day}")
    private Integer refreshTokenExpirationTimeInDay;
    @Value("${jwt.expiration-time-in-second}")
    private Integer jwtExpirationTimeInSecond;

    @Override
    @Transactional(readOnly = true)
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
        validateUserActiveStatus(user.getVerifiedAt(), user.getStatus());

        Set<SimplePermission> permissions = userAuthWithPermissions.stream()
                .filter(row -> row.getUserId().equals(user.getUserId()))
                .map(row -> new SimplePermission(GENERATED_PERMISSION_NAME,
                        row.getModuleId(),
                        row.getFunctionId(),
                        EnumConverter.convert(row.getLevel(), PermissionLevel.class),
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
    public String getAccessTokenByRefreshToken(AuthModel authModel) {
        try {
            String username = jwtProvider.parseSubject(authModel.accessToken());

            List<UserAuthProjection> userAuthWithPermissions = userRepository.findUserAuthByUsername(username);
            if (userAuthWithPermissions.isEmpty())
                throw new BaseAuthenticationException(ErrorCode.AUTH_USER_NOT_FOUND, "User not found");

            UserAuthProjection user = userAuthWithPermissions.getFirst();
            validateUserActiveStatus(user.getVerifiedAt(), user.getStatus());

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
}
