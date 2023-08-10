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

    @Column(name = "content")
    private String content;

    @Column(name = "seen")
    private Boolean seen;

    @Column(name = "timeSent")
    private Timestamp timeSent;

    @Column(name = "isLastMessage")
    private Boolean isLastMessage;

    public Message(String content) {
        this.content = content;
        this.seen = false;
        this.isLastMessage = true;
    }

    // external tables
    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_reg_id")
    private UserReg senderUserReg;

}
