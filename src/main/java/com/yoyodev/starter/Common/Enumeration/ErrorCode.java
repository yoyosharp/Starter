package com.yoyodev.starter.Common.Enumeration;

public enum ErrorCode implements TransformableEnum<String> {
    /* The error code should be unique and follow this format:
        "#" + [2 digit]<module> + [2 digit]<function> + [2 digit]<identifier>
    */
    // Authentication
    AUTH_VALIDATOR_INVALID_FIELDS("#010101"),

    AUTH_USER_NOT_FOUND("#010201"),
    AUTH_INVALID_CREDENTIAL("#010202"),
    AUTH_JWT_PROCESSING_ERROR("#010203"),
    AUTH_USER_NOT_VERIFIED("#010204"),
    AUTH_USER_LOCKED("#010205"),
    AUTH_USER_DEACTIVATED("#010206"),

    AUTH_NOT_AUTHENTICATED("#010301"),
    AUTH_NOT_AUTHORIZED("#010302"),
    AUTH_INVALID_TOKEN("#010303"),
    AUTH_TOKEN_BLACKLISTED("#010304"),

    // Common
    DEFAULT("#000001"),
    SUCCESS("#000000"),

    // DB
    PERSISTENCE_ERROR("#000001"),
    INVALID_DATABASE_VALUE("#000002"),

    // Validation
    VALIDATION_INVALID_PARAMETERS("#000010");
    private final String value;

    ErrorCode(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
