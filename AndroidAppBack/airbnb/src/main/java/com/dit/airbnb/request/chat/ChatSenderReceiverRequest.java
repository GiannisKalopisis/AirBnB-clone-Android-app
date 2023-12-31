package com.dit.airbnb.request.chat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatSenderReceiverRequest {

    private Long senderId;

    private Long receiverId;

}
