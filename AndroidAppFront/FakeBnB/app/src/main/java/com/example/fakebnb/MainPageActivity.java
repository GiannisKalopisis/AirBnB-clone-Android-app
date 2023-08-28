package com.example.fakebnb;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.adapter.MainPageRentalAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.RentalMainPageModel;
import com.example.fakebnb.model.response.UserRegResponse;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.UserRegAPI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPageActivity extends AppCompatActivity implements MainPageRecyclerViewInterface {

    private static final String TAG = "MainPageActivity";

    // User variables for main page layout
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private UserRegResponse.UserRegData userRegData;

    private TextView welcomeMessage;
    private ImageView profile_pic_layout;
    private RecyclerView rentalsRecyclerView;
    private Button chatButton, profileButton, roleButton;
    private LinearLayout searchFieldsLayout;
    private boolean isSearchFieldsLayoutVisible = false;

    // search variables
    private EditText districtEditText, cityEditText, countryEditText, checkInDate, checkOutDate;
    private RadioGroup rentalTypeGroup;
    private Spinner numGuestsSpinner;
    private Button searchFieldsButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

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
                                bottomBarClickListeners();
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



        onDatesClicked();

        ArrayList<RentalMainPageModel> rentals = new ArrayList<>();
        rentals.add(new RentalMainPageModel("Amalfi1 coast rooms", "Αθήνα", "€ 50", 4.5f));
        rentals.add(new RentalMainPageModel("Amalfi2 coast rooms with a long description that might take up two lines", "Αθήνα", "€ 50", 4.5f));
        rentals.add(new RentalMainPageModel("Amalfi3 coast rooms with a long description that might take up two lines", "Αθήνα", "€ 50", 4.5f));
        rentals.add(new RentalMainPageModel("Amalfi4 coast rooms with a long description that might take up two lines", "Αθήνα", "€ 50", 4.5f));
        rentals.add(new RentalMainPageModel("Amalfi5 coast rooms with a long description that might take up two lines", "Αθήνα", "€ 50", 4.5f));
        rentals.add(new RentalMainPageModel("Amalfi6 coast rooms with a long description that might take up two lines", "Αθήνα", "€ 50", 4.5f));
        rentals.add(new RentalMainPageModel("Amalfi7 coast rooms with a long description that might take up two lines", "Αθήνα", "€ 50", 4.5f));
        rentals.add(new RentalMainPageModel("Amalfi8 coast rooms with a long description that might take up two lines", "Αθήνα", "€ 50", 4.5f));

        MainPageRentalAdapter adapter = new MainPageRentalAdapter(this, rentals);
        rentalsRecyclerView.setAdapter(adapter);
        rentalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setGuestNumSpinnerValues();
        searchFieldsButtonListener();
    }

    private void setGuestNumSpinnerValues() {
        String[] arraySpinner = new String[] {
                "Pick number of guests", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
        };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner){
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                // set the color of the first item in the drop-down list to gray
                if (position == 0) {
                    view.setTextColor(Color.GRAY);
                }

                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numGuestsSpinner.setAdapter(spinnerAdapter);
    }

    public void searchFieldsButtonListener() {
        searchFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notAllSearchFieldsCompleted()) {
                    Toast.makeText(view.getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                String district = districtEditText.getText().toString();
                String city = cityEditText.getText().toString();
                String country = countryEditText.getText().toString();
                String checkIn = checkInDate.getText().toString();
                String checkOut = checkOutDate.getText().toString();
                String rentalType = ((RadioButton) findViewById(rentalTypeGroup.getCheckedRadioButtonId())).getText().toString();
                String numGuests = numGuestsSpinner.getSelectedItem().toString();
                Log.d(TAG, "onClick: district: " + district);
                Log.d(TAG, "onClick: city: " + city);
                Log.d(TAG, "onClick: country: " + country);
                Log.d(TAG, "onClick: checkIn: " + checkIn);
                Log.d(TAG, "onClick: checkOut: " + checkOut);
                Log.d(TAG, "onClick: rentalType: " + rentalType);
                Log.d(TAG, "onClick: numGuests: " + numGuests);
                Toast.makeText(view.getContext(), "Pressed SEARCH BUTTON", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean notAllSearchFieldsCompleted() {
        return districtEditText.getText().toString().isEmpty() ||
                cityEditText.getText().toString().isEmpty() ||
                countryEditText.getText().toString().isEmpty() ||
                checkInDate.getText().toString().isEmpty() ||
                checkOutDate.getText().toString().isEmpty() ||
                numGuestsSpinner.getSelectedItemPosition() == 0 ||
                rentalTypeGroup.getCheckedRadioButtonId() == -1;
    }

    public void onSearchBarClicked(View view) {

        if (isSearchFieldsLayoutVisible) {
            // Hide the search fields layout with animation
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchFieldsLayout.setVisibility(View.GONE);
                }
            }, 100); // Adjust the delay time as needed
            isSearchFieldsLayoutVisible = false;
        } else {
            // Show the search fields layout with animation
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchFieldsLayout.setVisibility(View.VISIBLE);
                }
            }, 100); // Adjust the delay time as needed
            isSearchFieldsLayoutVisible = true;
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void getAndSetWelcomeData() {
        Log.d(TAG, "getAndSetWelcomeData: started");

        // get username and picture
        welcomeMessage.setText("Welcome " + userRegData.getUsername());

        Bitmap userImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.people1);
        userImageBitmap = getCircularBitmap(userImageBitmap);
        if (userImageBitmap != null) {
            profile_pic_layout.setImageBitmap(userImageBitmap);
            profile_pic_layout.setPadding(0, 0, 0, 0);
        }
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

    private void onDatesClicked() {
        Log.d(TAG, "onDatesClicked: started");

        checkInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        MainPageActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                checkInDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        },
                        year, month, day);
                // not allow older dates to be selected
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                // display date picker dialog.
                datePickerDialog.show();
            }
        });

        checkOutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInDate.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please select check in date first", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Calendar c = Calendar.getInstance();

                // Get the selected check-in date from checkInDate TextView and parse it to Calendar.
                String checkInDateText = checkInDate.getText().toString();
                String[] checkInDateParts = checkInDateText.split("-");
                int checkInDay = Integer.parseInt(checkInDateParts[0]);
                int checkInMonth = Integer.parseInt(checkInDateParts[1]) - 1; // Months are 0-based in Calendar.
                int checkInYear = Integer.parseInt(checkInDateParts[2]);
                c.set(checkInYear, checkInMonth, checkInDay);

                // Add one day to the check-in date to get the minimum date for checkOutDate.
                c.add(Calendar.DAY_OF_MONTH, 1);

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        MainPageActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                checkOutDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        },
                        year, month, day);
                // not allow older dates to be selected
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                // display date picker dialog.
                datePickerDialog.show();
            }
        });
    }

    private void initView() {
        Log.d(TAG, "initViews: started");

        // Get a reference to the search container
        searchFieldsLayout = findViewById(R.id.searchFieldsLayout);

        // Initializing TextView
        welcomeMessage = findViewById(R.id.welcomeMessage);
        profile_pic_layout = findViewById(R.id.profile_pic_image_view);

        // Initializing search values
        districtEditText = findViewById(R.id.districtEditText);
        cityEditText = findViewById(R.id.cityEditText);
        countryEditText = findViewById(R.id.countryEditText);
        rentalTypeGroup = findViewById(R.id.rentalTypeGroup);
        checkInDate = findViewById(R.id.checkInDate);
        checkOutDate = findViewById(R.id.checkOutDate);
        numGuestsSpinner = findViewById(R.id.numGuestsSpinner);
        searchFieldsButton = findViewById(R.id.searchFieldsButton);

        // Initializing recycler view
        rentalsRecyclerView = findViewById(R.id.rentalsRecyclerView);

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
                Intent chat_intent = new Intent(MainPageActivity.this, ChatActivity.class);
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
                Intent profile_intent = new Intent(MainPageActivity.this, ProfileActivity.class);
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
                    Intent host_main_page_intent = new Intent(MainPageActivity.this, HostMainPageActivity.class);
                    host_main_page_intent.putExtra("user_id", userId);
                    host_main_page_intent.putExtra("user_jwt", jwtToken);
                    ArrayList<String> roleList = new ArrayList<>();
                    for (RoleName role : roles) {
                        roleList.add(role.toString());
                    }
                    host_main_page_intent.putExtra("user_roles", roleList);
                    startActivity(host_main_page_intent);
                } else {
                    Toast.makeText(MainPageActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemClick(long rentalId) {
        Intent rent_room_intent = new Intent(MainPageActivity.this, RentRoomPage.class);
        rent_room_intent.putExtra("user_id", userId);
        rent_room_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        rent_room_intent.putExtra("user_roles", roleList);
        rent_room_intent.putExtra("rental_id", rentalId);
        startActivity(rent_room_intent);
    }
}
