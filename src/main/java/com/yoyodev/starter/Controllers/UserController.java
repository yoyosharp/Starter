package com.yoyodev.starter.Controllers;

import com.yoyodev.starter.Common.Constants.EndpointConstants;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Model.Response.ResponseWrapper;
import com.yoyodev.starter.Model.Response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointConstants.API_V1_USERS)
public class UserController extends BaseController {
    @GetMapping(EndpointConstants.ME)
    public ResponseEntity<ResponseWrapper<UserResponse>> me() {
        UserPrincipal userPrincipal = getAuthentication();
        return getSuccess(new UserResponse(userPrincipal));
    }
}
