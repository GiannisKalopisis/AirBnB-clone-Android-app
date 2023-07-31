package com.dit.airbnb.security.config;

public class SecurityConstants {
    public static final String SECRET = "6a4c7de71120afd3b87910b8cc3dbe1df535e4ddb21ec27994f75c18eb12d9c3fc56c94275ec4bc74057238d3293d21eecaad8b5471dd131665c651f39b18fcc";
    public static final long   EXPIRATION_TIME = 300_000_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String SIGN_UP_URL = "/app/user/signUp";
    public static final String SIGN_IN_URL = "/app/user/signIn";
}
