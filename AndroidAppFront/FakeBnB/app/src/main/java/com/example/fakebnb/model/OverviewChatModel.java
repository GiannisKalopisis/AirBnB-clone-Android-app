package com.example.fakebnb.model;

public class OverviewChatModel {

    private Long chatId;

    private String username;

    private String contentOfLastMessage;

    private Boolean seen;

    public OverviewChatModel(Long chatId, String username, String contentOfLastMessage, Boolean seen) {
        this.chatId = chatId;
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
