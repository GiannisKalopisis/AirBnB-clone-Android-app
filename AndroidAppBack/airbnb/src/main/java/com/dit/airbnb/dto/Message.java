package com.dit.airbnb.dto;

import com.dit.airbnb.csv_dto.MessageCSV;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

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

    @CreatedDate
    private Timestamp timeSent;

    @Column(name = "isLastMessage")
    private Boolean isLastMessage;

    @PrePersist
    public void createSentTime() {
        timeSent = new Timestamp(System.currentTimeMillis());
    }


    public Message(String content) {
        this.content = content;
        this.seen = false;
        this.isLastMessage = true;
    }

    public Message(MessageCSV messageCSV) {
        this.content = messageCSV.getContent();
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
