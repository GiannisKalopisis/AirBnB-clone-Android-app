package com.dit.airbnb.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponse {

    private Long id;

    private String jwtToken;

    private final String tokenType = "Bearer";

    private String username;

    private String email;

    private String firstName;

    private String lastName;

}