package com.yoyodev.starter.AOP.Jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.proc.BadJWTException;
import com.yoyodev.starter.AOP.Cache.RedisCacheService;
import com.yoyodev.starter.Common.Constants.RedisConstants;
import com.yoyodev.starter.Common.Enumeration.ErrorCode;
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
import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final AuthenticationService authenticationService;
    private final RedisCacheService redisCacheService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Processing request " + request.getRequestURI());
        String token = getJwtFromRequest(request);
        try {
            String key = RedisConstants.BLACKLIST_TOKEN_KEY_PREFIX + RedisConstants.REDIS_KEY_SEPARATOR + token;
            if (redisCacheService.existsByKey(key)) {
                throw new BaseAuthenticationException(ErrorCode.AUTH_TOKEN_BLACKLISTED, "Token is blacklisted");
            }

            String username = jwtProvider.proceedToken(token);
            UserPrincipal userPrincipal = authenticationService.getUserPrincipalByUsername(username);

            Set<GrantedPermission> authorities = userPrincipal.permissions().stream().map(GrantedPermission::new).collect(Collectors.toSet());

            SimpleAuthenticationToken authentication = SimpleAuthenticationToken.authenticated(userPrincipal, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JOSEException | BadJWTException | ParseException | JwtVerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            response.getWriter().flush();
        } catch (BaseAuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(e.getMessage());
            response.getWriter().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error");
            response.getWriter().flush();
            log.error("Internal server error", e);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || bearerToken.isEmpty()) throw new JwtVerificationException("Invalid token");
        if (bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);
        return bearerToken;
    }
}
