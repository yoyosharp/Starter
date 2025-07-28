package com.yoyodev.starter.AOP.Logging;

import com.yoyodev.starter.Common.Constants.RequestContextAttributes;
import com.yoyodev.starter.Common.Utils.RequestContextHelper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class CustomAuditingFilter extends OncePerRequestFilter {
    private final MessageSource messageSource;

    protected String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    protected String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {

        // Initialize request context if not already set
        if (!RequestContextHelper.hasRequest()) {
            RequestContextHelper.initialize(request);
        }

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Set request attributes using RequestContextHelper
        String requestId = UUID.randomUUID().toString();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String fullUrl = request.getRequestURI() + (queryString != null ? "?" + URLDecoder.decode(queryString, StandardCharsets.UTF_8) : "");
        String remoteAddr = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // Set attributes in request context
        RequestContextHelper.setRequestAttribute(RequestContextAttributes.REQUEST_ID_ATTRIBUTE, requestId);
        RequestContextHelper.setRequestAttribute(RequestContextAttributes.FULL_URL_ATTRIBUTE, fullUrl);
        RequestContextHelper.setRequestAttribute(RequestContextAttributes.REMOTE_ADDR_ATTRIBUTE, remoteAddr);

        log.info("""
                        \nIncoming request: [{}]
                                  Method: {}
                                     URL: {}
                          Remote address: {}
                              User-Agent: {}""",
                requestId, method, fullUrl, remoteAddr, userAgent);

        try {
            filterChain.doFilter(request, wrappedResponse);
        } finally {
            int status = wrappedResponse.getStatus();
            String logMessage = "Request [{}] {} with status: {}";
            if (status < 200 || status > 299) {
                log.warn(logMessage, requestId, getMessage("common.failed"), status);
            } else {
                log.info(logMessage, requestId, getMessage("common.success"), status);
            }
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] matchingHeaders = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

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
