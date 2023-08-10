package com.example.fakebnb;

import android.Manifest;
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
import com.example.fakebnb.model.HostRoomModel;
import com.example.fakebnb.model.RentRoomModel;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PlaceModificationPageActivity extends AppCompatActivity {

    private static final String TAG = "PlaceModificationPage";

    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;

    // warning TextView messages
    private TextView modifyPlaceWarningAddress, modifyPlaceWarningDates,
            modifyPlaceWarningMaxVisitors, modifyPlaceWarningMinPrice, modifyPlaceWarningExtraCost,
            modifyPlaceWarningRentalType, modifyPlaceWarningPhotoUpload, modifyPlaceWarningRules,
            modifyPlaceWarningDescription, modifyPlaceWarningBeds, modifyPlaceWarningBedrooms,
            modifyPlaceWarningBathrooms, modifyPlaceWarningLivingRooms, modifyPlaceWarningArea;

    // EditTexts fields
    private EditText modifyPlaceAddress, modifyPlaceStartDate, modifyPlaceEndDate,
            modifyPlaceMaxVisitors, modifyPlaceMinPrice, modifyPlaceExtraCost,
            modifyPlacePhotoUpload, modifyPlaceRules,
            modifyPlaceDescription, modifyPlaceBeds, modifyPlaceBedrooms,
            modifyPlaceBathrooms, modifyPlaceLivingRooms, modifyPlaceArea;
    private RadioGroup modifyPlaceRentalTypeRadioGroup;
    // MapView
    private MapView modifyPlaceMapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean isMapReady = false;
    private String addressToShowOnMap;
    public GoogleMap googleMap;

    // page buttons
    private Button savePlaceChangesButton, deletePlaceButton;

    private HostRoomModel hostRoomModel;


    // bottom buttons
    private Button chatButton, profileButton, roleButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_modification);

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
        setTextWatchers();
        resetWarnVisibility();
        getDataFromDatabase();
        setDataToViews();
        modificationButtonsClickListener();
        savePlaceChangesButton.setVisibility(View.GONE);


        checkGoogleAPIAvailability();

        modifyPlaceMapView.onCreate(savedInstanceState);
        modifyPlaceMapView.getMapAsync(new OnMapReadyCallback() {
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

    // Map methods
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
        modifyPlaceMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        modifyPlaceMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modifyPlaceMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        modifyPlaceMapView.onLowMemory();
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


    private void setDataToViews() {
        modifyPlaceAddress.setText(hostRoomModel.getAddress());
        modifyPlaceStartDate.setText(hostRoomModel.getStartDate());
        modifyPlaceEndDate.setText(hostRoomModel.getEndDate());
        modifyPlaceMaxVisitors.setText(String.valueOf(hostRoomModel.getMaxVisitors()));
        modifyPlaceMinPrice.setText(String.valueOf(hostRoomModel.getMinPrice()));
        modifyPlaceExtraCost.setText(String.valueOf(hostRoomModel.getExtraCost()));
        modifyPlacePhotoUpload.setText(hostRoomModel.getPhotoUpload());
        modifyPlaceRules.setText(hostRoomModel.getRules());
        modifyPlaceDescription.setText(hostRoomModel.getDescription());
        modifyPlaceBeds.setText(String.valueOf(hostRoomModel.getBeds()));
        modifyPlaceBedrooms.setText(String.valueOf(hostRoomModel.getBedrooms()));
        modifyPlaceBathrooms.setText(String.valueOf(hostRoomModel.getBathrooms()));
        modifyPlaceLivingRooms.setText(String.valueOf(hostRoomModel.getLivingRooms()));
        modifyPlaceArea.setText(String.valueOf(hostRoomModel.getArea()));
    }

    private void getDataFromDatabase() {
        Log.d(TAG, "getDataFromDatabase: started");

        // get Data from database
        hostRoomModel = new HostRoomModel("Sperchiou 70, Peristeri", "24-7-2023", "29-7-2023", "Room",
                "photo_path.png", "Rule1, Rule2, Rule3", "wqefqxc wedfqw eweeq qwef qwef qwef qwefsd",
                4, 4, 3, 2, 1, 150, 25, 70);
    }

    private void sendDataToDatabase(String address, String startDate, String endDate,
                                    int maxVisitors, float minPrice, float extraCost,
                                    String rentalType, String photoUpload, String rules,
                                    String description, int beds, int bedrooms, int bathrooms,
                                    int livingRooms, float area) {
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


    private void setTextWatchers() {
        Log.d(TAG, "setTextWatchers: started");

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!modifyPlaceAddress.getText().toString().isEmpty()) {
                    modifyPlaceWarningAddress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceAddress.getText().toString().isEmpty()) {
                    modifyPlaceWarningAddress.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningAddress.setVisibility(View.GONE);
                    addressToShowOnMap = modifyPlaceAddress.getText().toString();
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                    Toast.makeText(PlaceModificationPageActivity.this, String.valueOf(googleMap), Toast.LENGTH_SHORT).show();
                    if (isMapReady && googleMap != null) {
                        showAddressOnMap(addressToShowOnMap);
                    }
                }
            }
        };
        modifyPlaceAddress.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherStartDate() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!modifyPlaceStartDate.getText().toString().isEmpty()) {
                    modifyPlaceWarningDates.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceStartDate.getText().toString().isEmpty()) {
                    modifyPlaceWarningDates.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningDates.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceStartDate.addTextChangedListener(textWatcher);;
    }

    private void setTextWatcherEndDate() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!modifyPlaceEndDate.getText().toString().isEmpty()) {
                    modifyPlaceWarningDates.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceEndDate.getText().toString().isEmpty()) {
                    modifyPlaceWarningDates.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningDates.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceEndDate.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherMaxVisitors() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceMaxVisitors.getText().toString().isEmpty()) {
                    modifyPlaceWarningMaxVisitors.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceMaxVisitors.getText().toString().isEmpty()) {
                    modifyPlaceWarningMaxVisitors.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningMaxVisitors.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceMaxVisitors.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherMinPrice() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceMinPrice.getText().toString().isEmpty()) {
                    modifyPlaceWarningMinPrice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceMinPrice.getText().toString().isEmpty()) {
                    modifyPlaceWarningMinPrice.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningMinPrice.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceMinPrice.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherExtraCost() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceExtraCost.getText().toString().isEmpty()) {
                    modifyPlaceWarningExtraCost.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceExtraCost.getText().toString().isEmpty()) {
                    modifyPlaceWarningExtraCost.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningExtraCost.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceExtraCost.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherPhotoUpload() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlacePhotoUpload.getText().toString().isEmpty()) {
                    modifyPlaceWarningPhotoUpload.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlacePhotoUpload.getText().toString().isEmpty()) {
                    modifyPlaceWarningPhotoUpload.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningPhotoUpload.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlacePhotoUpload.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherRules() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceRules.getText().toString().isEmpty()) {
                    modifyPlaceWarningRules.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceRules.getText().toString().isEmpty()) {
                    modifyPlaceWarningRules.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningRules.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceRules.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherDescription() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceDescription.getText().toString().isEmpty()) {
                    modifyPlaceWarningDescription.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceDescription.getText().toString().isEmpty()) {
                    modifyPlaceWarningDescription.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningDescription.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceDescription.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherBeds() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceBeds.getText().toString().isEmpty()) {
                    modifyPlaceWarningBeds.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceBeds.getText().toString().isEmpty()) {
                    modifyPlaceWarningBeds.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningBeds.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceBeds.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherBedrooms() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceBedrooms.getText().toString().isEmpty()) {
                    modifyPlaceWarningBedrooms.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceBedrooms.getText().toString().isEmpty()) {
                    modifyPlaceWarningBedrooms.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningBedrooms.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceBedrooms.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherBathrooms() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceBathrooms.getText().toString().isEmpty()) {
                    modifyPlaceWarningBathrooms.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {

                if (modifyPlaceBathrooms.getText().toString().isEmpty()) {
                    modifyPlaceWarningBathrooms.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningBathrooms.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceBathrooms.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherLivingRooms() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceLivingRooms.getText().toString().isEmpty()) {
                    modifyPlaceWarningLivingRooms.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceLivingRooms.getText().toString().isEmpty()) {
                    modifyPlaceWarningLivingRooms.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningLivingRooms.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceLivingRooms.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherArea() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence editable, int start, int count, int after) {
                if (!modifyPlaceArea.getText().toString().isEmpty()) {
                    modifyPlaceWarningArea.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence editable, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceArea.getText().toString().isEmpty()) {
                    modifyPlaceWarningArea.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningArea.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceArea.addTextChangedListener(textWatcher);
    }

    private void resetWarnVisibility() {
        Log.d(TAG, "resetWarnVisibility: started");

        modifyPlaceWarningAddress.setVisibility(View.GONE);
        modifyPlaceWarningDates.setVisibility(View.GONE);
        modifyPlaceWarningMaxVisitors.setVisibility(View.GONE);
        modifyPlaceWarningMinPrice.setVisibility(View.GONE);
        modifyPlaceWarningExtraCost.setVisibility(View.GONE);
        modifyPlaceWarningRentalType.setVisibility(View.GONE);
        modifyPlaceWarningPhotoUpload.setVisibility(View.GONE);
        modifyPlaceWarningRules.setVisibility(View.GONE);
        modifyPlaceWarningDescription.setVisibility(View.GONE);
        modifyPlaceWarningBeds.setVisibility(View.GONE);
        modifyPlaceWarningBedrooms.setVisibility(View.GONE);
        modifyPlaceWarningBathrooms.setVisibility(View.GONE);
        modifyPlaceWarningLivingRooms.setVisibility(View.GONE);
        modifyPlaceWarningArea.setVisibility(View.GONE);
    }

    private void initView() {
        Log.d(TAG, "initView: started");

        // initialize warning messages fields
        modifyPlaceWarningAddress = findViewById(R.id.modifyPlaceWarningAddress);
        modifyPlaceWarningDates = findViewById(R.id.modifyPlaceWarningDates);
        modifyPlaceWarningMaxVisitors = findViewById(R.id.modifyPlaceWarningMaxVisitors);
        modifyPlaceWarningMinPrice = findViewById(R.id.modifyPlaceWarningMinPrice);
        modifyPlaceWarningExtraCost = findViewById(R.id.modifyPlaceWarningExtraCost);
        modifyPlaceWarningRentalType = findViewById(R.id.modifyPlaceWarningRentalType);
        modifyPlaceWarningPhotoUpload = findViewById(R.id.modifyPlaceWarningPhotoUpload);
        modifyPlaceWarningRules = findViewById(R.id.modifyPlaceWarningRules);
        modifyPlaceWarningDescription = findViewById(R.id.modifyPlaceWarningDescription);
        modifyPlaceWarningBeds = findViewById(R.id.modifyPlaceWarningBeds);
        modifyPlaceWarningBedrooms = findViewById(R.id.modifyPlaceWarningBedrooms);
        modifyPlaceWarningBathrooms = findViewById(R.id.modifyPlaceWarningBathrooms);
        modifyPlaceWarningLivingRooms = findViewById(R.id.modifyPlaceWarningLivingRooms);
        modifyPlaceWarningArea = findViewById(R.id.modifyPlaceWarningArea);

        // initialize EditTexts fields
        modifyPlaceAddress = findViewById(R.id.modifyPlaceAddressEditText);
        modifyPlaceStartDate = findViewById(R.id.modifyStartDateEditText);
        modifyPlaceEndDate = findViewById(R.id.modifyEndDateEditText);
        modifyPlaceMaxVisitors = findViewById(R.id.modifyPlaceMaxVisitorsEditText);
        modifyPlaceMinPrice = findViewById(R.id.modifyPlaceMinPriceEditText);
        modifyPlaceExtraCost = findViewById(R.id.modifyPlaceExtraCostEditText);
        modifyPlaceRentalTypeRadioGroup = findViewById(R.id.modifyPlaceRentalTypeRadioGroup);
        modifyPlacePhotoUpload = findViewById(R.id.modifyPlacePhotoUploadEditText);
        modifyPlaceRules = findViewById(R.id.modifyPlaceRulesEditText);
        modifyPlaceDescription = findViewById(R.id.modifyPlaceDescriptionEditText);
        modifyPlaceBeds = findViewById(R.id.modifyPlaceBedsEditText);
        modifyPlaceBedrooms = findViewById(R.id.modifyPlaceBedroomsEditText);
        modifyPlaceBathrooms = findViewById(R.id.modifyPlaceBathroomsEditText);
        modifyPlaceLivingRooms = findViewById(R.id.modifyPlaceLivingRoomsEditText);
        modifyPlaceArea = findViewById(R.id.modifyPlaceAreaEditText);

        savePlaceChangesButton = findViewById(R.id.savePlaceChangesButton);
        deletePlaceButton = findViewById(R.id.deletePlaceButton);

        modifyPlaceMapView = findViewById(R.id.modifyPlaceMapView);


        // bottom buttons initialization
        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
    }

    private void modificationButtonsClickListener() {
        Log.d(TAG, "modificationButtonsClickListener: started");
        savePlaceChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if all fields are filled
                // send data to backend
                // not able to press the button if any field is not correctly filled
                resetWarnVisibility();
                sendDataToDatabase(modifyPlaceAddress.getText().toString(),
                        modifyPlaceStartDate.getText().toString(),
                        modifyPlaceEndDate.getText().toString(),
                        Integer.parseInt(modifyPlaceMaxVisitors.getText().toString()),
                        Float.parseFloat(modifyPlaceMinPrice.getText().toString()),
                        Float.parseFloat(modifyPlaceExtraCost.getText().toString()),
                        ((RadioButton) findViewById(modifyPlaceRentalTypeRadioGroup.getCheckedRadioButtonId())).getText().toString(),
                        modifyPlacePhotoUpload.getText().toString(),
                        modifyPlaceRules.getText().toString(),
                        modifyPlaceDescription.getText().toString(),
                        Integer.parseInt(modifyPlaceBeds.getText().toString()),
                        Integer.parseInt(modifyPlaceBedrooms.getText().toString()),
                        Integer.parseInt(modifyPlaceBathrooms.getText().toString()),
                        Integer.parseInt(modifyPlaceLivingRooms.getText().toString()),
                        Float.parseFloat(modifyPlaceArea.getText().toString()));
                Toast.makeText(PlaceModificationPageActivity.this, "Rental modified correctly", Toast.LENGTH_SHORT).show();
                // onBackPressed(); // -> not pressed the back. Have to remake the host main page
                Intent host_main_page_intent = new Intent(getApplicationContext(), HostMainPageActivity.class);
                startActivity(host_main_page_intent);
            }
        });

        deletePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete place from database
                resetWarnVisibility();
                Toast.makeText(PlaceModificationPageActivity.this, "Rental deleted correctly", Toast.LENGTH_SHORT).show();
                // onBackPressed(); // -> not pressed the back. Have to remake the host main page
                Intent host_main_page_intent = new Intent(getApplicationContext(), HostMainPageActivity.class);
                startActivity(host_main_page_intent);
            }
        });
    }

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");


        chatButton.setOnClickListener(view -> {
            resetWarnVisibility();
            Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
            Intent chat_intent = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(chat_intent);
        });

        profileButton.setOnClickListener(view -> {
            resetWarnVisibility();
            Toast.makeText(view.getContext(), "Pressed PROFILE BUTTON", Toast.LENGTH_SHORT).show();
            Intent profile_intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(profile_intent);
        });

        roleButton.setOnClickListener(view -> {
            resetWarnVisibility();
            Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();
            Intent role_intent = new Intent(getApplicationContext(), HostMainPageActivity.class);
            startActivity(role_intent);
        });
    }
}
