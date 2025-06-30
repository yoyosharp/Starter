package com.yoyodev.starter.Common.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public final class RequestContextHelper {

    private RequestContextHelper() {
    }

    public static <T> T getRequestAttribute(String name, Class<T> type) {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                Object value = attributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
                return type.cast(value);
            }
        } catch (Exception e) {
            log.warn("Failed to get request attribute: {}", name, e);
        }
        return null;
    }

    public static void setRequestAttribute(String name, Object value) {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                attributes.setAttribute(name, value, RequestAttributes.SCOPE_REQUEST);
            }
        } catch (Exception e) {
            log.warn("Failed to set request attribute: {}", name, e);
        }
    }

    public static void removeRequestAttribute(String name) {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                attributes.removeAttribute(name, RequestAttributes.SCOPE_REQUEST);
            }
        } catch (Exception e) {
            log.warn("Failed to remove request attribute: {}", name, e);
        }
    }

    public static HttpServletRequest getCurrentRequest() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes instanceof ServletRequestAttributes) {
                return ((ServletRequestAttributes) attributes).getRequest();
            }
        } catch (Exception e) {
            log.warn("Failed to get current request", e);
        }
        return null;
    }

    public static boolean hasRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    public static void initialize(HttpServletRequest request) {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}