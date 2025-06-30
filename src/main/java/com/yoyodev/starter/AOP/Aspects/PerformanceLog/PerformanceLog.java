package com.yoyodev.starter.AOP.Aspects.PerformanceLog;

import com.yoyodev.starter.Common.Enumeration.PerformanceLogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceLog {
    PerformanceLogType[] logType();
}
