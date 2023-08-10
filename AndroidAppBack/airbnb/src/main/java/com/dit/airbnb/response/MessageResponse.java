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

    private String username;

    private String contentOfLastMessage;

    private Boolean seen;

}
