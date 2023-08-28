package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.fakebnb.model.UserModel;
import com.example.fakebnb.model.request.MessageRequest;
import com.example.fakebnb.model.response.MessageResponse;
import com.example.fakebnb.rest.ChatAPI;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndividualChatActivity extends AppCompatActivity {

    private static final String TAG = "IndividualChatPage";

    private UserModel otherUser;
    private EditText chat_message_input;
    private ImageButton message_send_btn, back_btn;
    private TextView other_username;
    private RecyclerView chat_recycler_view;


    // intent variables
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private Long otherUserId;
    private Long chatId;

    // polling variables
    private final Handler handler = new Handler();
    private final int delay = 3000; // 3 seconds

    // pagination
    private ArrayList<MessageModel> messageModel = new ArrayList<>();
    private MessageRecyclerAdapter messageRecyclerAdapter = new MessageRecyclerAdapter(messageModel, "user1", "user2");
//    private boolean isLoading = false;
    private int currentPage = 1; // Keeps track of the current page
    private int lastVisibleItem = 0;
    private boolean isLoading = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        // get UserModel from intent
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        other_username.setText("Test Username");
        backButtonOnClickListener();
        messageSendOnClickListener();

        Toast.makeText(this, "UserID: " + userId+"\nChatID: " + chatId, Toast.LENGTH_SHORT).show();

        /**
         * GET messages every 3 seconds
         */
        // To start the polling
//        handler.postDelayed(getMessagesCallRunnable, delay);

        // Dummy data for testing
        messageModel.add(new MessageModel("user1", "Hello"));
        messageModel.add(new MessageModel("user2", "Hi"));
        messageModel.add(new MessageModel("user1", "How are you?"));
        messageModel.add(new MessageModel("user2", "I'm fine, thanks!"));
        messageModel.add(new MessageModel("user2", "What about you?"));
        messageModel.add(new MessageModel("user1", "I'm fine too!"));
        messageModel.add(new MessageModel("user1", "What are you doing?"));
        messageModel.add(new MessageModel("user2", "I'm working on my project"));
        messageModel.add(new MessageModel("user1", "Good luck!"));
        messageModel.add(new MessageModel("user2", "Thanks!"));


        chat_recycler_view.setAdapter(messageRecyclerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chat_recycler_view.setLayoutManager(layoutManager);


        messageModel.add(new MessageModel("user2", "I'm going to sleep now"));
        messageModel.add(new MessageModel("user1", "Good night!"));
        messageModel.add(new MessageModel("user2", "Good night!"));
        messageModel.add(new MessageModel("user1", "See you tomorrow!"));
        messageModel.add(new MessageModel("user2", "See you!"));
        messageModel.add(new MessageModel("user1", "Bye!"));
        messageModel.add(new MessageModel("user2", "Bye!"));
        messageModel.add(new MessageModel("user1", "Bye!"));
        messageModel.add(new MessageModel("user2", "Bye!"));
        messageModel.add(new MessageModel("user1", "Bye!"));

        chat_recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) chat_recycler_view.getLayoutManager();
                int firstVisibleItem = Objects.requireNonNull(layoutManager).findFirstVisibleItemPosition();

                if (dy < 0 && !isLoading && firstVisibleItem == 0 && firstVisibleItem != lastVisibleItem) {
                    loadOlderData();
                }

                lastVisibleItem = firstVisibleItem;
            }
        });

        // Initially load the first batch of data
        loadOlderData();
    }

    private void loadOlderData() {
        isLoading = true;

        // Calculate the offset before fetching newer data
        LinearLayoutManager layoutManager = (LinearLayoutManager) chat_recycler_view.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

        // Simulate fetching newer data from backend
        ArrayList<MessageModel> olderData = fetchOlderMessagesFromBackend(currentPage);
        Collections.reverse(olderData);

        // Add older messages at the beginning of the list
        messageModel.addAll(0, olderData);
        messageRecyclerAdapter.notifyItemRangeInserted(0, olderData.size());

        isLoading = false;
        currentPage++;

        // Restore scroll position
        if (lastVisibleItemPosition != RecyclerView.NO_POSITION) {
            layoutManager.scrollToPosition(lastVisibleItemPosition + olderData.size());
        }
    }

    private ArrayList<MessageModel> fetchOlderMessagesFromBackend(int page) {
        // Simulate fetching newer data from backend based on the page number
        ArrayList<MessageModel> newerData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            newerData.add(new MessageModel("user" + i % 2, "Newer Item " + (i + page * 10)));
        }
        return newerData;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(getMessagesCallRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(getMessagesCallRunnable);
    }

    private final Runnable getMessagesCallRunnable = new Runnable() {
        @Override
        public void run() {
            // Perform your API call here
            // Update the conversation if necessary

            // Schedule the next API call
            handler.postDelayed(this, delay);
        }
    };

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
        messageRequest.setReceiverUserRegId(otherUserId);
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
        other_username = findViewById(R.id.other_username);
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
