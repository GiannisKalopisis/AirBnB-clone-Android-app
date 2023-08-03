package com.dit.airbnb.request.user_reg;

import com.dit.airbnb.dto.enums.RoleName;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
public class UserRegUpdateRequest {

    private String firstName;

    private String lastName;

    private String phone;

    @Enumerated
    private Set<RoleName> roleNames;

}
