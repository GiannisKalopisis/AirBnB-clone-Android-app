package com.example.fakebnb.rest;

import com.example.fakebnb.model.request.ApartmentRequest;
import com.example.fakebnb.model.response.ApartmentResponse;
import com.example.fakebnb.model.response.UserRegResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApartmentAPI {

    @POST("/app/apartment")
    Call<ApartmentResponse> createApartment(@Body ApartmentRequest apartmentRequest);

    @PUT("/app/apartment/{apartmentId}")
    Call<ApartmentResponse> updateApartment(@Path("apartmentId") Long apartmentId,
                                            @Body ApartmentRequest apartmentRequest);

    @GET("/app/apartment/{apartmentId}")
    Call<ApartmentResponse> getApartmentInfo(@Path("apartmentId") Long apartmentId);

    @DELETE("/app/apartment/{apartmentId}")
    Call<ApartmentResponse> deleteApartment(@Path("apartmentId") Long apartmentId);

    @GET("/app/apartment/{apartmentId}")
    Call<UserRegResponse> getHostId(@Path("apartmentId") Long apartmentId);
}
