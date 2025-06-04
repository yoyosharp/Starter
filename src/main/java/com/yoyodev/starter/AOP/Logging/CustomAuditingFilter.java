package com.yoyodev.starter.AOP.Logging;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

import static com.yoyodev.starter.Common.Constants.RequestContextAttributes.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CustomAuditingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {

        if (RequestContextHolder.getRequestAttributes() == null) {
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        }

        String requestId = UUID.randomUUID().toString();
        RequestContextHolder.getRequestAttributes().setAttribute(REQUEST_ID_ATTRIBUTE, requestId, ServletRequestAttributes.SCOPE_REQUEST);
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String fullUrl = request.getRequestURI() + (queryString != null ? "?" + URLDecoder.decode(queryString, StandardCharsets.UTF_8) : "");
        RequestContextHolder.getRequestAttributes().setAttribute(FULL_URL_ATTRIBUTE, fullUrl, ServletRequestAttributes.SCOPE_REQUEST);
        String remoteAddr = getClientIpAddress(request);
        RequestContextHolder.getRequestAttributes().setAttribute(REMOTE_ADDR_ATTRIBUTE, remoteAddr, ServletRequestAttributes.SCOPE_REQUEST);
        String userAgent = request.getHeader("User-Agent");

        log.info("""
                \nIncoming request: [{}]
                          Method: {}
                             URL: {}
                  Remote address: {}
                      User-Agent: {}""",
                requestId, method, fullUrl, remoteAddr, userAgent);
        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            if (status >= 200 && status < 300) {
                log.info("Request completed: [{}] with status {}", requestId, status);
            } else {
                log.warn("Request incompleted: [{}] with status {}", requestId, status);
            }
            RequestContextHolder.resetRequestAttributes();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] matchingHeaders = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

        String ip = Arrays.stream(matchingHeaders)
                .map(request::getHeader)
                .filter(header -> header != null && !header.isEmpty() && !"unknown".equalsIgnoreCase(header))
                .findFirst()
                .orElse(request.getRemoteAddr());

        // If the IP has multiple values (in X-Forwarded-For header), take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
