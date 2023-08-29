package com.example.fakebnb.rest;

import com.example.fakebnb.model.request.BookingRequest;
import com.example.fakebnb.model.response.BookingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BookingAPI {

    @POST("/app/booking")
    Call<BookingResponse> createBooking(@Body BookingRequest bookingRequest);
}
