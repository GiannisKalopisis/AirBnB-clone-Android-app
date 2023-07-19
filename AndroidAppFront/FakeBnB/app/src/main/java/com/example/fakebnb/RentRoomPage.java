package com.example.fakebnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
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
import java.util.List;


public class RentRoomPage extends AppCompatActivity {

    private String TAG = "RentRoomPage";

    private TextView rentRoomPersonsValue, rentRoomBedsValue, rentRoomBathroomsValue,
            rentRoomBedroomsValue, rentRoomPriceValue, rentRoomExtraPriceValue, rentRoomFinalPriceValue,
            rentRoomDescriptionValue, rentRoomAddressValue, rentRoomAreaValue, rentRoomHostNameValue;

    private RecyclerView recyclerViewRules, recyclerViewAmenities;

    private MapView rentRoomMapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean isMapReady = false;

    private Button seeHostButton, contactHostButton, makeReservationButton, writeReviewButton;
    private Button chatButton, profileButton, roleButton;
    private RentHouseInfo info;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_room);

        initView();
        bottomBarClickListeners();

        // Initialize the MapView

        // Check for Google Play Services availability
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 1).show();
            } else {
                Toast.makeText(this, "This device does not support Google Play Services.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        // Check for location permissions and request if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Proceed with initializing the MapView and displaying the map
            initView();
            bottomBarClickListeners();

            createSlider();
            info = new RentHouseInfo(4, 2, 2, 100, 15,"Entire House",
                    "This is a descriptionwerg wgwe werg wergwer gwergwer gwerg werg wwerg werfgwefg wdfgsdfgwertg wergswdfg sdfg sdfg we ",
                    "These are the rules. These are the rules1. These are the rules2. These are the rules3. These are the rules4. These are the rules5.",
                    "Sperchiou 70, Peristeri 121 37", "These are the amenities. These are the amenities1. These are the amenities2. These are the amenities3. These are the amenities4.",
                    "photo_path.png", "Sakis Karpas");
            rentRoomMapView.onCreate(savedInstanceState);
            rentRoomMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    // Handle the map ready event here.
                    // Now you can use the "googleMap" instance to work with the map.
                    // Add the logic to display the address on the map here.
                    isMapReady = true; // Mark the map as ready
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    // Check if an address is available and show it on the map
                    if (info != null) {
                        showAddressOnMap(googleMap, info.getAddress());
                    }
                }
            });
            getDataFromDatabase();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rentRoomMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        rentRoomMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rentRoomMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        rentRoomMapView.onLowMemory();
    }

    private void showAddressOnMap(GoogleMap googleMap, String address) {
        if (isMapReady) {
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

    private void getDataFromDatabase() {
        // get the data
        // parse the data

        renderRoomInfoSection();
        renderPriceSection();
        renderDescriptionSection();
        renderRulesSection();
        renderLocationSection();
        renderAmenitiesSection();
        renderHostSection();
        renderReviewSection();
    }

    private void renderRoomInfoSection() {
        rentRoomPersonsValue.setText(String.valueOf(info.getMax_persons_allowed()));
        rentRoomBedsValue.setText(String.valueOf(info.getBathrooms()));
        rentRoomBathroomsValue.setText(String.valueOf(info.getBedrooms()));
        rentRoomBedroomsValue.setText(String.valueOf(info.getPrice()));
    }

    private void renderPriceSection() {
        rentRoomPriceValue.setText(String.valueOf(info.getPrice()));
        rentRoomExtraPriceValue.setText(String.valueOf(info.getExtra_person_cost()));
        rentRoomFinalPriceValue.setText(String.valueOf(info.getPrice() + info.getExtra_person_cost()));
    }

    private void renderDescriptionSection() {
        rentRoomDescriptionValue.setText(info.getDescription());
    }

    private void renderRulesSection() {
        String[] rulesArray = info.getRules().split("[\n.]");
        // Create an ArrayList to store the rules
        ArrayList<String> rulesList = new ArrayList<>();
        // Add each rule to the ArrayList
        for (String rule : rulesArray) {
            rule = rule.trim(); // Remove any leading/trailing spaces
            if (!rule.isEmpty()) {
                rulesList.add(rule);
            }
        }
        // Create an adapter for the rules
        RulesAdapter rulesAdapter = new RulesAdapter(rulesList);
        recyclerViewRules.setAdapter(rulesAdapter);
        recyclerViewRules.setLayoutManager(new LinearLayoutManager(this));
    }

    private void renderLocationSection() {
        String[] areaArray = info.getAddress().split(",");
        rentRoomAddressValue.setText(areaArray[0]);
        rentRoomAreaValue.setText(areaArray[1]);

    }

    private void renderAmenitiesSection() {
        String[] amenitiesArray = info.getAmenities().split("[\n.]");
        // Create an ArrayList to store the amenities
        ArrayList<String> amenitiesList = new ArrayList<>();
        // Add each amenity to the ArrayList
        for (String amenity : amenitiesArray) {
            amenity = amenity.trim(); // Remove any leading/trailing spaces
            if (!amenity.isEmpty()) {
                amenitiesList.add(amenity);
            }
        }
        // Create an adapter for the amenities
        RulesAdapter amenitiesAdapter = new RulesAdapter(amenitiesList);
        recyclerViewAmenities.setAdapter(amenitiesAdapter);
        recyclerViewAmenities.setLayoutManager(new LinearLayoutManager(this));
    }

    private void renderHostSection() {
        rentRoomHostNameValue.setText(info.getHostName());
    }

    private void renderReviewSection() {}

    private void createSlider() {
        // get images from database
        ImageSlider imageSlider = findViewById(R.id.cardImageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.login_image, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.register_image, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.start_image, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    private void initView() {
        Log.d(TAG, "initView: started");

        // init text views
        rentRoomPersonsValue = findViewById(R.id.rentRoomPersonsValue);
        rentRoomBedsValue = findViewById(R.id.rentRoomBedsValue);
        rentRoomBathroomsValue = findViewById(R.id.rentRoomBathroomsValue);
        rentRoomBedroomsValue = findViewById(R.id.rentRoomBedroomsValue);
        rentRoomPriceValue = findViewById(R.id.rentRoomPriceValue);
        rentRoomExtraPriceValue = findViewById(R.id.rentRoomExtraPriceValue);
        rentRoomFinalPriceValue = findViewById(R.id.rentRoomFinalPriceValue);
        rentRoomDescriptionValue = findViewById(R.id.rentRoomDescriptionValue);
        rentRoomAddressValue = findViewById(R.id.rentRoomAddressValue);
        rentRoomAreaValue = findViewById(R.id.rentRoomAreaValue);
        rentRoomHostNameValue = findViewById(R.id.rentRoomHostNameValue);

        // init recycler views
        recyclerViewRules = findViewById(R.id.recyclerViewRules);
        recyclerViewAmenities = findViewById(R.id.recyclerViewAmenities);

        // init map view
        rentRoomMapView = findViewById(R.id.rentRoomMapView);

        // init buttons
        seeHostButton = findViewById(R.id.seeHostButton);
        contactHostButton = findViewById(R.id.contactHostButton);
        makeReservationButton = findViewById(R.id.makeReservationButton);
        writeReviewButton = findViewById(R.id.writeReviewButton);

        // init bottom bar buttons
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
                // TODO: NEED TO CHANGE AND NOT GO TO HOST_REVIEW_PAGE. ONLY FOR TESTING PURPOSE!!!
                Intent profile_intent = new Intent(getApplicationContext(), HostReviewPageActivity.class);
                startActivity(profile_intent);
            }
        });
    }
}
