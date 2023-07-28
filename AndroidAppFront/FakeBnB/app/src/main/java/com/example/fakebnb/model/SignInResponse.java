package com.example.fakebnb.model;

public class SignInResponse {

    private Long id;

    private String jwtToken;

    private final String tokenType = "Bearer";

    private String username;

    private String email;

    private String firstName;

    private String lastName;
}
