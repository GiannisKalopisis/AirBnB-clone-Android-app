package com.example.fakebnb.rest;

import com.example.fakebnb.model.response.ApartmentImageIdsResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImageAPI {

    @GET("/app/image/user/{userId}")
    Call<ResponseBody> getImage(@Path(value = "userId") Long userId);

    @GET("/app/image/apartment/{imageId}")
    Call<ResponseBody> getApartmentImageByImageId(@Path(value = "imageId") Long imageId);

    @GET("/app/image/{apartmentId}")
    Call<ApartmentImageIdsResponse> getApartmentImageIds(@Path(value = "apartmentId") Long apartmentId);
}
