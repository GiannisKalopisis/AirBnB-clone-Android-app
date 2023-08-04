package com.example.fakebnb.rest;

import com.example.fakebnb.model.request.UserRegUpdateRequest;
import com.example.fakebnb.model.response.SignInResponse;
import com.example.fakebnb.model.request.UserLoginModel;
import com.example.fakebnb.model.request.UserRegisterModel;
import com.example.fakebnb.model.response.UserRegResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserRegAPI {

    @POST("/app/user/signUp")
    Call<UserRegisterModel> registerUser(@Body UserRegisterModel userRegisterModel);

    @POST("/app/user/signIn")
    Call<SignInResponse> singInUser(@Body UserLoginModel userLoginModel);

    @PUT("/app/user/{userId}")
    Call<Void> updateUserReg(@Path("userId") Long userId,
                             @Body UserRegUpdateRequest userRegUpdateRequest);

    @GET("/app/user/{userId}")
    Call<UserRegResponse> getUserReg(@Path("userId") Long userId);

}
