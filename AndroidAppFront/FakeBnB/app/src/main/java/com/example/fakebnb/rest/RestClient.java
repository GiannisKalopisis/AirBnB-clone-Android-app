package com.example.fakebnb.rest;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private final String BASE_URL = "http://192.168.1.6:8080/";
    private Retrofit retrofit = null;
    private String authToken = null;
    private String type = null;

    public RestClient () {
        initializeRetrofit();
    }

    public RestClient(String authToken) {
        this.authToken = authToken;
        initializeRetrofit();
    }

    private void initializeRetrofit() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if (authToken != null && type == null) {
            httpClient.addInterceptor(new AuthInterceptor(authToken));
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getClient() {
        return retrofit;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        // Rebuild the Retrofit instance whenever the token is set
        initializeRetrofit();
    }

}
