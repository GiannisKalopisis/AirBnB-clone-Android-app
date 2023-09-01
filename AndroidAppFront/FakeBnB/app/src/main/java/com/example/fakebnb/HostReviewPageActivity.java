package com.example.fakebnb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.adapter.HostReviewAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.BookingReviewModel;
import com.example.fakebnb.model.HostReviewModel;
import com.example.fakebnb.model.MessageModel;
import com.example.fakebnb.model.response.BookingReviewResponse;
import com.example.fakebnb.model.response.UserRegResponse;
import com.example.fakebnb.rest.BookingAPI;
import com.example.fakebnb.rest.BookingReviewAPI;
import com.example.fakebnb.rest.ChatAPI;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.UserRegAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostReviewPageActivity extends AppCompatActivity {

    private static final String TAG = "HostReviewPageActivity";

    private Long userId, hostId, apartmentId;
    private String jwtToken;
    private Set<RoleName> roles;
    private UserRegResponse.UserRegData userRegData;

    // do it hard coded, then do it with the database and add image
    private TextView hostReviewUsernameView, hostReviewEmailView, hostReviewPhoneView;

    private RecyclerView reviewsRecyclerView;

    private Button chatButton, profileButton, roleButton;

    // Pagination
    private HostReviewAdapter reviewAdapter = new HostReviewAdapter();
    private int currentPage = 0; // Keeps track of the current page
    private int size = 20; // The number of items fetched per page
    private List<BookingReviewModel> reviewResponseList = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isLastPage = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_review_page);

        initView();

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getSerializableExtra("user_id", Long.class);
            jwtToken = intent.getSerializableExtra("user_jwt", String.class);
            ArrayList<String> roleList = intent.getStringArrayListExtra("user_roles");
            if (roleList != null) {
                roles = new HashSet<>();
                for (String role : roleList) {
                    roles.add(RoleName.valueOf(role));
                }
            }
            hostId = intent.getSerializableExtra("host_id", Long.class);
            apartmentId = intent.getSerializableExtra("apartment_id", Long.class);
        }

        bottomBarClickListeners();
        getTopInfo();
        ArrayList<HostReviewModel> reviews = new ArrayList<>();
        reviews.add(new HostReviewModel("SakisKarpas", 3.5f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("AlexKarpas", 2f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("Trigkakis", 4.5f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("Stratoulis", 2.5f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("MrBeast", 3f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("Anastasis", 1.5f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("DoctorPatsForTheWin", 5f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("Alina", 4f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("Gatsos", 3.8f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));
        reviews.add(new HostReviewModel("Paparis", 2f, "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia"));

        reviewsRecyclerView.setAdapter(reviewAdapter);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadReviews();
        loadReviewsOnScroll();
    }

    /**
     * Pagination for the reviews
     */
    private void loadReviews() {
        isLoading = true;

        RestClient restClient = new RestClient(jwtToken);
        BookingReviewAPI bookingReviewAPI = restClient.getClient().create(BookingReviewAPI.class);

        bookingReviewAPI.getBookingReviews(apartmentId, currentPage, size)
                .enqueue(new Callback<BookingReviewResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BookingReviewResponse> call, @NonNull Response<BookingReviewResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getSuccess()) {
                                reviewResponseList = response.body().getObject().getContent();
                                reviewAdapter.setReviewsListModel((ArrayList<BookingReviewModel>) reviewResponseList);
                                isLastPage = response.body().getObject().isLast();
                            } else {
                                Toast.makeText(HostReviewPageActivity.this, "1 Couldn't get messages", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HostReviewPageActivity.this, "2 Couldn't get messages", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BookingReviewResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "3 Failed to communicate with server and get reviews, " + t.getMessage());
                        Toast.makeText(HostReviewPageActivity.this, "3 Failed to communicate with server and get reviews", Toast.LENGTH_SHORT).show();
                    }
                });

        isLoading = false;
        currentPage++;
    }

    /**
     * Difference of onScrollStateChanged and onScrolled:
     * onScrollStateChanged: This method is typically used to detect when scrolling starts,
     *                       stops, or settles, and it's useful for triggering actions or
     *                       animations when the scroll state changes.
     * onScrolled: This method is called continuously as the RecyclerView is scrolled. It
     *             provides information about the current scroll position and the amount
     *             of scroll that has occurred and is useful for implementing features like
     *             infinite scrolling or updating UI elements as the user scrolls through a list.
     */
    private void loadReviewsOnScroll() {
        reviewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) reviewsRecyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && !isLastPage) {
                    // Load more data when the user is near the end of the list
                    loadReviews();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getTopInfo() {
        //TODO: get the data from backend -> image
        RestClient restClient = new RestClient(jwtToken);
        UserRegAPI userRegAPI = restClient.getClient().create(UserRegAPI.class);

        userRegAPI.getUserReg(hostId).enqueue(new Callback<UserRegResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserRegResponse> call, @NonNull Response<UserRegResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        userRegData = response.body().getObject();
                        hostReviewUsernameView.setText(userRegData.getUsername());
                        hostReviewEmailView.setText(userRegData.getEmail());
                        hostReviewPhoneView.setText(userRegData.getPhone());
                    } else {
                        Toast.makeText(HostReviewPageActivity.this, "Error getting user info", Toast.LENGTH_SHORT).show();
                        goToMainPage();
                    }
                } else {
                    Toast.makeText(HostReviewPageActivity.this, "Error getting user info", Toast.LENGTH_SHORT).show();
                    goToMainPage();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserRegResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Failed to connect to server and get user info, " + t.getMessage());
                Toast.makeText(HostReviewPageActivity.this, "Failed to connect to server and get user info", Toast.LENGTH_SHORT).show();
                goToMainPage();
            }
        });
    }

    private void goToMainPage() {
        Intent main_page_intent = new Intent(getApplicationContext(), MainPageActivity.class);
        main_page_intent.putExtra("user_id", userId);
        main_page_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        main_page_intent.putStringArrayListExtra("user_roles", roleList);
        startActivity(main_page_intent);
    }

    private void initView() {
        Log.d(TAG, "initViews: started");

        // top level info
        hostReviewUsernameView = findViewById(R.id.hostReviewUsernameView);
        hostReviewEmailView = findViewById(R.id.hostReviewEmailView);
        hostReviewPhoneView = findViewById(R.id.hostReviewPhoneView);

        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);

        // Initializing bottom bar buttons
        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
    }

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");
        // only role_user can be here

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
                Intent chat_intent = new Intent(HostReviewPageActivity.this, ChatActivity.class);
                chat_intent.putExtra("user_id", userId);
                chat_intent.putExtra("user_jwt", jwtToken);
                chat_intent.putExtra("user_current_role", RoleName.ROLE_USER.toString());
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
                Intent profile_intent = new Intent(HostReviewPageActivity.this, ProfileActivity.class);
                profile_intent.putExtra("user_id", userId);
                profile_intent.putExtra("user_jwt", jwtToken);
                profile_intent.putExtra("user_current_role", RoleName.ROLE_USER.toString());
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
                // only user can be at this page so go to main Host page
                if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                    // to be at this activity he has the user role
                    Intent host_main_page_intent = new Intent(HostReviewPageActivity.this, HostMainPageActivity.class);
                    host_main_page_intent.putExtra("user_id", userId);
                    host_main_page_intent.putExtra("user_jwt", jwtToken);
                    ArrayList<String> roleList = new ArrayList<>();
                    for (RoleName role : roles) {
                        roleList.add(role.toString());
                    }
                    host_main_page_intent.putExtra("user_roles", roleList);
                    startActivity(host_main_page_intent);
                } else {
                    Toast.makeText(HostReviewPageActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
