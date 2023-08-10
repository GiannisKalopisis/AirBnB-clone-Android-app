package com.example.fakebnb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.adapter.HostReviewAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.HostReviewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HostReviewPageActivity extends AppCompatActivity {

    private static final String TAG = "HostReviewPageActivity";

    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;

    // do it hard coded, then do it with the database and add image
    private TextView hostReviewUsernameView, hostReviewEmailView, hostReviewPhoneView;

    private RecyclerView reviewsRecyclerView;

    private Button chatButton, profileButton, roleButton;

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
        }

        bottomBarClickListeners();
        getTopInfo();
        // getReviews();
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

        HostReviewAdapter adapter = new HostReviewAdapter(reviews);
        reviewsRecyclerView.setAdapter(adapter);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @SuppressLint("SetTextI18n")
    private void getTopInfo() {
        //TODO: get the data from backend -> image, username, email, phone
        //TODO: render the data to frontend

        // all this get from backend
        hostReviewUsernameView.setText("Jane_Doe");
        hostReviewEmailView.setText("jane_doe@gmail.com");
        hostReviewPhoneView.setText("1234567890");
    }

    private void getReviews() {
        //TODO: get the data from backend -> username, review, stars (per review)
        //TODO: render the data to frontend
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
