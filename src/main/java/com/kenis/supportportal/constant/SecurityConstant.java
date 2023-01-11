package com.kenis.supportportal.constant;

/**
 * This is a utility class that contains constants related to security for the application.
 *
 * @author Mohamed Ali Kenis
 * @version 1.0
 */
public class SecurityConstant {
    /**
     * The expiration time for JWTs in the application, in milliseconds.
     */
    public static final long EXPIRATION_TIM = 3_600_000; // 1 hour expressed in milliseconds

    /**
     * The prefix to be used in the 'Authorization' header of requests that contain a JWT.
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * The name of the header to be used to pass the JWT to the application.
     */
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";

    /**
     * The message to be shown when a JWT cannot be verified.
     */
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

    /**
     * The name of the company that owns the application.
     */
    public static final String GET_ARRAY_LLC = "Get Array, LLC";

    /**
     * The name of the administration portal for the application.
     */
    public static final String GET_ARRAY_ADMINISTRATION = " User Management Portal";

    /**
     * The key for the authorities (i.e., roles) claim in the JWT.
     */
    public static final String AUTHORITIES = "authorities";

    /**
     * The message to be shown when a user tries to access a protected resource without being authenticated.
     */
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";

    /**
     * The message to be shown when a user tries to access a resource that they do not have permission to access.
     */
    public static final String ACCESS_DENIED_MESSAGE = "you do not have permission to access this page";

    /**
     * The 'OPTIONS' HTTP method.
     */
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";

    /**
     * An array of URLs that are public and do not require authentication.
     */
    public static final String[] PUBLIC_URLS = {"/user/login", "/user/register", "/user/reset-password/**", "/user/image/**"};
}
