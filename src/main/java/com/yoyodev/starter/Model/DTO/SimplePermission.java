package com.yoyodev.starter.Model.DTO;

import com.yoyodev.starter.Common.Enumerate.PermissionLevel;

public record SimplePermission(String name,
                               String moduleId,
                               String functionId,
                               PermissionLevel level) {
}
