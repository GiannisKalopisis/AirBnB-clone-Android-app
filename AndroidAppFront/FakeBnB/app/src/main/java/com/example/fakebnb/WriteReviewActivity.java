package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.request.BookingReviewRequest;
import com.example.fakebnb.model.response.BookingReviewResponse;
import com.example.fakebnb.rest.BookingReviewAPI;
import com.example.fakebnb.rest.RestClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewActivity extends AppCompatActivity {

    private static final String TAG = "WriteReviewPage";

    // user intent data
    private Long userId, rentalId;
    private String jwtToken;
    private Set<RoleName> roles;

    private EditText reviewText;
    private RatingBar ratingBar;

    private Button submitReviewButton;

    private Button chatButton, profileButton, roleButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

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
            rentalId = intent.getSerializableExtra("rental_id", Long.class);
        }

        initView();
        bottomBarClickListeners();

        submitReviewButtonClickListener();
    }

    private void initView() {
        Log.d(TAG, "initView: started");

        reviewText = findViewById(R.id.reviewInputText);
        ratingBar = findViewById(R.id.ratingBar);

        submitReviewButton = findViewById(R.id.submitReviewButton);

        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
    }

    private void submitReviewButtonClickListener() {
        Log.d(TAG, "submitReviewButtonClickListener: started");

        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed SUBMIT REVIEW BUTTON", Toast.LENGTH_SHORT).show();

                RestClient restClient = new RestClient(jwtToken);
                BookingReviewAPI bookingReviewAPI = restClient.getClient().create(BookingReviewAPI.class);
                BookingReviewRequest bookingReviewRequest = createBookingReviewRequest();

                bookingReviewAPI.createBookingReview(bookingReviewRequest)
                        .enqueue(new Callback<BookingReviewResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<BookingReviewResponse> call, @NonNull Response<BookingReviewResponse> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().getSuccess()) {
                                            Toast.makeText(WriteReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                                            goToMainPage();
                                        } else {
                                            Toast.makeText(WriteReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(WriteReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(WriteReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<BookingReviewResponse> call, @NonNull Throwable t) {
                                Log.d(TAG, "Failed to connect to server and submit review, " + t.getMessage());
                                Toast.makeText(WriteReviewActivity.this, "Failed to connect to server and submit review", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private BookingReviewRequest createBookingReviewRequest() {
        BookingReviewRequest bookingReviewRequest = new BookingReviewRequest();
        bookingReviewRequest.setApartmentId(rentalId);
        bookingReviewRequest.setDescription(reviewText.getText().toString());
        bookingReviewRequest.setRating((short) ratingBar.getRating());
        return bookingReviewRequest;
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

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");
        // only user_role can be here

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
                Intent chat_intent = new Intent(WriteReviewActivity.this, ChatActivity.class);
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
                Intent profile_intent = new Intent(WriteReviewActivity.this, ProfileActivity.class);
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
                Log.d(TAG, "onClick: pressed role button");
                Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();

                if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                    // to be at this activity he has the user role
                    Intent host_main_page_intent = new Intent(WriteReviewActivity.this, HostMainPageActivity.class);
                    host_main_page_intent.putExtra("user_id", userId);
                    host_main_page_intent.putExtra("user_jwt", jwtToken);
                    ArrayList<String> roleList = new ArrayList<>();
                    for (RoleName role : roles) {
                        roleList.add(role.toString());
                    }
                    host_main_page_intent.putExtra("user_roles", roleList);
                    startActivity(host_main_page_intent);
                } else {
                    Toast.makeText(WriteReviewActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
