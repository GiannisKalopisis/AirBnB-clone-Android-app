package com.dit.airbnb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "seen")
    private Boolean seen;

    @Column(name = "message")
    private String message;

    @Column(name = "timeSent")
    private Timestamp timeSent;

    @Column(name = "isLastMessage")
    private Boolean isLastMessage;

    // external tables
    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_reg_sender_id")
    private UserReg userRegSender;

    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_reg_receiver_id")
    private UserReg userRegReceiver;

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setUserRegSender(UserReg userRegSender) {
        this.userRegSender = userRegSender;
    }

    public void setUserRegReceiver(UserReg userRegReceiver) {
        this.userRegReceiver = userRegReceiver;
    }
}
