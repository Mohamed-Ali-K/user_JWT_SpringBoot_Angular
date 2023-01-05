package com.kenis.supportportal.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIM = 3_600_000; // 1 hour expressed in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String GET_ARRAY_LLC = "Get Array, LLC";
    public static final String GET_ARRAY_ADMINISTRATION = " User Management Portal";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "you do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {
            "/user/login",
            "user/register",
            "/user/ressetpassword/**",
            "/user/image/**"};
}
