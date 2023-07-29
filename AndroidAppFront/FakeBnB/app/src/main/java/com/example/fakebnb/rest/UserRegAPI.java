package com.example.fakebnb.rest;

import com.example.fakebnb.model.response.SignInResponse;
import com.example.fakebnb.model.UserLoginModel;
import com.example.fakebnb.model.UserRegisterModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserRegAPI {

    @POST("/app/signUp")
    Call<UserRegisterModel> registerUser(@Body UserRegisterModel userRegisterModel);

    @POST("app/signIn")
    Call<SignInResponse> singInUser(@Body UserLoginModel userLoginModel);
}
