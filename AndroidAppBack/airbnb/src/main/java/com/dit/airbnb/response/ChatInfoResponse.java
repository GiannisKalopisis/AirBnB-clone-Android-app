package com.dit.airbnb.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class ChatInfoResponse {

    private Long senderId;

    private String senderUsername;

    private Long receiverId;

    private String receiverUsername;

}
