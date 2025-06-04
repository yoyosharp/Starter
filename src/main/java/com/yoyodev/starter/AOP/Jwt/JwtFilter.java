package com.yoyodev.starter.AOP.Jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.proc.BadJWTException;
import com.yoyodev.starter.Exception.BaseAuthenticationException;
import com.yoyodev.starter.Exception.JwtVerificationException;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Service.AuthenticationService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Processing request " + request.getRequestURI());
        String token = getJwtFromRequest(request);
        try {
            String username = jwtProvider.parseSubject(token);
            UserPrincipal userPrincipal = authenticationService.getUserPrincipalByUsername(username);

            List<GrantedPermission> authorities = new ArrayList<>();

            SimpleAuthenticationToken authentication = SimpleAuthenticationToken.authenticated(userPrincipal, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (JOSEException | BadJWTException | BaseAuthenticationException jwtException) {
            throw new JwtVerificationException(jwtException.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || bearerToken.isEmpty()) throw new JwtVerificationException("Invalid token");
        if (bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
        return bearerToken;
    }
}
