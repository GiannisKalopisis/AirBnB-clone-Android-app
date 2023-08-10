package com.dit.airbnb.response;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class UserRegResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String phone;

}
