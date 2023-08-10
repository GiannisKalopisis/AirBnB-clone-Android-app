package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fakebnb.enums.RoleName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WriteReviewActivity extends AppCompatActivity {

    private static final String TAG = "WriteReviewPage";

    // user intent data
    private Long userId;
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

                // get Data
                String review = reviewText.getText().toString();
                float rating = ratingBar.getRating();

                // send Review to DB
                Log.d(TAG, "onClick: review: " + review);
                Log.d(TAG, "onClick: rating: " + rating);

                Intent submit_review_intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(submit_review_intent);
            }
        });
    }

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
                Intent chat_intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(chat_intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed PROFILE BUTTON", Toast.LENGTH_SHORT).show();
                Intent profile_intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile_intent);
            }
        });

        roleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
