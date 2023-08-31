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
import com.example.fakebnb.model.request.BookingRequest;
import com.example.fakebnb.model.response.AbleToReviewResponse;
import com.example.fakebnb.model.response.ApartmentResponse;
import com.example.fakebnb.model.response.BookingResponse;
import com.example.fakebnb.model.response.UserRegResponse;
import com.example.fakebnb.rest.ApartmentAPI;
import com.example.fakebnb.rest.BookingAPI;
import com.example.fakebnb.rest.BookingReviewAPI;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RentRoomPage extends AppCompatActivity {

    private static final String TAG = "RentRoomPage";

    // user intent data
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private Long apartmentId, hostId;
    private ApartmentResponse.ApartmentData apartmentData = null;
    private UserRegResponse.UserRegData host = null;

    private TextView rentRoomPersonsValue, rentRoomBedsValue, rentRoomBathroomsValue,
            rentRoomBedroomsValue, rentRoomPriceValue, rentRoomExtraPriceValue, rentRoomFinalPriceValue,
            rentRoomDescriptionValue, rentRoomAddressValue, rentRoomDistrictValue,
            rentRoomCityValue, rentRoomCountryValue, rentRoomHostNameValue;

    private RecyclerView recyclerViewRules, recyclerViewAmenities;

    private MapView rentRoomMapView = null;
    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean isMapReady = false;
    private String finalAddress = null;

    private Button seeHostButton, contactHostButton, makeReservationButton, writeReviewButton;
    private Button chatButton, profileButton, roleButton;

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
            apartmentId = intent.getSerializableExtra("rental_id", Long.class);
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

        // Proceed with initializing the MapView and displaying the map
        initView();
        bottomBarClickListeners();
        buttonClickListener();

        // THOSE 2 LINES ARE MUST BE BEFORE API CALL TO WORK GOOGLE MAPS AND NOT CRASH ONRESUME METHOD
        checkGoogleAPIAvailability();
        rentRoomMapView.onCreate(savedInstanceState);

        RestClient restClient = new RestClient(jwtToken);
        ApartmentAPI apartmentAPI = restClient.getClient().create(ApartmentAPI.class);

        // GET HOST ID
        apartmentAPI.getHostId(apartmentId)
                .enqueue(new Callback<UserRegResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserRegResponse> call, @NonNull Response<UserRegResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                Log.d(TAG, "onResponse: HOST ID: " + response.body().getObject().getId());
                                host = response.body().getObject();
                                hostId = response.body().getObject().getId();
                                renderHostSection();
                            } else {
                                Toast.makeText(RentRoomPage.this, "Could not get host of apartment", Toast.LENGTH_SHORT).show();
                                goToMainPage();
                            }
                        } else {
                            Toast.makeText(RentRoomPage.this, "Could not get host of apartment", Toast.LENGTH_SHORT).show();
                            goToMainPage();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserRegResponse> call, @NonNull Throwable t) {
                        Toast.makeText(RentRoomPage.this, "Failed to connect to server and get host of apartment", Toast.LENGTH_SHORT).show();
                        goToMainPage();
                    }
                });

        // GET APARTMENT INFO
        apartmentAPI.getApartmentInfo(apartmentId)
                .enqueue(new Callback<ApartmentResponse>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<ApartmentResponse> call, @NonNull retrofit2.Response<ApartmentResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                apartmentData = response.body().getObject();

                                concatFinalAddress();
                                renderFetchedData();
                                createSlider();

                                Log.d(TAG, "handleResponse: INTO HANDLE");

//                                checkGoogleAPIAvailability();
//                                rentRoomMapView.onCreate(savedInstanceState);
                                // Check for location permissions and request if not granted
                                if (ContextCompat.checkSelfPermission(RentRoomPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(RentRoomPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                } else {
                                    rentRoomMapView.getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(@NonNull GoogleMap map) {
                                            googleMap = map;
                                            isMapReady = true; // Mark the map as ready
                                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                                            // Check if an address is available and show it on the map
                                            if (apartmentData != null) {
                                                showAddressOnMap("Sperchiou 70, Peristeri");
                                            }
                                        }
                                    });
                                }

                                fetchApartmentImages(apartmentId, apartmentAPI);
                                fetchHostImage(apartmentId, apartmentAPI);
                            } else {
                                showToast("Could not get rental info");
                                goToMainPage();
                            }
                        } else {
                            showToast("Could not get rental info");
                            goToMainPage();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<ApartmentResponse> call, @NonNull Throwable t) {
                        showToast("Failed to connect to server");
                        goToMainPage();
                    }
                });
    }

    private void concatFinalAddress() {
        finalAddress = apartmentData.getAddress() + ", " +
                apartmentData.getDistrict() + ", " +
                apartmentData.getCity() + ", " +
                apartmentData.getCountry();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, initialize the map
                rentRoomMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap map) {
                        googleMap = map;
                        isMapReady = true;
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        if (apartmentData != null) {
                            showAddressOnMap(apartmentData.getAddress() + ", " + apartmentData.getDistrict());
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Not rendering map", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchApartmentImages(long apartmentId, ApartmentAPI apartmentAPI) {
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

    private void goToMainPage() {
        Intent main_page_intent = new Intent(getApplicationContext(), MainPageActivity.class);
        main_page_intent.putExtra("user_id", userId);
        main_page_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        main_page_intent.putStringArrayListExtra("user_roles", roleList);
        startActivity(main_page_intent);
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
    protected void onPause() {
        super.onPause();
        rentRoomMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rentRoomMapView.onResume();
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
        renderReviewSection();
    }

    private void renderRoomInfoSection() {
        rentRoomPersonsValue.setText(String.valueOf(apartmentData.getMaxVisitors()));
        rentRoomBedsValue.setText(String.valueOf(apartmentData.getNumberOfBeds()));
        rentRoomBathroomsValue.setText(String.valueOf(apartmentData.getNumberOfBathrooms()));
        rentRoomBedroomsValue.setText(String.valueOf(apartmentData.getNumberOfBedrooms()));
    }

    private void renderPriceSection() {
        rentRoomPriceValue.setText(String.valueOf(apartmentData.getMinRetailPrice()));
        rentRoomExtraPriceValue.setText(String.valueOf(apartmentData.getExtraCostPerPerson()));
        // TODO: need to pass search parameters to calculate the final price
        rentRoomFinalPriceValue.setText(
                String.valueOf(
                        apartmentData.getExtraCostPerPerson().
                                multiply(BigDecimal.valueOf(3L)).
                                add(apartmentData.getMinRetailPrice())));
    }

    private void renderDescriptionSection() {
        rentRoomDescriptionValue.setText(apartmentData.getDescription());
    }

    private void renderRulesSection() {
        String[] rulesArray = apartmentData.getRules().split("[\n.]");
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
        rentRoomAddressValue.setText(apartmentData.getAddress());
        rentRoomDistrictValue.setText(apartmentData.getDistrict());
        rentRoomCityValue.setText(apartmentData.getCity());
        rentRoomCountryValue.setText(apartmentData.getCountry());
    }

    private void renderAmenitiesSection() {
        String[] amenitiesArray = apartmentData.getAmenities().split("[\n.]");
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
        rentRoomHostNameValue.setText(host.getUsername());
    }

    private void renderReviewSection() {
        // TODO: get the userStayedAtRental from the API
        Log.d(TAG, "renderReviewSection: started");

        RestClient restClient = new RestClient(jwtToken);
        BookingReviewAPI bookingReviewAPI = restClient.getClient().create(BookingReviewAPI.class);

        bookingReviewAPI.ableToReview(apartmentId)
                .enqueue(new Callback<AbleToReviewResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AbleToReviewResponse> call, @NonNull Response<AbleToReviewResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (Boolean.TRUE.equals(response.body().getObject())) {
                                Log.d(TAG, "1 User can review apartment:" + apartmentId);
                                rentRoomReviewTitle.setVisibility(View.VISIBLE);
                                writeReviewButton.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "2 User cannot review apartment:" + apartmentId);
                                rentRoomReviewTitle.setVisibility(View.GONE);
                                writeReviewButton.setVisibility(View.GONE);
                            }
                        } else {
                            Log.d(TAG, "3 User cannot review apartment:" + apartmentId);
                            rentRoomReviewTitle.setVisibility(View.GONE);
                            writeReviewButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AbleToReviewResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "Failed to connect to server and check if reviews are enabled" + apartmentId);
                        Toast.makeText(RentRoomPage.this, "4 Failed to connect to server and check if reviews are enabled", Toast.LENGTH_SHORT).show();
                        rentRoomReviewTitle.setVisibility(View.GONE);
                        writeReviewButton.setVisibility(View.GONE);
                    }
                });
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
        rentRoomDistrictValue = findViewById(R.id.rentRoomDistrictValue);
        rentRoomCityValue = findViewById(R.id.rentRoomCityValue);
        rentRoomCountryValue = findViewById(R.id.rentRoomCountryValue);
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
                                            make_reservation_intent.putExtra("apartment_id", apartmentId);
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
                write_review_intent.putExtra("user_id", userId);
                write_review_intent.putExtra("user_jwt", jwtToken);
                ArrayList<String> roleList = new ArrayList<>();
                for (RoleName role : roles) {
                    roleList.add(role.toString());
                }
                write_review_intent.putExtra("user_roles", roleList);
                write_review_intent.putExtra("rental_id", apartmentId);
                startActivity(write_review_intent);
            }
        });
    }

    private BookingRequest setBookingRequest() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setApartmentId(apartmentId);
        bookingRequest.setCheckInDate("2021-05-01");
        bookingRequest.setCheckOutDate("2021-05-05");
        return bookingRequest;
    }
}
