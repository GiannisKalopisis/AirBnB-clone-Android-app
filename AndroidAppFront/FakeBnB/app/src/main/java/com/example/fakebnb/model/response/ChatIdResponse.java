package com.example.fakebnb.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatIdResponse implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private Long object;

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

    public Long getObject() {
        return object;
    }

    public void setObject(Long object) {
        this.object = object;
    }
}
