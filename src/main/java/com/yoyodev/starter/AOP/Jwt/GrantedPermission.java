package com.yoyodev.starter.AOP.Jwt;

import com.yoyodev.starter.Common.Enumerate.PermissionLevel;
import com.yoyodev.starter.Model.DTO.SimplePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class GrantedPermission implements GrantedAuthority {
    private final SimplePermission permission;
    @Override
    public String getAuthority() {
        return permission.moduleId() + ":" + permission.functionId();
    }


    public String getModule() {
        return permission == null ? null : permission.moduleId();
    }

    public String getFunctionName() {
        return permission == null ? null : permission.functionId();
    }

    public PermissionLevel getLevel() {
        return permission == null ? null : permission.level();
    }

    public boolean hasPrivilege(PermissionLevel level) {
        return permission != null && permission.level().getValue() >= level.getValue();
    }
}
