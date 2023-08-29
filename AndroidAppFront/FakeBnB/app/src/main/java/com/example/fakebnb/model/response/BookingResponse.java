package com.example.fakebnb.model.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class BookingResponse implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private BookingData object;

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

    public BookingData getObject() {
        return object;
    }

    public void setObject(BookingData object) {
        this.object = object;
    }

    public class BookingData implements Serializable {

        @SerializedName("id")
        private Long id;

        @SerializedName("checkInDate")
        private Date checkInDate;

        @SerializedName("checkOutDate")
        private Date checkOutDate;

        @SerializedName("isReviewed")
        private Boolean isReviewed;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getCheckInDate() {
            return checkInDate;
        }

        public void setCheckInDate(Date checkInDate) {
            this.checkInDate = checkInDate;
        }

        public Date getCheckOutDate() {
            return checkOutDate;
        }

        public void setCheckOutDate(Date checkOutDate) {
            this.checkOutDate = checkOutDate;
        }

        public Boolean getReviewed() {
            return isReviewed;
        }

        public void setReviewed(Boolean reviewed) {
            isReviewed = reviewed;
        }
    }
}
