package com.dit.airbnb.request.user_reg;

import com.dit.airbnb.dto.enums.RoleName;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Data
@AllArgsConstructor
public class SignInRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;
    
}