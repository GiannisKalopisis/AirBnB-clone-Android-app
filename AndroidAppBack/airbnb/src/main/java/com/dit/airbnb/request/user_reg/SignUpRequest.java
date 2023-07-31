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
public class SignUpRequest {

    @NotNull
    @Size(min = 2, max = 80)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 80)
    private String lastName;

    @NotNull
    @Size(min = 2, max = 80)
    private String username;

    @NotNull
    @Size(min = 6, max = 45)
    private String password;

    @NotNull
    @Size(max = 80)
    @Email
    private String email;

    @Size(max = 45)
    private String phone;

    @Enumerated
    private Set<RoleName> roleNames;

}
