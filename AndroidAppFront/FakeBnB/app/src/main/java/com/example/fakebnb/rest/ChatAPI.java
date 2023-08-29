package com.example.fakebnb.rest;

import com.example.fakebnb.model.request.MessageRequest;
import com.example.fakebnb.model.request.OverviewMessageRequest;
import com.example.fakebnb.model.response.OverviewChatResponse;
import com.example.fakebnb.model.response.MessageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatAPI {

    @POST("/app/chat/message")
    Call<MessageResponse> createMessage(@Body MessageRequest messageRequest);

    @GET("/app/chat/all")
    Call<OverviewChatResponse> getOverviewMessagesByRegUserId(@Query("page") int page,
                                                              @Query("size") int size,
                                                              @Body OverviewMessageRequest overviewMessageRequest);

    @GET("/app/chat/{chatId}")
    Call<OverviewChatResponse> getMessagesByChatId(@Query("page") int page,
                                                   @Query("size") int size,
                                                   @Path("chatId") Long chatId);
}