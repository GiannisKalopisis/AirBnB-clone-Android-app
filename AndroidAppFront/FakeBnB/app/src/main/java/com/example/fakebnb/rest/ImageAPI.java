package com.example.fakebnb.rest;

import java.io.FileNotFoundException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImageAPI {

    @GET("/app/image/user/{userId}")
    Call<ResponseBody> getImage(@Path(value = "userId") Long userId);

    @GET("/app/image/apartment/{apartmentId}")
    Call<List<ResponseBody>> getApartmentImages(@Path(value = "apartmentId") Long apartmentId);
}
