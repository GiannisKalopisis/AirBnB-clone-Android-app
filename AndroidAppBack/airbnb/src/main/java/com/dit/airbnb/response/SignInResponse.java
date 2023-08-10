package com.dit.airbnb.response;

import com.dit.airbnb.dto.enums.RoleName;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Setter
@Getter
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

    @Enumerated
    private Set<RoleName> roleNames;

}