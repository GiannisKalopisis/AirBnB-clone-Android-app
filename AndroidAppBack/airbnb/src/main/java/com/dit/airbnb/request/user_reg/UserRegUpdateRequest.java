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

    @Size(min = 2, max = 80)
    private String firstName;

    @Size(min = 2, max = 80)
    private String lastName;

    @Size(min = 4, max = 80)
    private String username;

    @Size(min = 8, max = 45)
    private String password;

    @Size(max = 80)
    @Email
    private String email;

    @Size(max = 45)
    private String phone;

    @Enumerated
    private Set<RoleName> roleNames;

}
