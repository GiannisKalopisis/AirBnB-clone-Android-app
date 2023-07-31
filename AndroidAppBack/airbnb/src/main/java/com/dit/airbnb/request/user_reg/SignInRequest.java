package com.dit.airbnb.request.user_reg;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class SignInRequest {

    @NotNull
    @Size(min = 4, max = 80)
    private String username;

    @NotNull
    @Size(min = 8, max = 45)
    private String password;

}