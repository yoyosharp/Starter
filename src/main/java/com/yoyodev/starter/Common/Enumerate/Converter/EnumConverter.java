package com.yoyodev.starter.Common.Enumerate.Converter;

import com.yoyodev.starter.Common.Enumerate.TransformableEnum;

import java.util.Arrays;

public class EnumConverter {
    public static <T> TransformableEnum<T> convert(T value, Class<? extends TransformableEnum<T>> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumValue -> enumValue.getValue().equals(value))
                .findFirst().orElse(null);
    }
}
