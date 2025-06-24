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
import java.util.Set;
import java.util.stream.Collectors;

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
            String username = jwtProvider.proceedToken(token);
            UserPrincipal userPrincipal = authenticationService.getUserPrincipalByUsername(username);

            Set<GrantedPermission> authorities = userPrincipal.permissions().stream().map(GrantedPermission::new).collect(Collectors.toSet());

            SimpleAuthenticationToken authentication = SimpleAuthenticationToken.authenticated(userPrincipal, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JOSEException | BadJWTException | BaseAuthenticationException jwtException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(jwtException.getMessage());
            response.getWriter().flush();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || bearerToken.isEmpty()) throw new JwtVerificationException("Invalid token");
        if (bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
        return bearerToken;
    }
}
