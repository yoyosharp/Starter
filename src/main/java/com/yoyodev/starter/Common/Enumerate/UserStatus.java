package com.yoyodev.starter.Common.Enumerate;

import lombok.Getter;

@Getter
public enum UserStatus implements TransformableEnum<Integer> {
    Pending(0),
    Active(1),
    Locked(-1),
    Deactivated(-10),;

    private final int value;

    UserStatus(int value) {
        this.value = value;
    }
}
