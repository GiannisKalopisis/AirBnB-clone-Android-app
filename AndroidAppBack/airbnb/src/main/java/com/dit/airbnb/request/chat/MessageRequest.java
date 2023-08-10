package com.dit.airbnb.request.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MessageRequest {

    Long receiverUserRegId;

    String content;
}
