package com.yoyodev.starter.Model.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthUserRequest(@NotNull(message = "Username is required")
                              @NotBlank(message = "Username is required")
                              String identity,
                              @NotNull(message = "Password is required")
                              @Size(min = 6, message = "Password must be at least 6 characters long")
                              String password,
                              Boolean rememberMe) {
}
