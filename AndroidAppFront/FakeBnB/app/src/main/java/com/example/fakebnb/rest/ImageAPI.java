package com.example.fakebnb.rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImageAPI {

    @GET("/app/image/{userId}")
    Call<ResponseBody> getImage(@Path(value = "userId") Long userId);
}
