package com.dit.airbnb.request.chat;

import com.dit.airbnb.dto.enums.RoleName;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    private Long receiverUserRegId;

    @NotNull
    @Enumerated
    private RoleName currentSenderRole;

    private String content;
}
