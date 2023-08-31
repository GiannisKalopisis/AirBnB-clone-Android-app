package com.example.fakebnb.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookingReviewResponse implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private BookingReviewData object;

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

    public BookingReviewData getObject() {
        return object;
    }

    public void setObject(BookingReviewData object) {
        this.object = object;
    }

    public class BookingReviewData implements Serializable {

        @SerializedName("id")
        private Long id;
        @SerializedName("rating")
        private Short rating;
        @SerializedName("description")
        private String description;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Short getRating() {
            return rating;
        }

        public void setRating(Short rating) {
            this.rating = rating;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
