package com.yoyodev.starter.Model.Response;

import com.yoyodev.starter.Common.Enumerate.UserStatus;
import com.yoyodev.starter.Model.DTO.SimplePermission;
import com.yoyodev.starter.Model.DTO.UserPrincipal;
import com.yoyodev.starter.Repositories.Projections.UserAuthProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private String username;
    private String email;
    private UserStatus status;
    private Timestamp verifiedAt;
    private Set<String> permissions;

    public UserResponse(UserPrincipal userPrincipal) {
        this.username = userPrincipal.username();
        this.email = null;
        this.status = userPrincipal.status();
        this.verifiedAt = null;
        this.permissions = userPrincipal.permissions().stream().map(SimplePermission::name).collect(Collectors.toSet());
    }
}
