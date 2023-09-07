package com.example.fakebnb.rest;

import com.example.fakebnb.model.request.SearchRequest;
import com.example.fakebnb.model.response.SearchPagedResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SearchAPI {

    @POST("/app/search")
    Call<SearchPagedResponse> search(@Body SearchRequest searchRequest,
                                     @Query("page") int page,
                                     @Query("size") int size);
}
