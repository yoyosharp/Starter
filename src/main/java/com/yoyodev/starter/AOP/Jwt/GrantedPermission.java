package com.yoyodev.starter.AOP.Jwt;

import com.yoyodev.starter.Common.Enumerate.EnabledStatus;
import com.yoyodev.starter.Common.Enumerate.PermissionLevel;
import com.yoyodev.starter.Model.DTO.SimplePermission;
import org.springframework.security.core.GrantedAuthority;

public class GrantedPermission implements GrantedAuthority {
    private final SimplePermission permission;

    public GrantedPermission(SimplePermission permission) {
        assert permission != null;
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return permission.moduleId() + ":" + permission.functionId();
    }


    public String getModule() {
        return permission.moduleId();
    }

    public String getFunctionName() {
        return permission.functionId();
    }

    public PermissionLevel getLevel() {
        return permission.level();
    }

    public boolean hasPrivilege(PermissionLevel level) {
        return permission.level().getValue() >= level.getValue();
    }

    public boolean isEnabled() {
        return permission.isEnabled() == EnabledStatus.Enabled;
    }
}
