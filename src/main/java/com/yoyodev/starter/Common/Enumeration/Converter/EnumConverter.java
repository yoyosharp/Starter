package com.yoyodev.starter.Common.Enumeration.Converter;

import com.yoyodev.starter.Common.Enumeration.TransformableEnum;
import com.yoyodev.starter.Exception.InvalidStoredEnumValueException;

import java.util.Arrays;

public class EnumConverter {
    public static <T, E extends Enum<E> & TransformableEnum<T>> E convert(T value, Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumValue -> enumValue.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new InvalidStoredEnumValueException("Invalid value for enum class: " + enumClass.getSimpleName() + ", value: " + value));
    }
}
