package com.yoyodev.starter.AOP.Aspects;

import com.yoyodev.starter.Common.Enumerate.PermissionLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermission {
    String module();
    String functionName();
    PermissionLevel level();
}
