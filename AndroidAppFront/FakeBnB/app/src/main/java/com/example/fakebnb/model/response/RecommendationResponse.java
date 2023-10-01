package com.example.fakebnb.model.response;

import com.example.fakebnb.model.SearchRentalModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RecommendationResponse implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private List<SearchRentalModel> object;

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

    public List<SearchRentalModel> getObject() {
        return object;
    }

    public void setObject(List<SearchRentalModel> object) {
        this.object = object;
    }
}
