package com.example.fakebnb.model.request;

import com.example.fakebnb.enums.RoleName;

public class MessageRequest {

    private Long receiverUserRegId;
    private String content;
    private RoleName currentSenderRole;

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

    public RoleName getCurrentRole() {
        return currentSenderRole;
    }

    public void setCurrentRole(RoleName currentSenderRole) {
        this.currentSenderRole = currentSenderRole;
    }
}
