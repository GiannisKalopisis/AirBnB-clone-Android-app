package com.dit.airbnb.request.user_reg;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class SignInRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;

}