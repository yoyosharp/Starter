package com.yoyodev.starter.Common.Enumerate;

import lombok.Getter;

@Getter
public enum PermissionLevel implements TransformableEnum<Integer> {
    None(0),
    Get(1),
    Get_Update(2);
    private final int value;

    PermissionLevel(int value) {
        this.value = value;
    }
}
