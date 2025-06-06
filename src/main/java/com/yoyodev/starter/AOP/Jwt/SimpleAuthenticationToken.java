package com.yoyodev.starter.AOP.Jwt;

import com.yoyodev.starter.Model.DTO.UserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import javax.security.auth.Subject;
import java.io.Serial;
import java.util.Set;

public class SimpleAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 100L;
    private final UserPrincipal principal;

    private SimpleAuthenticationToken(UserPrincipal principal, Set<GrantedPermission> permissions) {
        super(permissions);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public static SimpleAuthenticationToken authenticated(UserPrincipal principal, Set<GrantedPermission> permissions) {
        return new SimpleAuthenticationToken(principal, permissions);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }
}
