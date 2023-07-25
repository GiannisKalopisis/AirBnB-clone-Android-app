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
import com.example.fakebnb.model.RentalMainPageModel;
import com.example.fakebnb.model.UserModel;
import com.example.fakebnb.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.Calendar;

public class MainPageActivity extends AppCompatActivity implements MainPageRecyclerViewInterface {

    private static final String TAG = "MainPageActivity";
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
        bottomBarClickListeners();
        getAndSetWelcomeData();
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
                if (districtEditText.getText().toString().isEmpty() ||
                        cityEditText.getText().toString().isEmpty() ||
                        countryEditText.getText().toString().isEmpty() ||
                        checkInDate.getText().toString().isEmpty() ||
                        checkOutDate.getText().toString().isEmpty() ||
                        numGuestsSpinner.getSelectedItemPosition() == 0 ||
                        rentalTypeGroup.getCheckedRadioButtonId() == -1) {
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
        String username = "Sakis Karpas";


        welcomeMessage.setText("Welcome, " + username);
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

//                go to modify rental page
                Intent modify_rental_intent = new Intent(getApplicationContext(), PlaceModificationPageActivity.class);
                startActivity(modify_rental_intent);

//                go to rent room page
//                Intent rent_room_intent = new Intent(getApplicationContext(), RentRoomPage.class);
//                startActivity(rent_room_intent);

//                go to chat page
//                Intent individual_chat_intent = new Intent(getApplicationContext(), IndividualChatPage.class);
//                AndroidUtil.passUserModelAsIntent(individual_chat_intent, new UserModel("Sakis Karpas"));
//                startActivity(individual_chat_intent);
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
                Log.d(TAG, "onClick: pressed role button");
                Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();

                // for debugging purposes go to Host page directly
                Intent host_main_page_intent = new Intent(getApplicationContext(), HostMainPageActivity.class);
                AndroidUtil.passUserModelAsIntent(host_main_page_intent, new UserModel("Sakis Karpas"));
                startActivity(host_main_page_intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, RentRoomPage.class);

        intent.putExtra("rental_id", position);
        startActivity(intent);
    }
}
