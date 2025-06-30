package com.yoyodev.starter.Controllers;

import com.yoyodev.starter.Common.Constants.EndpointConstants;
import com.yoyodev.starter.Common.Enumeration.ErrorCode;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Response.ResponseWrapper;
import com.yoyodev.starter.Model.Response.UserResponse;
import com.yoyodev.starter.Service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.API_V1_USERS)
@Slf4j
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final AuthenticationService authenticationService;

    @GetMapping(EndpointConstants.ME)
    public ResponseEntity<ResponseWrapper<UserResponse>> me() {
        UserPrincipal userPrincipal = getAuthentication();
        return getSuccess(new UserResponse(userPrincipal));
    }

    @PostMapping(EndpointConstants.LOGOUT)
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token != null) {
                authenticationService.blacklistToken(token);
                SecurityContextHolder.clearContext();
                return getSuccess("Logged out successfully");
            }
            return getFailed(ErrorCode.AUTH_INVALID_TOKEN, "No valid token found");
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage(), e);
            return getFailed(ErrorCode.DEFAULT, e.getMessage());
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
