package com.example.fakebnb.model;

public class OverviewChatModel {

    private Long chatId;

    private Long userId;

    private String username;

    private String contentOfLastMessage;

    private Boolean seen;

    public OverviewChatModel(Long chatId, Long userId, String username, String contentOfLastMessage, Boolean seen) {
        this.chatId = chatId;
        this.userId = userId;
        this.username = username;
        this.contentOfLastMessage = contentOfLastMessage;
        this.seen = seen;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContentOfLastMessage() {
        return contentOfLastMessage;
    }

    public void setContentOfLastMessage(String contentOfLastMessage) {
        this.contentOfLastMessage = contentOfLastMessage;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
