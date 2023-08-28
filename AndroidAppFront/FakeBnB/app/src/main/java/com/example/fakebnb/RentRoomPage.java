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
import com.example.fakebnb.adapter.RulesAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.RentRoomModel;
import com.example.fakebnb.model.request.BookingRequest;
import com.example.fakebnb.model.response.ApartmentResponse;
import com.example.fakebnb.model.response.BookingResponse;
import com.example.fakebnb.rest.ApartmentAPI;
import com.example.fakebnb.rest.BookingAPI;
import com.example.fakebnb.rest.RestClient;
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

import retrofit2.Callback;


public class RentRoomPage extends AppCompatActivity {

    private static final String TAG = "RentRoomPage";

    // user intent data
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private Long rentalId;
    private ApartmentResponse.ApartmentData apartmentData;

    private TextView rentRoomPersonsValue, rentRoomBedsValue, rentRoomBathroomsValue,
            rentRoomBedroomsValue, rentRoomPriceValue, rentRoomExtraPriceValue, rentRoomFinalPriceValue,
            rentRoomDescriptionValue, rentRoomAddressValue, rentRoomAreaValue, rentRoomHostNameValue;

    private RecyclerView recyclerViewRules, recyclerViewAmenities;

    private MapView rentRoomMapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean isMapReady = false;

    private Button seeHostButton, contactHostButton, makeReservationButton, writeReviewButton;
    private Button chatButton, profileButton, roleButton;
    private RentRoomModel info;

    private boolean userStayedAtRental = false;
    private TextView rentRoomReviewTitle;

    private ArrayList<SlideModel> slideModels;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_room);

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getSerializableExtra("user_id", Long.class);
            jwtToken = intent.getSerializableExtra("user_jwt", String.class);
            rentalId = intent.getSerializableExtra("rental_id", Long.class);
            ArrayList<String> roleList = intent.getStringArrayListExtra("user_roles");
            if (roleList != null) {
                roles = new HashSet<>();
                for (String role : roleList) {
                    roles.add(RoleName.valueOf(role));
                }
            }
        }

        /**
         * Do all the API calls here:
         * 1. Get the rental info - Failure is NOT_OK -> go back to main page
         * 2. Get the rental images - Failure is OK
         * 3. Get the host image - Failure is OK
         */


        /**
         * Get RENTAL-INFO. In case of success, do the rest of the work:
         * a. initialize the map and the rest logic
         * b. render data
         * c. get the rental images - failure is OK
         * d. get the host image - failure is OK
         */

        RestClient restClient = new RestClient(jwtToken);
        ApartmentAPI apartmentAPI = restClient.getClient().create(ApartmentAPI.class);

        fetchApartmentInfo(rentalId, apartmentAPI, savedInstanceState);

        info = new RentRoomModel(4, 2, 2, 100, 15,"Entire House",
                "This is a descriptionwerg wgwe werg wergwer gwergwer gwerg werg wwerg werfgwefg wdfgsdfgwertg wergswdfg sdfg sdfg we ",
                "These are the rules. These are the rules1. These are the rules2. These are the rules3. These are the rules4. These are the rules5.",
                "Sperchiou 70, Peristeri 121 37", "These are the amenities. These are the amenities1. These are the amenities2. These are the amenities3. These are the amenities4.",
                "photo_path.png", "Sakis Karpas", true);


        Toast.makeText(this, TAG + " ID: " + getIntent().getIntExtra("rental_id", 0) + 1, Toast.LENGTH_SHORT).show();
    }

    /**
     * API calls methods
     */

    private void fetchApartmentInfo(long rentalId, ApartmentAPI apartmentAPI, @Nullable Bundle savedInstanceState) {
        apartmentAPI.getApartmentInfo(rentalId)
                .enqueue(new Callback<ApartmentResponse>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<ApartmentResponse> call, @NonNull retrofit2.Response<ApartmentResponse> response) {
                        handleResponse(response, apartmentAPI, savedInstanceState);
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<ApartmentResponse> call, @NonNull Throwable t) {
                        showToast("Failed to connect to server");
                        finish();
                    }
                });
    }

    private void handleResponse(retrofit2.Response<ApartmentResponse> response, ApartmentAPI apartmentAPI, @Nullable Bundle savedInstanceState) {
        if (response.isSuccessful()) {
            if (response.body() != null) {
                apartmentData = response.body().getObject();

                checkGoogleAPIAvailability();

                // Check for location permissions and request if not granted
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    // Proceed with initializing the MapView and displaying the map
                    initView();
                    bottomBarClickListeners();

                    rentRoomMapView.onCreate(savedInstanceState);
                    rentRoomMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            isMapReady = true; // Mark the map as ready
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            // Check if an address is available and show it on the map
                            if (info != null) {
                                showAddressOnMap(googleMap, info.getAddress());
                            }
                        }
                    });

                    renderFetchedData();
                    buttonClickListener();
                    createSlider();
                    fetchApartmentImages(rentalId, apartmentAPI);
                    fetchHostImage(apartmentData.getId(), apartmentAPI);
                }
            } else {
                showToast("Could not get rental info");
                finish();
            }
        } else {
            showToast("Could not get rental info");
            finish();
        }
    }

    private void fetchApartmentImages(long rentalId, ApartmentAPI apartmentAPI) {
        /*
        apartmentAPI.callOfImages()...
        if (success) {
            slideModels.addAllImages();
        }
        else {
            showToast("Could not get rental images");
            continue;
        }
         */
    }

    private void fetchHostImage(long hostId, ApartmentAPI apartmentAPI) {
        /*
        // get host id of the apartment
        apartmentAPI.callOfHostImage()...
        if (success) {
            slideModels.addHostImage();
        }
        else {
            showToast("Could not get host image");
            continue;
        }
         */
    }

    private void showToast(String message) {
        Toast.makeText(RentRoomPage.this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Google Maps methods
     */

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

    /**
     * Data manipulation methods
     */
    private void renderFetchedData() {
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

    private void renderReviewSection() {
        Log.d(TAG, "renderReviewSection: started");
        userStayedAtRental = info.getUserStayedHere();
        if (userStayedAtRental) {
            rentRoomReviewTitle.setVisibility(View.VISIBLE);
            writeReviewButton.setVisibility(View.VISIBLE);
        } else {
            rentRoomReviewTitle.setVisibility(View.GONE);
            writeReviewButton.setVisibility(View.GONE);
        }
        Log.d(TAG, "renderReviewSection: finished");
    }

    private void createSlider() {
        // get images from database
        ImageSlider imageSlider = findViewById(R.id.cardImageSlider);
        slideModels = new ArrayList<>();

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
        rentRoomReviewTitle = findViewById(R.id.rentRoomReviewTitle);

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
        // only user_role can be here

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
                Intent chat_intent = new Intent(RentRoomPage.this, ChatActivity.class);
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
                Intent profile_intent = new Intent(RentRoomPage.this, ProfileActivity.class);
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
                    Intent host_main_page_intent = new Intent(RentRoomPage.this, HostMainPageActivity.class);
                    host_main_page_intent.putExtra("user_id", userId);
                    host_main_page_intent.putExtra("user_jwt", jwtToken);
                    ArrayList<String> roleList = new ArrayList<>();
                    for (RoleName role : roles) {
                        roleList.add(role.toString());
                    }
                    host_main_page_intent.putExtra("user_roles", roleList);
                    startActivity(host_main_page_intent);
                } else {
                    Toast.makeText(RentRoomPage.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void buttonClickListener() {
        seeHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: get the host id from the API
                long hostId = 1L;

                Toast.makeText(view.getContext(), "Pressed SEE HOST BUTTON", Toast.LENGTH_SHORT).show();
                Intent see_host_intent = new Intent(getApplicationContext(), HostReviewPageActivity.class);
                see_host_intent.putExtra("user_id", userId);
                see_host_intent.putExtra("user_jwt", jwtToken);
                ArrayList<String> roleList = new ArrayList<>();
                for (RoleName role : roles) {
                    roleList.add(role.toString());
                }
                see_host_intent.putExtra("user_roles", roleList);
                see_host_intent.putExtra("host_id", hostId);
                startActivity(see_host_intent);
            }
        });

        contactHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: get the host id from the API
                long hostId = 2L;

                Toast.makeText(view.getContext(), "Pressed CONTACT HOST BUTTON", Toast.LENGTH_SHORT).show();
                Intent contact_host_intent = new Intent(getApplicationContext(), IndividualChatActivity.class);
                contact_host_intent.putExtra("user_id", userId);
                contact_host_intent.putExtra("user_jwt", jwtToken);
                ArrayList<String> roleList = new ArrayList<>();
                for (RoleName role : roles) {
                    roleList.add(role.toString());
                }
                contact_host_intent.putExtra("user_roles", roleList);
                contact_host_intent.putExtra("other_user_id", hostId);
                startActivity(contact_host_intent);
            }
        });

        makeReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Pressed MAKE RESERVATION BUTTON", Toast.LENGTH_SHORT).show();

                // TODO: get the reservation dates, now use dummy dates
                RestClient restClient = new RestClient(jwtToken);
                BookingAPI bookingAPI = restClient.getClient().create(BookingAPI.class);
                BookingRequest bookingRequest = setBookingRequest();

                bookingAPI.createBooking(bookingRequest)
                        .enqueue(new Callback<BookingResponse>() {
                            @Override
                            public void onResponse(@NonNull retrofit2.Call<BookingResponse> call, @NonNull retrofit2.Response<BookingResponse> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().getSuccess()) {
                                            Toast.makeText(RentRoomPage.this, "Reservation successful", Toast.LENGTH_SHORT).show();
                                            Intent make_reservation_intent = new Intent(getApplicationContext(), ReservationDoneActivity.class);
                                            make_reservation_intent.putExtra("user_id", userId);
                                            make_reservation_intent.putExtra("user_jwt", jwtToken);
                                            ArrayList<String> roleList = new ArrayList<>();
                                            for (RoleName role : roles) {
                                                roleList.add(role.toString());
                                            }
                                            make_reservation_intent.putExtra("user_roles", roleList);
                                            make_reservation_intent.putExtra("apartment_id", apartmentData.getId());
                                            startActivity(make_reservation_intent);
                                        } else {
                                            Toast.makeText(RentRoomPage.this, "1 Reservation failed", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(RentRoomPage.this, "2 Reservation failed", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(RentRoomPage.this, "3 Reservation failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull retrofit2.Call<BookingResponse> call, @NonNull Throwable t) {
                                Toast.makeText(RentRoomPage.this, "4 Reservation failed", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: go to write review page
                Toast.makeText(view.getContext(), "Pressed WRITE REVIEW BUTTON", Toast.LENGTH_SHORT).show();
                Intent write_review_intent = new Intent(getApplicationContext(), WriteReviewActivity.class);
                startActivity(write_review_intent);
            }
        });
    }

    private BookingRequest setBookingRequest() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setApartmentId(apartmentData.getId());
        bookingRequest.setCheckInDate("2021-05-01");
        bookingRequest.setCheckOutDate("2021-05-05");
        return bookingRequest;
    }
}
