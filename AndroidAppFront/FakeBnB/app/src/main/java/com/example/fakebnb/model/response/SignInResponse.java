package com.example.fakebnb.model.response;

public class SignInResponse {

    private Long id;

    private String jwtToken;

    private final String tokenType = "Bearer";

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    @Override
    public String toString() {
        return "SignInResponse{" +
                "id=" + id +
                ", jwtToken='" + jwtToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
