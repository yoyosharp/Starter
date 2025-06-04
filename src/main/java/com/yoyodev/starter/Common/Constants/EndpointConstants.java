package com.yoyodev.starter.Common.Constants;

import java.util.List;

public class EndpointConstants {
    public static final String API_V1 = "/api/v1";

    public static final String AUTH = "/auth";
    public static final String API_V1_AUTH = API_V1 + AUTH;
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH_TOKEN = "/refresh-token";
    public static final String VERIFY = "/verify";

    public static final List<String> AUTH_ENDPOINTS = List.of(API_V1_AUTH + LOGIN,
            API_V1_AUTH + REGISTER,
            API_V1_AUTH + LOGOUT,
            API_V1_AUTH + REFRESH_TOKEN,
            API_V1_AUTH + VERIFY);

    public static final String USERS = "/users";
    public static final String API_V1_USERS = API_V1 + USERS;
    public static final String USER_BY_ID = "/{id}";
    public static final String USER_BY_USERNAME = "/by-username";
    public static final String ME = "/me";
}
