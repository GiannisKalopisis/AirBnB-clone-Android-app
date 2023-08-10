package com.example.fakebnb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fakebnb.enums.RoleName;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AddNewPlaceActivity extends AppCompatActivity {

    private static final String TAG = "AddNewPlaceActivity";

    // User variables for main page layout
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;

    private TextView addPlaceWarningAddress, addPlaceWarningDates, addPlaceWarningMaxVisitors,
            addPlaceWarningMinPrice, addPlaceWarningExtraCost, addPlaceWarningRentType,
            addPlaceWarningPhotoUpload, addPlaceWarningRules, addPlaceWarningDescription,
            addPlaceWarningBeds, addPlaceWarningBedrooms, addPlaceWarningBathrooms,
            addPlaceWarningLivingRooms, addPlaceWarningArea;

    private EditText addPlaceAddressEditText, startDateEditText, endDateEditText, addPlaceMaxVisitorsEditText,
            addPlaceMinPriceEditText, addPlaceExtraCostEditText, addPlacePhotoUploadEditText,
            addPlaceRulesEditText, addPlaceDescriptionEditText, addPlaceBedsEditText,
            addPlaceBedroomsEditText, addPlaceBathroomsEditText, addPlaceLivingRoomsEditText,
            addPlaceAreaEditText;

    private RadioGroup addPlaceRentalTypeRadioGroup;

    private RadioButton checkedRadioButton;

    private Button addPlaceButton;

    private Button chatButton, profileButton, roleButton;

    private boolean fieldsAreValid = false;

    // MapView
    private MapView addPlaceMapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean isMapReady = false;
    private String addressToShowOnMap;
    private GoogleMap googleMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);

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
        resetWarnVisibility();
        setTextWatchers();
        onDatesClicked();
        addPlaceButtonClickListener();

        checkGoogleAPIAvailability();

        addPlaceMapView.onCreate(savedInstanceState);
        addPlaceMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map; // Store the GoogleMap object in the global variable
                isMapReady = true; // Mark the map as ready
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                // Check if an address is available and show it on the map
                if (addressToShowOnMap != null) {
                    showAddressOnMap(addressToShowOnMap);
                }
            }
        });

        // Check for location permissions and request if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkGoogleAPIAvailability() {
        // Check for Google Play Services availability
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                Objects.requireNonNull(googleApiAvailability.getErrorDialog(this, resultCode, 1)).show();
            } else {
                Toast.makeText(this, "This device does not support Google Play Services.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addPlaceMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        addPlaceMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        addPlaceMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        addPlaceMapView.onLowMemory();
    }

    private void showAddressOnMap(String address) {
        if (isMapReady && googleMap != null) {
            googleMap.clear(); // Clear any existing markers on the map
            LatLng locationLatLng = getLocationFromAddress(address);
            if (locationLatLng != null) {
                googleMap.addMarker(new MarkerOptions().position(locationLatLng).title(address));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));
            }
        }
    }

    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addressList;
        LatLng locationLatLng = null;
        try {
            addressList = geocoder.getFromLocationName(strAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                locationLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationLatLng;
    }

    private void setTextWatchers() {
        setTextWatcherAddress();
        setTextWatcherStartDate();
        setTextWatcherEndDate();
        setTextWatcherMaxVisitors();
        setTextWatcherMinPrice();
        setTextWatcherExtraCost();
        setTextWatcherPhotoUpload();
        setTextWatcherRules();
        setTextWatcherDescription();
        setTextWatcherBeds();
        setTextWatcherBedrooms();
        setTextWatcherBathrooms();
        setTextWatcherLivingRooms();
        setTextWatcherArea();
    }

    private void setTextWatcherAddress() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceAddressEditText.getText().toString().isEmpty()) {
                    addPlaceWarningAddress.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningAddress.setVisibility(View.GONE);
                    addressToShowOnMap = addPlaceAddressEditText.getText().toString();
                    if (isMapReady && googleMap != null) {
                        showAddressOnMap(addressToShowOnMap);
                    }
                }
            }
        };
        addPlaceAddressEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherStartDate() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (startDateEditText.getText().toString().isEmpty()) {
                    addPlaceWarningDates.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningDates.setVisibility(View.GONE);
                }
            }
        };
        startDateEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherEndDate() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (endDateEditText.getText().toString().isEmpty()) {
                    addPlaceWarningDates.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningDates.setVisibility(View.GONE);
                }
            }
        };
        endDateEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherMaxVisitors() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceMaxVisitorsEditText.getText().toString().isEmpty()) {
                    addPlaceWarningMaxVisitors.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningMaxVisitors.setVisibility(View.GONE);
                }
            }
        };
        addPlaceMaxVisitorsEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherMinPrice() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceMinPriceEditText.getText().toString().isEmpty()) {
                    addPlaceWarningMinPrice.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningMinPrice.setVisibility(View.GONE);
                }
            }
        };
        addPlaceMinPriceEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherExtraCost() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceExtraCostEditText.getText().toString().isEmpty()) {
                    addPlaceWarningExtraCost.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningExtraCost.setVisibility(View.GONE);
                }
            }
        };
        addPlaceExtraCostEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherPhotoUpload() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlacePhotoUploadEditText.getText().toString().isEmpty()) {
                    addPlaceWarningPhotoUpload.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningPhotoUpload.setVisibility(View.GONE);
                }
            }
        };
        addPlacePhotoUploadEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherRules() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceRulesEditText.getText().toString().isEmpty()) {
                    addPlaceWarningRules.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningRules.setVisibility(View.GONE);
                }
            }
        };
        addPlaceRulesEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherDescription() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceDescriptionEditText.getText().toString().isEmpty()) {
                    addPlaceWarningDescription.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningDescription.setVisibility(View.GONE);
                }
            }
        };
        addPlaceDescriptionEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherBeds() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceBedsEditText.getText().toString().isEmpty()) {
                    addPlaceWarningBeds.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningBeds.setVisibility(View.GONE);
                }
            }
        };
        addPlaceBedsEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherBedrooms() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceBedroomsEditText.getText().toString().isEmpty()) {
                    addPlaceWarningBedrooms.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningBedrooms.setVisibility(View.GONE);
                }
            }
        };
        addPlaceBedroomsEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherBathrooms() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceBathroomsEditText.getText().toString().isEmpty()) {
                    addPlaceWarningBathrooms.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningBathrooms.setVisibility(View.GONE);
                }
            }
        };
        addPlaceBathroomsEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherLivingRooms() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceLivingRoomsEditText.getText().toString().isEmpty()) {
                    addPlaceWarningLivingRooms.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningLivingRooms.setVisibility(View.GONE);
                }
            }
        };
        addPlaceLivingRoomsEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherArea() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (addPlaceAreaEditText.getText().toString().isEmpty()) {
                    addPlaceWarningArea.setVisibility(View.VISIBLE);
                } else {
                    addPlaceWarningArea.setVisibility(View.GONE);
                }
            }
        };
        addPlaceAreaEditText.addTextChangedListener(textWatcher);
    }

    private void resetWarnVisibility() {
        addPlaceWarningAddress.setVisibility(View.GONE);
        addPlaceWarningDates.setVisibility(View.GONE);
        addPlaceWarningMaxVisitors.setVisibility(View.GONE);
        addPlaceWarningMinPrice.setVisibility(View.GONE);
        addPlaceWarningExtraCost.setVisibility(View.GONE);
        addPlaceWarningRentType.setVisibility(View.GONE);
        addPlaceWarningPhotoUpload.setVisibility(View.GONE);
        addPlaceWarningRules.setVisibility(View.GONE);
        addPlaceWarningDescription.setVisibility(View.GONE);
        addPlaceWarningBeds.setVisibility(View.GONE);
        addPlaceWarningBedrooms.setVisibility(View.GONE);
        addPlaceWarningBathrooms.setVisibility(View.GONE);
        addPlaceWarningLivingRooms.setVisibility(View.GONE);
        addPlaceWarningArea.setVisibility(View.GONE);
    }

    private void onDatesClicked() {
        Log.d(TAG, "onDatesClicked: started");

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        AddNewPlaceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                startDateEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        },
                        year, month, day);
                // not allow older dates to be selected
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                // display date picker dialog.
                datePickerDialog.show();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (startDateEditText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please select check in date first", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Calendar c = Calendar.getInstance();

                // Get the selected check-in date from checkInDate TextView and parse it to Calendar.
                String checkInDateText = startDateEditText.getText().toString();
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
                        AddNewPlaceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                endDateEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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

    private void addPlaceButtonClickListener() {
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateFields()) {
                    Toast.makeText(view.getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(view.getContext(), "Pressed ADD PLACE BUTTON", Toast.LENGTH_SHORT).show();

                // data fields
                String address, startDate, endDate, rentalType, rules, description, photoUpload;
                int maxVisitors, beds, bedrooms, bathrooms, livingRooms;
                float minPrice, extraCost, area;

                // read the data and send to the database
                address = addPlaceAddressEditText.getText().toString();
                startDate = startDateEditText.getText().toString();
                endDate = endDateEditText.getText().toString();
                String tempMaxVisitors = addPlaceMaxVisitorsEditText.getText().toString();
                try {
                    maxVisitors = Integer.parseInt(tempMaxVisitors);
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Max visitors must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tempMinPrice = addPlaceMinPriceEditText.getText().toString();
                try {
                    minPrice = Float.parseFloat(tempMinPrice);
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Min price must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tempExtraCost = addPlaceExtraCostEditText.getText().toString();
                try {
                    extraCost = Float.parseFloat(tempExtraCost);
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Extra cost must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                int checkedRadioButtonId = addPlaceRentalTypeRadioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId != -1) {
                    checkedRadioButton = findViewById(checkedRadioButtonId);
                    rentalType = checkedRadioButton.getText().toString();
                } else {
                    Toast.makeText(view.getContext(), "Please select a rental type", Toast.LENGTH_SHORT).show();
                    return;
                }
                photoUpload = addPlacePhotoUploadEditText.getText().toString();
                rules = addPlaceRulesEditText.getText().toString();
                description = addPlaceDescriptionEditText.getText().toString();
                String tempBeds = addPlaceBedsEditText.getText().toString();
                try {
                    beds = Integer.parseInt(tempBeds);
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Beds must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tempBedrooms = addPlaceBedroomsEditText.getText().toString();
                try {
                    bedrooms = Integer.parseInt(tempBedrooms);
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Bedrooms must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tempBathrooms = addPlaceBathroomsEditText.getText().toString();
                try {
                    bathrooms = Integer.parseInt(tempBathrooms);
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Bathrooms must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tempLivingRooms = addPlaceLivingRoomsEditText.getText().toString();
                try {
                    livingRooms = Integer.parseInt(tempLivingRooms);
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Living rooms must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }
                String tempArea = addPlaceAreaEditText.getText().toString();
                try {
                    area = Float.parseFloat(tempArea);
                } catch (NumberFormatException e) {
                    Toast.makeText(view.getContext(), "Area must be a number", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendDataToDatabase(address, startDate, endDate, maxVisitors, minPrice, extraCost, rentalType,
                        photoUpload, rules, description, beds, bedrooms, bathrooms,
                        livingRooms, area);

                Intent host_main_page_intent = new Intent(getApplicationContext(), HostMainPageActivity.class);
                startActivity(host_main_page_intent);
            }
        });
    }

    private void sendDataToDatabase(String address, String startDate, String endDate, int maxVisitors, float minPrice,
                                    float extraCost, String rentalType, String photoUpload,
                                    String rules, String description, int beds, int bedrooms,
                                    int bathrooms, int livingRooms, float area) {
        Log.d(TAG, "onClick: address: " + address);
        Log.d(TAG, "onClick: startDate: " + startDate);
        Log.d(TAG, "onClick: endDate: " + endDate);
        Log.d(TAG, "onClick: maxVisitors: " + maxVisitors);
        Log.d(TAG, "onClick: minPrice: " + minPrice);
        Log.d(TAG, "onClick: extraCost: " + extraCost);
        Log.d(TAG, "onClick: rentalType: " + rentalType);
        Log.d(TAG, "onClick: photoUpload: " + photoUpload);
        Log.d(TAG, "onClick: rules: " + rules);
        Log.d(TAG, "onClick: description: " + description);
        Log.d(TAG, "onClick: beds: " + beds);
        Log.d(TAG, "onClick: bedrooms: " + bedrooms);
        Log.d(TAG, "onClick: bathrooms: " + bathrooms);
        Log.d(TAG, "onClick: livingRooms: " + livingRooms);
        Log.d(TAG, "onClick: area: " + area);
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (addPlaceAddressEditText.getText().toString().isEmpty()) {
            addPlaceWarningAddress.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (startDateEditText.getText().toString().isEmpty()) {
            addPlaceWarningDates.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (endDateEditText.getText().toString().isEmpty()) {
            addPlaceWarningDates.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceMaxVisitorsEditText.getText().toString().isEmpty()) {
            addPlaceWarningMaxVisitors.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceMinPriceEditText.getText().toString().isEmpty()) {
            addPlaceWarningMinPrice.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceExtraCostEditText.getText().toString().isEmpty()) {
            addPlaceWarningExtraCost.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlacePhotoUploadEditText.getText().toString().isEmpty()) {
            addPlaceWarningPhotoUpload.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceRulesEditText.getText().toString().isEmpty()) {
            addPlaceWarningRules.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceDescriptionEditText.getText().toString().isEmpty()) {
            addPlaceWarningDescription.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceBedsEditText.getText().toString().isEmpty()) {
            addPlaceWarningBeds.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceBedroomsEditText.getText().toString().isEmpty()) {
            addPlaceWarningBedrooms.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceBathroomsEditText.getText().toString().isEmpty()) {
            addPlaceWarningBathrooms.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceLivingRoomsEditText.getText().toString().isEmpty()) {
            addPlaceWarningLivingRooms.setVisibility(View.VISIBLE);
            isValid = false;
        }
        if (addPlaceAreaEditText.getText().toString().isEmpty()) {
            addPlaceWarningArea.setVisibility(View.VISIBLE);
            isValid = false;
        }
        return isValid;
    }

    private void initView() {
        Log.d(TAG, "initView: started");

        // Warning TextViews
        addPlaceWarningAddress = findViewById(R.id.addPlaceWarningAddress);
        addPlaceWarningDates = findViewById(R.id.addPlaceWarningDates);
        addPlaceWarningMaxVisitors = findViewById(R.id.addPlaceWarningMaxVisitors);
        addPlaceWarningMinPrice = findViewById(R.id.addPlaceWarningMinPrice);
        addPlaceWarningExtraCost = findViewById(R.id.addPlaceWarningExtraCost);
        addPlaceWarningRentType = findViewById(R.id.addPlaceWarningRentType);
        addPlaceWarningPhotoUpload = findViewById(R.id.addPlaceWarningPhotoUpload);
        addPlaceWarningRules = findViewById(R.id.addPlaceWarningRules);
        addPlaceWarningDescription = findViewById(R.id.addPlaceWarningDescription);
        addPlaceWarningBeds = findViewById(R.id.addPlaceWarningBeds);
        addPlaceWarningBedrooms = findViewById(R.id.addPlaceWarningBedrooms);
        addPlaceWarningBathrooms = findViewById(R.id.addPlaceWarningBathrooms);
        addPlaceWarningLivingRooms = findViewById(R.id.addPlaceWarningLivingRooms);
        addPlaceWarningArea = findViewById(R.id.addPlaceWarningArea);

        // EditTexts
        addPlaceAddressEditText = findViewById(R.id.addPlaceAddressEditText);
//        addPlaceDatesEditText = findViewById(R.id.addPlaceDatesEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        addPlaceMaxVisitorsEditText = findViewById(R.id.addPlaceMaxVisitorsEditText);
        addPlaceMinPriceEditText = findViewById(R.id.addPlaceMinPriceEditText);
        addPlaceExtraCostEditText = findViewById(R.id.addPlaceExtraCostEditText);
        addPlacePhotoUploadEditText = findViewById(R.id.addPlacePhotoUploadEditText);
        addPlaceRulesEditText = findViewById(R.id.addPlaceRulesEditText);
        addPlaceDescriptionEditText = findViewById(R.id.addPlaceDescriptionEditText);
        addPlaceBedsEditText = findViewById(R.id.addPlaceBedsEditText);
        addPlaceBedroomsEditText = findViewById(R.id.addPlaceBedroomsEditText);
        addPlaceBathroomsEditText = findViewById(R.id.addPlaceBathroomsEditText);
        addPlaceLivingRoomsEditText = findViewById(R.id.addPlaceLivingRoomsEditText);
        addPlaceAreaEditText = findViewById(R.id.addPlaceAreaEditText);

        addPlaceRentalTypeRadioGroup = findViewById(R.id.addPlaceRentalTypeRadioGroup);

        // MapView
        addPlaceMapView = findViewById(R.id.addPlaceMapView);

        // Buttons
        addPlaceButton = findViewById(R.id.addPlaceButton);

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
