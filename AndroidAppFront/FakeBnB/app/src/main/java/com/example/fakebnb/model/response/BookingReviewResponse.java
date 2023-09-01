package com.example.fakebnb.model.response;

import com.example.fakebnb.model.BookingReviewModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BookingReviewResponse implements Serializable {

    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("object")
    private PagedResponse<BookingReviewModel> object;

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

    public PagedResponse<BookingReviewModel> getObject() {
        return object;
    }

    public void setObject(PagedResponse<BookingReviewModel> object) {
        this.object = object;
    }

    public class PagedResponse <T>  implements Serializable {

        @SerializedName("content")
        private List<T> content;

        @SerializedName("page")
        private int page;

        @SerializedName("size")
        private int size;

        @SerializedName("totalElements")
        private long totalElements;

        @SerializedName("totalPages")
        private int totalPages;

        @SerializedName("last")
        private boolean last;

        public List<T> getContent() {
            return content;
        }

        public void setContent(List<T> content) {
            this.content = content;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }
    }
}
