package com.example.fakebnb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.adapter.HostMainPageRentalAdapter;
import com.example.fakebnb.model.HostRentalMainPageModel;

import java.util.ArrayList;

public class HostMainPageActivity extends AppCompatActivity implements HostMainPageRecyclerViewInterface {

    private static final String TAG = "HostMainPageActivity";

    private TextView welcomeMessage;
    private ImageView host_profile_pic_layout;
    private RecyclerView hostRentalsRecyclerView;
    private Button chatButton, profileButton, roleButton, addRentalButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_main_page);

        initView();
        bottomBarClickListeners();
        addButtonClickListener();
        getAndSetWelcomeData();

        ArrayList<HostRentalMainPageModel> hostRentals = new ArrayList<>();
        hostRentals.add(new HostRentalMainPageModel("Amalfi1 coast rooms", "Αθήνα", 4.5f));
        hostRentals.add(new HostRentalMainPageModel("Amalfi2 coast rooms with a long description that might take up two lines", "Αθήνα", 4.5f));
        hostRentals.add(new HostRentalMainPageModel("Amalfi3 coast rooms with a long description that might take up two lines", "Αθήνα", 4.5f));
        hostRentals.add(new HostRentalMainPageModel("Amalfi4 coast rooms with a long description that might take up two lines", "Αθήνα", 4.5f));

        HostMainPageRentalAdapter adapter = new HostMainPageRentalAdapter(this, hostRentals);
        hostRentalsRecyclerView.setAdapter(adapter);
        hostRentalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addButtonClickListener() {
        addRentalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HostMainPageActivity.this, "Add place button pressed", Toast.LENGTH_SHORT).show();
                Intent add_new_place_intent = new Intent(getApplicationContext(), AddNewPlaceActivity.class);
                startActivity(add_new_place_intent);
            }
        });
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void getAndSetWelcomeData() {
        Log.d(TAG, "getAndSetWelcomeData: started");

        // get username and picture
        String username = "Sakis Karpas";

        welcomeMessage.setText("Welcome, " + username);
//        Bitmap userImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.people1);
//        userImageBitmap = getCircularBitmap(userImageBitmap);
//        if (userImageBitmap != null) {
//            profile_pic_layout.setImageBitmap(userImageBitmap);
//            profile_pic_layout.setPadding(0, 0, 0, 0);
//        }
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

    private void initView() {
        Log.d(TAG, "initView: started");

        // Initializing TextView
        welcomeMessage = findViewById(R.id.welcomeHostMessage);
        host_profile_pic_layout = findViewById(R.id.profile_pic_image_view);

        // Initializing recycler view
        hostRentalsRecyclerView = findViewById(R.id.hostRentalsRecyclerView);

        // Initializing add rental button
        addRentalButton = findViewById(R.id.addRentalButton);

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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, PlaceModificationPageActivity.class);

        intent.putExtra("host_rental_id", position);
        startActivity(intent);
    }
}
