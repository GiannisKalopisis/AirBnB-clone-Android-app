package com.example.fakebnb.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

public class SingleMessageResponse implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private SingleMessage object;

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

    public SingleMessage getObject() {
        return object;
    }

    public void setObject(SingleMessage object) {
        this.object = object;
    }

    public class SingleMessage implements Serializable {

        @SerializedName("id")
        private Long id;
        @SerializedName("content")
        private String content;
        @SerializedName("seen")
        private Boolean seen;
        @SerializedName("timeSent")
        private Timestamp timeSent;
        @SerializedName("isLastMessage")
        private Boolean isLastMessage;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Boolean getSeen() {
            return seen;
        }

        public void setSeen(Boolean seen) {
            this.seen = seen;
        }

        public Timestamp getTimeSent() {
            return timeSent;
        }

        public void setTimeSent(Timestamp timeSent) {
            this.timeSent = timeSent;
        }

        public Boolean getLastMessage() {
            return isLastMessage;
        }

        public void setLastMessage(Boolean lastMessage) {
            isLastMessage = lastMessage;
        }
    }
}
