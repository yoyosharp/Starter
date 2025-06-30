package com.yoyodev.starter.Common.Enumeration;

public enum EnabledStatus implements TransformableEnum<Integer> {
    Enabled(1),
    Disabled(0);

    private final int value;

    EnabledStatus(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
