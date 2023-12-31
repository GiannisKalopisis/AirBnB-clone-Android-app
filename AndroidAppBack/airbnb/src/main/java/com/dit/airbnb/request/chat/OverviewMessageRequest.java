package com.dit.airbnb.request.chat;

import com.dit.airbnb.dto.enums.RoleName;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OverviewMessageRequest {

    @Enumerated
    private RoleName roleName;

}
