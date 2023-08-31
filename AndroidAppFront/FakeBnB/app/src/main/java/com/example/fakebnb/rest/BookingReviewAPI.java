package com.example.fakebnb.rest;

import com.example.fakebnb.model.request.BookingReviewRequest;
import com.example.fakebnb.model.response.AbleToReviewResponse;
import com.example.fakebnb.model.response.BookingReviewResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingReviewAPI {

    @POST("/app/bookingReview")
    Call<BookingReviewResponse> createBookingReview(@Body BookingReviewRequest bookingReviewRequest);

    @GET("/app/bookingReview/{apartmentId}/ableToReview")
    Call<AbleToReviewResponse> ableToReview(@Path("apartmentId") Long apartmentId);
}
