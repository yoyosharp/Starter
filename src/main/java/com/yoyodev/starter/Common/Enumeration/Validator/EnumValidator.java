package com.yoyodev.starter.Common.Enumeration.Validator;

import com.yoyodev.starter.Common.Enumeration.TransformableEnum;
import com.yoyodev.starter.Exception.BaseException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class EnumValidator implements ConstraintValidator<EnumValidation, Object> {
    private Class<? extends TransformableEnum<?>> enumClass;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return false;
        try {
            return Arrays.stream(enumClass.getEnumConstants())
                    .anyMatch(enumConstant -> enumConstant.getValue().equals(value));
        } catch (Exception e) {
            log.error("Error while validating enum, type: {}, value: {}", enumClass.getName(), value);
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public void initialize(EnumValidation constraintAnnotation) {
        this.enumClass = constraintAnnotation.targetClass();
    }
}
