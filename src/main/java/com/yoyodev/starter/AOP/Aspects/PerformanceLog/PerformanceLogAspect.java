package com.yoyodev.starter.AOP.Aspects.PerformanceLog;

import com.yoyodev.starter.Common.Constants.RequestContextAttributes;
import com.yoyodev.starter.Common.Enumeration.PerformanceLogType;
import com.yoyodev.starter.Common.Utils.RequestContextHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
@Slf4j
public class PerformanceLogAspect {
    private static final String LOG_TEMPLATE = """
            \nRequest ID: {},
            Method: {},
            URL: {},
            {}{}""";

    @PostConstruct
    public void init() {
        log.info("PerformanceLogAspect initialized");
    }

    @Around("@annotation(performanceLog)")
    public Object performanceLog(ProceedingJoinPoint joinPoint, PerformanceLog performanceLog) throws Throwable {
        if (performanceLog == null || performanceLog.logType() == null || performanceLog.logType().length == 0) {
            return joinPoint.proceed();
        }

        String requestId = RequestContextHelper.getRequestAttribute(RequestContextAttributes.REQUEST_ID_ATTRIBUTE, String.class);
        String fullUrl = RequestContextHelper.getRequestAttribute(RequestContextAttributes.FULL_URL_ATTRIBUTE, String.class);
        String method = joinPoint.getSignature().toShortString();

        // Log start metrics
        logStartMetrics(performanceLog, requestId, method, fullUrl);

        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        try {
            Object result = joinPoint.proceed();

            // Log success metrics
            logMetrics(performanceLog, requestId, method, fullUrl,
                    System.currentTimeMillis() - startTime,
                    Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - startMemory,
                    false);
            return result;
        } catch (Throwable e) {
            // Log error metrics
            logMetrics(performanceLog, requestId, method, fullUrl,
                    System.currentTimeMillis() - startTime,
                    Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - startMemory,
                    true);
            throw e;
        }
    }

    private void logStartMetrics(PerformanceLog performanceLog, String requestId, String method, String fullUrl) {
        for (PerformanceLogType logType : performanceLog.logType()) {
            switch (logType) {
                case TIME ->
                        log.debug(LOG_TEMPLATE, requestId, method, fullUrl, "StartTime: ", System.currentTimeMillis());
                case MEMORY -> log.debug(LOG_TEMPLATE, requestId, method, fullUrl,
                        "StartMemory: ", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            }
        }
    }

    private void logMetrics(PerformanceLog performanceLog, String requestId, String method,
                            String fullUrl, long duration, long memoryUsed, boolean isError) {
        String prefix = isError ? "Error - " : "";

        for (PerformanceLogType logType : performanceLog.logType()) {
            switch (logType) {
                case TIME -> log.debug(LOG_TEMPLATE, requestId, method, fullUrl,
                        prefix + "Duration (ms): ", duration);
                case MEMORY -> log.debug(LOG_TEMPLATE, requestId, method, fullUrl,
                        prefix + "Memory used (bytes): ", memoryUsed);
            }
        }
    }
}
