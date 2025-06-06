package com.yoyodev.starter.AOP.Aspects;

import com.yoyodev.starter.AOP.Jwt.GrantedPermission;
import com.yoyodev.starter.Common.Enumerate.ErrorCode;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class HasPermissionAspect {

    @Around("@annotation(hasPermission)")
    public Object validatePermission(ProceedingJoinPoint joinPoint, HasPermission hasPermission) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.error("Not authenticated");
            throw new BaseAuthenticationException(ErrorCode.AUTH_NOT_AUTHENTICATED, "Not authenticated");
        }

        Set<GrantedPermission> authorities = auth.getAuthorities().stream()
                .filter(GrantedPermission.class::isInstance)
                .map(GrantedPermission.class::cast)
                .collect(Collectors.toSet());
        boolean accepted = authorities.stream()
                .anyMatch(authority ->
                        authority.getModule().equals(hasPermission.module()) &&
                                authority.getFunctionName().equals(hasPermission.functionName()) &&
                                (authority.hasPrivilege(hasPermission.level()) || !authority.isEnabled()));

        if (!accepted) {
            log.error("No permission, username: {}, module: {}, functionName: {}",
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    hasPermission.module(),
                    hasPermission.functionName());
            throw new BaseAuthenticationException(ErrorCode.AUTH_NOT_AUTHORIZED, "No permission");
        }

        return joinPoint.proceed();
    }
}
