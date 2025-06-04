package com.yoyodev.starter.AOP.Jwt;

import com.yoyodev.starter.Common.Enumerate.Converter.EnumConverter;
import com.yoyodev.starter.Common.Enumerate.EnabledStatus;
import com.yoyodev.starter.Common.Enumerate.PermissionLevel;
import com.yoyodev.starter.Entities.Permission;
import org.springframework.security.core.GrantedAuthority;

public class GrantedPermission implements GrantedAuthority {
    private final Permission permission;
    @Override
    public String getAuthority() {
        return permission.getName();
    }

    public GrantedPermission(Permission permission) {
        if (EnumConverter.convert(permission.getEnabled(), EnabledStatus.class) == EnabledStatus.Enabled) {
            this.permission = permission;
        }
        else {
            this.permission = null;
        }
    }

    public String getModule() {
        return permission == null ? null : permission.getModule();
    }

    public String getFunctionName() {
        return permission == null ? null : permission.getFunctionName();
    }

    public Integer getLevel() {
        return permission == null ? null : permission.getLevel();
    }

    public boolean hasPrivilege(PermissionLevel level) {
        return permission != null && permission.getLevel() >= level.getValue();
    }
}
