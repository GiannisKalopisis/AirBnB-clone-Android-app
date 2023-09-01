package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.adapter.MessageRecyclerAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.MessageModel;
import com.example.fakebnb.model.request.MessageRequest;
import com.example.fakebnb.model.response.MessageResponse;
import com.example.fakebnb.rest.ChatAPI;
import com.example.fakebnb.rest.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndividualChatActivity extends AppCompatActivity {

    private static final String TAG = "IndividualChatPage";

    private EditText chat_message_input;
    private ImageButton message_send_btn, back_btn;
    private TextView receiver_username;
    private RecyclerView chat_recycler_view;


    // intent variables
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private Long receiverId;
    private Long chatId;

    // pagination
    private ArrayList<MessageModel> messageModel = new ArrayList<>();
    private MessageRecyclerAdapter messageRecyclerAdapter = new MessageRecyclerAdapter("michasgeo", "kalopisis");
//    private boolean isLoading = false;
    private int currentPage = 0; // Keeps track of the current page
    private int size = 20; // The number of items fetched per page
    private int lastVisibleItem = 0;
    private boolean isLoading = false;
    private List<MessageModel> messageResponseList = new ArrayList<>();
    private boolean isLastPage = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO: a call to take the senderID(userID),sencerUsername,receiverID,receiverUsername
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat);

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getSerializableExtra("user_id", Long.class);
            jwtToken = intent.getSerializableExtra("user_jwt", String.class);
            chatId = intent.getSerializableExtra("chat_id", Long.class);
            ArrayList<String> roleList = intent.getStringArrayListExtra("user_roles");
            if (roleList != null) {
                roles = new HashSet<>();
                for (String role : roleList) {
                    roles.add(RoleName.valueOf(role));
                }
            }
        }

        initView();

        receiver_username.setText("Test Username");
        backButtonOnClickListener();
        messageSendOnClickListener();

        chat_recycler_view.setAdapter(messageRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chat_recycler_view.setLayoutManager(layoutManager);

        // Initially load the first batch of data
        loadOlderData();
        loadOlderDataOnScroll();
    }

    /**
     * Load older data when scrolling up
     */
    private void loadOlderDataOnScroll() {
        chat_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) chat_recycler_view.getLayoutManager();
                int firstVisibleItem = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();
                int visibleItemCount = layoutManager.getChildCount();

                if (dy < 0 && !isLoading && firstVisibleItem <= visibleItemCount
                        && firstVisibleItem != lastVisibleItem && !isLastPage) {
                    loadOlderData();
                }

                lastVisibleItem = firstVisibleItem;
            }
        });
    }

    private void loadOlderData() {
        isLoading = true;

        // Calculate the offset before fetching newer data
        LinearLayoutManager layoutManager = (LinearLayoutManager) chat_recycler_view.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

        // Simulate fetching newer data from backend
        RestClient restClient = new RestClient(jwtToken);
        ChatAPI chatAPI = restClient.getClient().create(ChatAPI.class);

        chatAPI.getMessagesByChatId(chatId, currentPage, size)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getSuccess()) {
                                messageResponseList = response.body().getObject().getContent();
                                Collections.reverse(messageResponseList);
                                messageRecyclerAdapter.setMessageListModel((ArrayList<MessageModel>) messageResponseList);
                                isLastPage = response.body().getObject().isLast();
                            } else {
                                Toast.makeText(IndividualChatActivity.this, "1 Couldn't get messages", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(IndividualChatActivity.this, "2 Couldn't get messages", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "3 Couldn't get messages, " + t.getMessage());
                        Toast.makeText(IndividualChatActivity.this, "3 Couldn't get messages", Toast.LENGTH_SHORT).show();
                    }
                });

        isLoading = false;
        currentPage++;

        // Restore scroll position
        if (lastVisibleItemPosition != RecyclerView.NO_POSITION) {
            layoutManager.scrollToPosition(lastVisibleItemPosition + messageResponseList.size());
        }
    }

    /**
     * Send message to backend
     */
    private void sendMessageToUser(MessageRequest messageRequest) {
        RestClient restClient = new RestClient(jwtToken);
        ChatAPI chatAPI = restClient.getClient().create(ChatAPI.class);

        chatAPI.createMessage(messageRequest)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(IndividualChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(IndividualChatActivity.this, "Message not sent", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                        Toast.makeText(IndividualChatActivity.this, "Message not sent", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }

    private MessageRequest createMessageRequest() {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setReceiverUserRegId(receiverId);
        messageRequest.setContent(chat_message_input.getText().toString().trim());
        if (messageRequest.getContent().isEmpty()) {
            return null;
        }
        return messageRequest;
    }

    private void messageSendOnClickListener() {
        message_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageRequest messageRequest = createMessageRequest();
                if (messageRequest == null) {
                    return;
                }
                sendMessageToUser(messageRequest);
            }
        });
    }

    private void initView() {
        chat_message_input = findViewById(R.id.chat_message_input);
        message_send_btn = findViewById(R.id.message_send_btn);
        back_btn = findViewById(R.id.back_btn);
        receiver_username = findViewById(R.id.other_username);
        chat_recycler_view = findViewById(R.id.chat_recycler_view);
    }

    private void backButtonOnClickListener() {
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
