package com.yoyodev.starter.Common.Enumerate.Converter;

import com.yoyodev.starter.Common.Enumerate.TransformableEnum;

import java.util.Arrays;

public class EnumConverter {
    public static <T, E extends Enum<E> & TransformableEnum<T>> E convert(T value, Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumValue -> enumValue.getValue().equals(value))
                .findFirst()
                .orElse(null);
    }
}
