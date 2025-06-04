package com.yoyodev.starter.Model.DTO;

import com.yoyodev.starter.Common.Enumerate.UserStatus;
import com.yoyodev.starter.Entities.Permission;
import jakarta.persistence.MappedSuperclass;

import java.util.Set;

public record UserPrincipal(long id,
                            String username,
                            UserStatus status,
                            boolean isVerified,
                            Set<SimplePermission> permissions) {
}


