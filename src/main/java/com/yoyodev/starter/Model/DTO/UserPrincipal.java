package com.yoyodev.starter.Model.DTO;

import com.yoyodev.starter.Common.Enumeration.UserStatus;

import java.util.Set;

public record UserPrincipal(long id,
                            String username,
                            UserStatus status,
                            boolean isVerified,
                            Set<SimplePermission> permissions) {
}


