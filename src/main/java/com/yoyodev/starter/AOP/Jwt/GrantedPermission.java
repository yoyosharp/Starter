package com.yoyodev.starter.AOP.Jwt;

import com.yoyodev.starter.Common.Enumeration.EnabledStatus;
import com.yoyodev.starter.Common.Enumeration.PermissionLevel;
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

    public boolean hasPrivilege(PermissionLevel targetLevel) {
        return permission.level().getValue() >= targetLevel.getValue();
    }

    public boolean isEnabled() {
        return permission.isEnabled() == EnabledStatus.Enabled;
    }
}
