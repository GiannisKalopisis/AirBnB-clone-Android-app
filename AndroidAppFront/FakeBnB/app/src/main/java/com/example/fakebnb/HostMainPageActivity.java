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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.adapter.HostMainPageRentalAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.HostRentalMainPageModel;
import com.example.fakebnb.model.response.UserRegResponse;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.UserRegAPI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostMainPageActivity extends AppCompatActivity implements HostMainPageRecyclerViewInterface {

    private static final String TAG = "HostMainPageActivity";

    // User variables for main page layout
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private UserRegResponse.UserRegData userRegData;

    private TextView welcomeMessage;
    private ImageView host_profile_pic_layout;
    private RecyclerView hostRentalsRecyclerView;
    private Button chatButton, profileButton, roleButton, addRentalButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_main_page);

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

        RestClient restClient = new RestClient(jwtToken);
        UserRegAPI userRegAPI = restClient.getClient().create(UserRegAPI.class);

        userRegAPI.getUserReg(userId)
                .enqueue(new Callback<UserRegResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserRegResponse> call, @NonNull Response<UserRegResponse> response) {
                        if (response.isSuccessful()) {
                            UserRegResponse userRegResponse = response.body();
                            if (userRegResponse != null) {
                                Log.d("API_CALL", "GetUserReg successful");
                                userRegData = userRegResponse.getObject();
                                getAndSetWelcomeData();
                            } else {
                                // Handle unsuccessful response
                                Log.d("API_CALL", "GetUserReg failed");
                            }
                        } else {
                            // Handle unsuccessful response
                            Log.d("API_CALL", "GetUserReg failed");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserRegResponse> call, @NonNull Throwable t) {
                        // Handle failure
                        Log.e("API_CALL", "Error: " + t.getMessage());
                    }
                });

        bottomBarClickListeners();
        addButtonClickListener();

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

        welcomeMessage.setText("Welcome, " + userRegData.getUsername());
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
