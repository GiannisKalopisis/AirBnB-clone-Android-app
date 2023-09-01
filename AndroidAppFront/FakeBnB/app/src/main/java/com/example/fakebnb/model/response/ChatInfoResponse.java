package com.example.fakebnb.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatInfoResponse implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private ChatInfo object;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatInfo getObject() {
        return object;
    }

    public void setObject(ChatInfo object) {
        this.object = object;
    }

    public class ChatInfo implements Serializable {

        @SerializedName("senderId")
        private Long senderId;
        @SerializedName("senderUsername")
        private String senderUsername;
        @SerializedName("receiverId")
        private Long receiverId;
        @SerializedName("receiverUsername")
        private String receiverUsername;

        public Long getSenderId() {
            return senderId;
        }

        public void setSenderId(Long senderId) {
            this.senderId = senderId;
        }

        public String getSenderUsername() {
            return senderUsername;
        }

        public void setSenderUsername(String senderUsername) {
            this.senderUsername = senderUsername;
        }

        public Long getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(Long receiverId) {
            this.receiverId = receiverId;
        }

        public String getReceiverUsername() {
            return receiverUsername;
        }

        public void setReceiverUsername(String receiverUsername) {
            this.receiverUsername = receiverUsername;
        }
    }
}
