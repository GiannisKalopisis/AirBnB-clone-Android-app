package com.example.fakebnb.model.request;

public class MessageRequest {

    private Long receiverUserRegId;
    private String content;

    public Long getReceiverUserRegId() {
        return receiverUserRegId;
    }

    public void setReceiverUserRegId(Long receiverUserRegId) {
        this.receiverUserRegId = receiverUserRegId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
