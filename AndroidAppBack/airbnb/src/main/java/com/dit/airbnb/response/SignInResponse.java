package com.dit.airbnb.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SignInResponse {

    private Long id;

    private String jwtToken;

    private final String tokenType = "Bearer";

    private String username;

    private String email;

    private String firstName;

    private String lastName;

}