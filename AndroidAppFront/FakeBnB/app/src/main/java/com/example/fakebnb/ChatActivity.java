package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.adapter.ChatRecyclerAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.MessageModel;
import com.example.fakebnb.model.OverviewChatModel;
import com.example.fakebnb.model.request.OverviewMessageRequest;
import com.example.fakebnb.model.response.OverviewChatResponse;
import com.example.fakebnb.model.response.MessageResponse;
import com.example.fakebnb.rest.ChatAPI;
import com.example.fakebnb.rest.RestClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements ChatRecyclerViewInterface {

    private static final String TAG = "ChatActivity";

    private Long userId;
    private String jwtToken;
    private RoleName currentRole;
    private Set<RoleName> roles;

    // bottom bar buttons
    private Button chatButton, profileButton, roleButton;

    private int page = 1, size = 10;
    private OverviewChatResponse.PagedResponse<OverviewChatModel> chats;

    private RecyclerView chatRecyclerView;

    // pagination
    private ArrayList<OverviewChatModel> overviewChatModel = new ArrayList<>();
    private ChatRecyclerAdapter chatRecyclerAdapter = new ChatRecyclerAdapter(this, overviewChatModel);
    private boolean isLoading = false;
    private int currentPage = 1; // Keeps track of the current page

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getSerializableExtra("user_id", Long.class);
            jwtToken = intent.getSerializableExtra("user_jwt", String.class);
            currentRole = RoleName.valueOf(intent.getStringExtra("user_current_role"));
            ArrayList<String> roleList = intent.getStringArrayListExtra("user_roles");
            if (roleList != null) {
                roles = new HashSet<>();
                for (String role : roleList) {
                    roles.add(RoleName.valueOf(role));
                }
            }
        }

        initView();
        bottomBarClickListener();


//        RestClient restClient = new RestClient(jwtToken);
//        ChatAPI chatAPI = restClient.getClient().create(ChatAPI.class);
//        OverviewMessageRequest overviewMessageRequest = new OverviewMessageRequest();
//        overviewMessageRequest.setRoleName(currentRole);
//
//        chatAPI.getOverviewMessagesByRegUserId(page, size, overviewMessageRequest)
//                .enqueue(new Callback<OverviewChatResponse>() {
//                    @Override
//                    public void onResponse(@NonNull Call<OverviewChatResponse> call, @NonNull Response<OverviewChatResponse> response) {
//                        if (response.isSuccessful()) {
//                            OverviewChatResponse overviewChatResponse = response.body();
//                            if (overviewChatResponse != null) {
//                                Log.d(TAG, "onResponse: Success");
//                                chats = overviewChatResponse.getObject();
//                            } else {
//                                Log.d(TAG, "1 Couldn't fetch your chats");
//                            }
//                        } else {
//                            Log.d(TAG, "2 Couldn't fetch your chats");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<OverviewChatResponse> call, @NonNull Throwable t) {
//                        Log.d(TAG, "onFailure: Couldn't fetch your chats, " + t.getMessage());
//                    }
//                });

//        ChatRecyclerAdapter chatRecyclerAdapter = new ChatRecyclerAdapter(this, chats);


        overviewChatModel.add(new OverviewChatModel(1L, "username1", "contentOfLastMessage1", true));
        overviewChatModel.add(new OverviewChatModel(2L, "username2", "contentOfLastMessage2", false));
        overviewChatModel.add(new OverviewChatModel(3L, "username3", "contentOfLastMessage3", true));
        overviewChatModel.add(new OverviewChatModel(4L, "username4", "contentOfLastMessage4", false));
        overviewChatModel.add(new OverviewChatModel(5L, "username5", "contentOfLastMessage5", true));
        overviewChatModel.add(new OverviewChatModel(6L, "username6", "contentOfLastMessage6", false));
        overviewChatModel.add(new OverviewChatModel(7L, "username7", "contentOfLastMessage7", true));
        overviewChatModel.add(new OverviewChatModel(8L, "username8", "contentOfLastMessage8", false));
        overviewChatModel.add(new OverviewChatModel(9L, "username9", "contentOfLastMessage9", true));
        overviewChatModel.add(new OverviewChatModel(10L, "username10", "contentOfLastMessage10", false));
//        overviewChatModel.add(new OverviewChatModel(11L, "username11", "contentOfLastMessage11", true));
//        overviewChatModel.add(new OverviewChatModel(12L, "username12", "contentOfLastMessage12", false));
//        overviewChatModel.add(new OverviewChatModel(13L, "username13", "contentOfLastMessage13", true));
//        overviewChatModel.add(new OverviewChatModel(14L, "username14", "contentOfLastMessage14", false));
//        overviewChatModel.add(new OverviewChatModel(15L, "username15", "contentOfLastMessage15", true));

        chatRecyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) chatRecyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    // Load more data when the user is near the end of the list
                    loadMoreData();
                }
            }
        });
        // Initially load the first batch of data
        loadMoreData();
    }

    private void loadMoreData() {
        isLoading = true;

        // Simulate fetching data from backend
        ArrayList<OverviewChatModel> newData = fetchDataFromBackend(currentPage);

        overviewChatModel.addAll(newData);
        chatRecyclerAdapter.notifyDataSetChanged();

        isLoading = false;
        currentPage++;
    }

    private ArrayList<OverviewChatModel> fetchDataFromBackend(int page) {
        // Simulate fetching data from backend based on the page number
        ArrayList<OverviewChatModel> newData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            newData.add(new OverviewChatModel((i + page * 10L),
                            "username" + (i + page * 10),
                            "contentOfLastMessage" + (i + page * 10),
                            i%2 == 0));
        }
        return newData;
    }

    private void initView() {
        Log.d(TAG, "initView: started");

        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        // bottom bar buttons
        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
    }

    private void bottomBarClickListener() {
        Log.d(TAG, "bottomBarClickListener: started");

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
                Intent chat_intent = new Intent(ChatActivity.this, ChatActivity.class);
                chat_intent.putExtra("user_id", userId);
                chat_intent.putExtra("user_jwt", jwtToken);
                chat_intent.putExtra("user_current_role", currentRole.toString());
                ArrayList<String> roleList = new ArrayList<>();
                for (RoleName role : roles) {
                    roleList.add(role.toString());
                }
                chat_intent.putExtra("user_roles", roleList);
                startActivity(chat_intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed PROFILE BUTTON", Toast.LENGTH_SHORT).show();
                Intent profile_intent = new Intent(ChatActivity.this, ProfileActivity.class);
                profile_intent.putExtra("user_id", userId);
                profile_intent.putExtra("user_jwt", jwtToken);
                profile_intent.putExtra("user_current_role", currentRole.toString());
                ArrayList<String> roleList = new ArrayList<>();
                for (RoleName role : roles) {
                    roleList.add(role.toString());
                }
                profile_intent.putStringArrayListExtra("user_roles", roleList);
                startActivity(profile_intent);
            }
        });

        roleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: pressed role button");
                Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();

                if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                    if (currentRole == RoleName.ROLE_USER) {
                        Intent host_main_page_intent = new Intent(ChatActivity.this, HostMainPageActivity.class);
                        host_main_page_intent.putExtra("user_id", userId);
                        host_main_page_intent.putExtra("user_jwt", jwtToken);
                        ArrayList<String> roleList = new ArrayList<>();
                        for (RoleName role : roles) {
                            roleList.add(role.toString());
                        }
                        host_main_page_intent.putExtra("user_roles", roleList);
                        startActivity(host_main_page_intent);
                    } else if (currentRole == RoleName.ROLE_HOST) {
                        Intent main_page_intent = new Intent(ChatActivity.this, MainPageActivity.class);
                        main_page_intent.putExtra("user_id", userId);
                        main_page_intent.putExtra("user_jwt", jwtToken);
                        ArrayList<String> roleList = new ArrayList<>();
                        for (RoleName role : roles) {
                            roleList.add(role.toString());
                        }
                        main_page_intent.putExtra("user_roles", roleList);
                        startActivity(main_page_intent);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemClick(long chatId) {
        Intent individual_chat_intent = new Intent(ChatActivity.this, IndividualChatActivity.class);
        individual_chat_intent.putExtra("user_id", userId);
        individual_chat_intent.putExtra("user_jwt", jwtToken);
        individual_chat_intent.putExtra("chat_id", chatId);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        individual_chat_intent.putExtra("user_roles", roleList);
        startActivity(individual_chat_intent);
    }
}
