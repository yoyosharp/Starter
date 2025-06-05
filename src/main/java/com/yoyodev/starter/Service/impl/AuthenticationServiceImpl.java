package com.yoyodev.starter.Service.impl;

import com.yoyodev.starter.AOP.Jwt.JwtProvider;
import com.yoyodev.starter.Common.Enumerate.Converter.EnumConverter;
import com.yoyodev.starter.Common.Enumerate.UserStatus;
import com.yoyodev.starter.Entities.User;
import com.yoyodev.starter.Model.DTO.SimplePermission;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Request.AuthUserRequest;
import com.yoyodev.starter.Model.Response.AuthModel;
import com.yoyodev.starter.Repositories.UserRepository;
import com.yoyodev.starter.Service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
        User user = userRepository.findUserAuthByIdentity(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<SimplePermission> permissions = new HashSet<>();

        boolean isVerified = user.getVerifiedAt() != null && user.getVerifiedAt().before(Timestamp.valueOf(LocalDateTime.now()));
        UserStatus status = (UserStatus) EnumConverter.convert(user.getStatus(), UserStatus.class);


        return new UserPrincipal(user.getId(), user.getUsername(), status, isVerified, permissions);
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
