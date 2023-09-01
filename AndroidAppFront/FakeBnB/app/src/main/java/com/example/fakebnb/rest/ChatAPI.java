package com.example.fakebnb.rest;

import com.example.fakebnb.model.request.ChatSenderReceiverRequest;
import com.example.fakebnb.model.request.MessageRequest;
import com.example.fakebnb.model.request.OverviewMessageRequest;
import com.example.fakebnb.model.response.ChatIdResponse;
import com.example.fakebnb.model.response.ChatInfoResponse;
import com.example.fakebnb.model.response.OverviewChatResponse;
import com.example.fakebnb.model.response.MessageResponse;
import com.example.fakebnb.model.response.SingleMessageResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatAPI {

    @POST("/app/chat/message")
    Call<SingleMessageResponse> createMessage(@Body MessageRequest messageRequest);

    @GET("/app/chat/all")
    Call<OverviewChatResponse> getOverviewMessagesByRegUserId(@Query("page") int page,
                                                              @Query("size") int size,
                                                              @Body OverviewMessageRequest overviewMessageRequest);

    @GET("/app/chat/{chatId}")
    Call<MessageResponse> getMessagesByChatId(@Path(value = "chatId") Long chatId,
                                              @Query("page") int page,
                                              @Query("size") int size);

    @POST("/app/chat/senderReceiver")
    Call<ChatIdResponse> getChatIdBySenderReceiver(@Body ChatSenderReceiverRequest chatSenderReceiverRequest);

    @GET("/app/chat/{chatId}/info")
    Call<ChatInfoResponse> getChatInfoByChatId(@Path(value = "chatId") Long chatId);
}
