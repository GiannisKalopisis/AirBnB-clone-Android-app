package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fakebnb.enums.RoleName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private Long userId;
    private String jwtToken;
    private RoleName currentRole;
    private Set<RoleName> roles;

    // bottom bar buttons
    private Button chatButton, profileButton, roleButton;

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

    }

    private void initView() {
        Log.d(TAG, "initView: started");

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
}
