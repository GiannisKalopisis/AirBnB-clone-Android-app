package com.example.fakebnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fakebnb.model.UserModel;
import com.example.fakebnb.utils.AndroidUtil;

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Button chatButton = findViewById(R.id.chatButton);
        Button profileButton = findViewById(R.id.profileButton);
        Button roleButton = findViewById(R.id.roleButton);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
//                Intent chat_intent = new Intent(getApplicationContext(), ChatActivity.class);
//                startActivity(chat_intent);

                // TODO: ONLY FOR TESTING PURPOSES. NEEDS TO BE REMOVED

//                go to reservation done page
//                Intent reservation_done_intent = new Intent(getApplicationContext(), ReservationDoneActivity.class);
//                startActivity(reservation_done_intent);

//                go to write review page
//                Intent write_review_intent = new Intent(getApplicationContext(), WriteReviewPage.class);
//                startActivity(write_review_intent);

//                go to add new place page
//                Intent add_new_place_intent = new Intent(getApplicationContext(), AddNewPlaceActivity.class);
//                startActivity(add_new_place_intent);

//                go to rent room page
//                Intent rent_room_intent = new Intent(getApplicationContext(), RentRoomPage.class);
//                startActivity(rent_room_intent);

//                go to chat page
                Intent individual_chat_intent = new Intent(getApplicationContext(), IndividualChatPage.class);
                AndroidUtil.passUserModelAsIntent(individual_chat_intent, new UserModel("Sakis Karpas"));
                startActivity(individual_chat_intent);
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
