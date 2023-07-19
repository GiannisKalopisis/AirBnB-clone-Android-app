package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ReservationDoneActivity extends AppCompatActivity {

    private static final String TAG = "ReservationDoneActivity";

    private String image_url;

    private Button reservationDoneHomeButton;

    private Button chatButton, profileButton, roleButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_done);

        initView();
        bottomBarClickListeners();

        getAndRenderImage();

        setClickListener();
    }


    private void setClickListener() {
        Log.d(TAG, "setClickListener: started");

        reservationDoneHomeButton = findViewById(R.id.reservationDoneHomeButton);

        reservationDoneHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed HOME BUTTON", Toast.LENGTH_SHORT).show();
                Intent home_intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(home_intent);
            }
        });
    }

    private void getAndRenderImage() {
        Log.d(TAG, "getData: started");

        // get image from backend
        // set image to image_url
    }

    private void initView() {
        Log.d(TAG, "initViews: started");

        reservationDoneHomeButton = findViewById(R.id.reservationDoneHomeButton);

        // Initializing bottom bar buttons
        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
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
