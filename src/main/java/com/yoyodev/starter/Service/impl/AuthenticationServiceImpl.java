package com.yoyodev.starter.Service.impl;

import com.yoyodev.starter.AOP.Jwt.JwtProvider;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Request.AuthUserRequest;
import com.yoyodev.starter.Model.Response.AuthModel;
import com.yoyodev.starter.Repositories.UserRepository;
import com.yoyodev.starter.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

//    @Override
//    @Transactional(readOnly = true)
//    public UserAuthProjection getUserAuthById(Long id) {
//        return userRepository.findUserAuthById(id).orElse(null);
//    }

    @Override
    @Transactional
    public UserPrincipal getUserPrincipalByUsername(String username) {
//        UserPrincipal user = userRepository.findUserPrincipalByUsername(username).orElse(null);
//        AuthUserHelper.validateUserStatus(user);
//        Set<SimplePermission> permissions = new HashSet<>();
//        if (user.getPermissions() != null) {
//            permissions = user.getPermissions().stream()
//                    .filter(Objects::nonNull)
//                    .filter(permission -> permission.getEnabled() == EnabledStatus.Enabled)
//                    .map(permission -> new SimplePermission(permission.getName(), permission.getModule(), permission.getFunctionName())).collect(Collectors.toSet());
//        }
//        return new UserPrincipal(user.getId(), user.getUsername(), user.getStatus(), user.getVerifiedAt() != null, permissions);
        return null;
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
