package com.example.fakebnb.model;

import java.security.Timestamp;
import java.time.LocalTime;

public class ChatMessageModel {

    private String message, senderId;
    private LocalTime timestamp;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderId, LocalTime timestamp) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalTime timestamp) {
        this.timestamp = timestamp;
    }
}
