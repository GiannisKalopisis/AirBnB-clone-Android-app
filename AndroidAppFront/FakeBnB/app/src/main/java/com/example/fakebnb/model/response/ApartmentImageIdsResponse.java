package com.example.fakebnb.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ApartmentImageIdsResponse implements Serializable {

    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private List<Long> object;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Long> getObject() {
        return object;
    }

    public void setObject(List<Long> object) {
        this.object = object;
    }
}
