package com.yoyodev.starter.Model.Response;

import com.yoyodev.starter.Common.Enumeration.UserStatus;
import com.yoyodev.starter.Model.DTO.SimplePermission;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private UserStatus status;
    private Timestamp verifiedAt;
    private Set<SimplePermission> effectivePermissions;

    public UserResponse(UserPrincipal userPrincipal) {
        this.username = userPrincipal.username();
        this.email = null;
        this.status = userPrincipal.status();
        this.verifiedAt = null;
        this.effectivePermissions = userPrincipal.permissions();
    }
}
