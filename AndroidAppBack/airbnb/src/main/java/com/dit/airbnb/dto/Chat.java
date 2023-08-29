package com.dit.airbnb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // USER
    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_sender_user_reg_id")
    private UserReg firstSenderUserReg;

    // HOST
    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_receiver_user_reg_id")
    private UserReg firstReceiverUserReg;

    @Column(name = "creation_time")
    private Timestamp creationTime;

    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    @PrePersist
    public void createCreationTime() {
        creationTime = new Timestamp(System.currentTimeMillis());
    }

    public Chat(UserReg firstSenderUserReg, UserReg firstReceiverUserReg) {
        this.firstSenderUserReg = firstSenderUserReg;
        this.firstReceiverUserReg = firstReceiverUserReg;
    }

}
