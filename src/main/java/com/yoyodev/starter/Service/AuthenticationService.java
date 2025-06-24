package com.yoyodev.starter.Service;

import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Request.AuthUserRequest;
import com.yoyodev.starter.Model.Response.AuthModel;

public interface AuthenticationService {

    UserPrincipal getUserPrincipalByUsername(String username);

    AuthModel login(AuthUserRequest request);

    String getAccessTokenByRefreshToken(AuthModel authModel);

    void blacklistToken(String token);
}
