package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.fakebnb.enums.RoleName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ReservationDoneActivity extends AppCompatActivity {

    private static final String TAG = "ReservationDoneActivity";

    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private Long apartmentId;

    private Button reservationDoneHomeButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_done);

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
            apartmentId = intent.getSerializableExtra("apartment_id", Long.class);
        }

        initView();

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
                Intent main_page_intent = new Intent(ReservationDoneActivity.this, MainPageActivity.class);
                main_page_intent.putExtra("user_id", userId);
                main_page_intent.putExtra("user_jwt", jwtToken);
                ArrayList<String> roleList = new ArrayList<>();
                for (RoleName role : roles) {
                    roleList.add(role.toString());
                }
                main_page_intent.putStringArrayListExtra("user_roles", roleList);
                startActivity(main_page_intent);
            }
        });
    }

    private void getAndRenderImage() {
        Log.d(TAG, "getData: started");
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.playAnimation();
    }

    private void initView() {
        Log.d(TAG, "initViews: started");
        reservationDoneHomeButton = findViewById(R.id.reservationDoneHomeButton);
    }
}
