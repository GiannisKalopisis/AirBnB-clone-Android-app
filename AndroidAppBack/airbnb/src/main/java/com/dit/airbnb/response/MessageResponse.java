package com.dit.airbnb.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class MessageResponse {

    private Long chatId;

    private String username;

    private String contentOfLastMessage;

    private Boolean seen;

}
