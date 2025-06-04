package com.yoyodev.starter.Controllers;

import com.yoyodev.starter.Common.Constants.EndpointConstants;
import com.yoyodev.starter.Common.Enumerate.ErrorCode;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import com.yoyodev.starter.Model.Request.AuthUserRequest;
import com.yoyodev.starter.Model.Response.AuthModel;
import com.yoyodev.starter.Model.Response.UserResponse;
import com.yoyodev.starter.Service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(EndpointConstants.API_V1_AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController extends BaseController {
    private final AuthenticationService authenticationService;

    @GetMapping(EndpointConstants.USER_BY_ID)
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
//        UserAuthProjection user = authenticationService.getUserAuthById(id);

        var userResponse = new UserResponse();
//        userResponse.setUsername(user.getUsername());
//        userResponse.setEmail(user.getEmail());
//        userResponse.setStatus(user.getStatus());
//        userResponse.setVerifiedAt(user.getVerifiedAt());
//        userResponse.setPermissions(user.getPermissions().stream()
//                .map(UserAuthProjection.PermissionProjection::getName)
//                .collect(Collectors.toSet())
//        );

        return getSuccess(userResponse);
    }

    @PostMapping(EndpointConstants.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody AuthUserRequest request,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String[] errors = bindingResult.getAllErrors().stream().map(e -> e.getDefaultMessage()).toArray(String[]::new);
            return getFailed(ErrorCode.VALIDATION_INVALID_PARAMETERS, errors);
        }
        try {
            AuthModel auth = authenticationService.login(request);
            return getSuccess(auth);
        } catch (BaseAuthenticationException e) {
            log.error("Login failed: user identity: {}, message: {}", request.identity(), e.getMessage());
            return getFailed(e.getErrorCode(), e.getMessage());
        }
    }
}
