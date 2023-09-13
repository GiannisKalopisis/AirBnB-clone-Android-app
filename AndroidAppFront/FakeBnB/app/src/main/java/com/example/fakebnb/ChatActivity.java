package com.example.fakebnb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

import com.example.fakebnb.Callbacks.ImageLoadCallback;
import com.example.fakebnb.adapter.ChatRecyclerAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.OverviewChatModel;
import com.example.fakebnb.model.request.OverviewMessageRequest;
import com.example.fakebnb.model.response.OverviewChatResponse;
import com.example.fakebnb.rest.ChatAPI;
import com.example.fakebnb.rest.ImageAPI;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.utils.NavigationUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
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

    private int page = 0, size = 10;
    private List<OverviewChatModel> chats;

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




        chatRecyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initially load the first batch of data
        loadMoreChats();
        loadOlderChatOnScroll();
    }

    /**
     * Load older chat when the user scrolls to the bottom of the list
     */
    private void loadOlderChatOnScroll() {
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
                    loadMoreChats();
                }
            }
        });
    }

    private void loadMoreChats() {
        isLoading = true;

        // Simulate fetching data from backend
        RestClient restClient = new RestClient(jwtToken);
        ChatAPI chatAPI = restClient.getClient().create(ChatAPI.class);
        OverviewMessageRequest overviewMessageRequest = createOverviewMessageRequest();

        chatAPI.getOverviewMessagesByRegUserId(overviewMessageRequest, page, size)
                .enqueue(new Callback<OverviewChatResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OverviewChatResponse> call, @NonNull Response<OverviewChatResponse> response) {
                        if (response.isSuccessful()) {
                            OverviewChatResponse overviewChatResponse = response.body();
                            if (overviewChatResponse != null) {
                                Log.d(TAG, "onResponse: Success");
                                chats = overviewChatResponse.getObject().getContent();
                                for (OverviewChatModel chat : chats) {
                                    getUserImage(chat.getUserId(), new ImageLoadCallback() {
                                        @Override
                                        public void onImageLoaded(Bitmap userImageBitmap) {
                                            chatRecyclerAdapter.setUserImage(chat.getChatId(), userImageBitmap);
                                        }

                                        @Override
                                        public void onError(String errorMessage) {
                                            Toast.makeText(ChatActivity.this, "Error while downloading image: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                chatRecyclerAdapter.setChatListModel((ArrayList<OverviewChatModel>)  chats);
                            } else {
                                Log.d(TAG, "1 Couldn't fetch your chats");
                            }
                        } else {
                            Log.d(TAG, "2 Couldn't fetch your chats");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OverviewChatResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "onFailure: Couldn't fetch your chats, " + t.getMessage());
                    }
                });

        isLoading = false;
        page++;
    }

    private void getUserImage(Long userId, ImageLoadCallback callback) {
        RestClient restClient = new RestClient(jwtToken);
        ImageAPI imageAPI = restClient.getClient().create(ImageAPI.class);

        imageAPI.getImage(userId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Bitmap userImageBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            userImageBitmap = getCircularBitmap(userImageBitmap);
                            if (userImageBitmap != null) {
                                callback.onImageLoaded(userImageBitmap);
                            } else {
                                callback.onError("Couldn't process user image");
                            }
                        } else {
                            callback.onError("Couldn't get user image");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        callback.onError("Couldn't get user image: " + t.getMessage());
                    }
                });
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawCircle(width / 2f, height / 2f, width / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, 0, 0, paint);
        bitmap.recycle();

        return outputBitmap;
    }

    private OverviewMessageRequest createOverviewMessageRequest() {
        OverviewMessageRequest overviewMessageRequest = new OverviewMessageRequest();
        overviewMessageRequest.setRoleName(currentRole);
        return overviewMessageRequest;
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
                Toast.makeText(ChatActivity.this, "Already in Chat page", Toast.LENGTH_SHORT).show();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed PROFILE BUTTON", Toast.LENGTH_SHORT).show();
                NavigationUtils.goToProfilePage(ChatActivity.this, userId, jwtToken, roles, currentRole.toString());
            }
        });

        roleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: pressed role button");
                Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();

                if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                    if (currentRole == RoleName.ROLE_USER) {
                        NavigationUtils.goToHostMainPage(ChatActivity.this, userId, jwtToken, roles);
                    } else if (currentRole == RoleName.ROLE_HOST) {
                        NavigationUtils.goToMainPage(ChatActivity.this, userId, jwtToken, roles);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemClick(long chatId) {
        NavigationUtils.goToIndividualChatPage(ChatActivity.this, userId, jwtToken, roles, chatId, currentRole);
    }
}
