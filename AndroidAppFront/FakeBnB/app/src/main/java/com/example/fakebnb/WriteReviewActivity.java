package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.fakebnb.utils.NavigationUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewActivity extends AppCompatActivity {

    private static final String TAG = "WriteReviewPage";

    // user intent data
    private Long userId, rentalId, hostId;
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
            hostId = intent.getSerializableExtra("host_id", Long.class);
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

        submitReviewButton.setOnClickListener(view -> {

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
                                        NavigationUtils.goToMainPage(WriteReviewActivity.this, userId, jwtToken, roles);
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
        });
    }

    private BookingReviewRequest createBookingReviewRequest() {
        BookingReviewRequest bookingReviewRequest = new BookingReviewRequest();
        bookingReviewRequest.setApartmentId(rentalId);
        bookingReviewRequest.setDescription(reviewText.getText().toString());
        bookingReviewRequest.setRating((short) ratingBar.getRating());
        return bookingReviewRequest;
    }

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");

        chatButton.setOnClickListener(view -> NavigationUtils.goToChatPage(WriteReviewActivity.this, userId, jwtToken, roles, RoleName.ROLE_USER.toString()));

        profileButton.setOnClickListener(view -> NavigationUtils.goToProfilePage(WriteReviewActivity.this, userId, jwtToken, roles, RoleName.ROLE_USER.toString()));

        roleButton.setOnClickListener(view -> {
            Log.d(TAG, "onClick: pressed role button");
            if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                NavigationUtils.goToHostMainPage(WriteReviewActivity.this, userId, jwtToken, roles);
            } else {
                Toast.makeText(WriteReviewActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
