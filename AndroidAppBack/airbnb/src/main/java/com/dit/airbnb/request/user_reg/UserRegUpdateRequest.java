package com.dit.airbnb.request.user_reg;

import com.dit.airbnb.dto.enums.RoleName;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegUpdateRequest {

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    @Email
    private String email;

    private String phone;

    @Enumerated
    private Set<RoleName> roleNames;

}
