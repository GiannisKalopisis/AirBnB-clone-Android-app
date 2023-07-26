package com.dit.airbnb.security.config;

public class SecurityConstants {
    public static final String SECRET = "DB.JwtSecret-di-airbnb";
    public static final long   EXPIRATION_TIME = 300_000_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String SIGN_UP_URL = "/app/signUp";
    public static final String SIGN_IN_URL = "/app/signIn";
}
