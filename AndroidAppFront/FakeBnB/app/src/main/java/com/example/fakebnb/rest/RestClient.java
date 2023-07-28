package com.example.fakebnb.rest;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private final String BASE_URL = "http://192.168.1.6:8080/";
    private Retrofit retrofit = null;

    public RestClient () {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .build();
        }
    }

    public Retrofit getClient() {
        return retrofit;
    }

}
