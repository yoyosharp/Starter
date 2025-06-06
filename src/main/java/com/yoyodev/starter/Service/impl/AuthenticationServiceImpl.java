package com.yoyodev.starter.Service.impl;

import com.yoyodev.starter.AOP.Jwt.JwtProvider;
import com.yoyodev.starter.Common.Enumerate.Converter.EnumConverter;
import com.yoyodev.starter.Common.Enumerate.EnabledStatus;
import com.yoyodev.starter.Common.Enumerate.ErrorCode;
import com.yoyodev.starter.Common.Enumerate.PermissionLevel;
import com.yoyodev.starter.Common.Enumerate.UserStatus;
import com.yoyodev.starter.Entities.User;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import com.yoyodev.starter.Model.DTO.SimplePermission;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Request.AuthUserRequest;
import com.yoyodev.starter.Model.Response.AuthModel;
import com.yoyodev.starter.Repositories.UserRepository;
import com.yoyodev.starter.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional(readOnly = true)
    public UserPrincipal getUserPrincipalByUsername(String username) {
        User user = userRepository.findUserAuthByIdentity(username)
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

        boolean isVerified = user.getVerifiedAt() != null && user.getVerifiedAt().before(Timestamp.valueOf(LocalDateTime.now()));

        UserStatus status = EnumConverter.convert(user.getStatus(), UserStatus.class);
        if (status == UserStatus.Pending || !isVerified) throw new BaseAuthenticationException(ErrorCode.AUTH_USER_NOT_VERIFIED.getValue());
        if (status == UserStatus.Locked) throw new BaseAuthenticationException(ErrorCode.AUTH_USER_LOCKED.getValue());
        if (status == UserStatus.Deactivated) throw new BaseAuthenticationException(ErrorCode.AUTH_USER_DEACTIVATED.getValue());

        return new UserPrincipal(user.getId(), user.getUsername(), status, isVerified, new HashSet<>(rolePermissions.values()));
    }

    @Override
    public AuthModel login(AuthUserRequest request) {
//        UserAuthProjection user = userRepository.findUserAuthByIdentity(request.identity()).orElse(null);
//        AuthUserHelper.validateUserStatus(user);
//        if (!passwordEncoder.matches(request.password(), user.getPassword()))
//            throw new BaseAuthenticationException(ErrorCode.AUTH_INVALID_CREDENTIAL, "Invalid credentials");
//        try {
//            String accessToken = jwtProvider.generateToken(user);
//            if (request.rememberMe() == null || !request.rememberMe()) {
//                return new AuthModel(accessToken, null);
//            }
//            String refreshToken = "123";
//            return new AuthModel(accessToken, refreshToken);
//        } catch (Exception e) {
//            throw new BaseException(ErrorCode.AUTH_JWT_PROCESSING_ERROR, e.getMessage());
//        }
        return null;
    }
}
