package com.example.fakebnb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.Callbacks.ApartmentImageLoadCallback;
import com.example.fakebnb.adapter.RulesAdapter;
import com.example.fakebnb.adapter.SliderAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.request.BookingRequest;
import com.example.fakebnb.model.request.ChatSenderReceiverRequest;
import com.example.fakebnb.model.response.AbleToReviewResponse;
import com.example.fakebnb.model.response.ApartmentImageIdsResponse;
import com.example.fakebnb.model.response.ApartmentResponse;
import com.example.fakebnb.model.response.BookingResponse;
import com.example.fakebnb.model.response.ChatIdResponse;
import com.example.fakebnb.model.response.UserRegResponse;
import com.example.fakebnb.rest.ApartmentAPI;
import com.example.fakebnb.rest.BookingAPI;
import com.example.fakebnb.rest.BookingReviewAPI;
import com.example.fakebnb.rest.ChatAPI;
import com.example.fakebnb.rest.ImageAPI;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.utils.NavigationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RentRoomPage extends AppCompatActivity {

    private static final String TAG = "RentRoomPage";

    // user intent data
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private Long apartmentId, hostId, chatId;
    private ApartmentResponse.ApartmentData apartmentData = null;
    private UserRegResponse.UserRegData host = null;

    private TextView rentRoomPersonsValue, rentRoomBedsValue, rentRoomBathroomsValue,
            rentRoomBedroomsValue, rentRoomPriceValue, rentRoomExtraPriceValue, rentRoomFinalPriceValue,
            rentRoomDescriptionValue, rentRoomAddressValue, rentRoomDistrictValue,
            rentRoomCityValue, rentRoomCountryValue, rentRoomHostNameValue;

    private ImageView hostImage;

    private RecyclerView recyclerViewRules, recyclerViewAmenities;

    private MapView rentRoomMapView = null;
    private GoogleMap googleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean isMapReady = false;
    private String finalAddress = null;

    private Button seeHostButton, contactHostButton, makeReservationButton, writeReviewButton;
    private Button chatButton, profileButton, roleButton;

    private TextView rentRoomReviewTitle;

//    private ArrayList<SlideModel> slideModels;

    // search dates
    private String checkInDate, checkOutDate;
    private Integer numOfGuests;


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
            checkInDate = intent.getSerializableExtra("check_in_date", String.class);
            checkOutDate = intent.getSerializableExtra("check_out_date", String.class);
            numOfGuests = intent.getSerializableExtra("num_of_guests", Integer.class);
        }

        // Proceed with initializing the MapView and displaying the map
        initView();
        bottomBarClickListeners();
        buttonClickListener();

        if (checkInDate == null || checkInDate.isEmpty() || checkOutDate == null || checkOutDate.isEmpty() || numOfGuests == null) {
            makeReservationButton.setVisibility(View.GONE);
        }

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
                                NavigationUtils.goToMainPage(RentRoomPage.this, userId, jwtToken, roles);
                            }
                        } else {
                            Toast.makeText(RentRoomPage.this, "Could not get host of apartment", Toast.LENGTH_SHORT).show();
                            NavigationUtils.goToMainPage(RentRoomPage.this, userId, jwtToken, roles);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserRegResponse> call, @NonNull Throwable t) {
                        Toast.makeText(RentRoomPage.this, "Failed to connect to server and get host of apartment", Toast.LENGTH_SHORT).show();
                        NavigationUtils.goToMainPage(RentRoomPage.this, userId, jwtToken, roles);
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
                                    rentRoomMapView.getMapAsync(map -> {
                                        googleMap = map;
                                        isMapReady = true; // Mark the map as ready
                                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                                        // Check if an address is available and show it on the map
                                        if (apartmentData != null) {
                                            showAddressOnMap(finalAddress);
                                        }
                                    });
                                }
                            } else {
                                showToast("Could not get rental info");
                                NavigationUtils.goToMainPage(RentRoomPage.this, userId, jwtToken, roles);
                            }
                        } else {
                            showToast("Could not get rental info");
                            NavigationUtils.goToMainPage(RentRoomPage.this, userId, jwtToken, roles);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<ApartmentResponse> call, @NonNull Throwable t) {
                        showToast("Failed to connect to server");
                        NavigationUtils.goToMainPage(RentRoomPage.this, userId, jwtToken, roles);
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
                rentRoomMapView.getMapAsync(map -> {
                    googleMap = map;
                    isMapReady = true;
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    if (apartmentData != null) {
                        showAddressOnMap(finalAddress);
                    }
                });
            } else {
                Toast.makeText(this, "Not rendering map", Toast.LENGTH_SHORT).show();
            }
        }
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
        if (numOfGuests != null) {
            rentRoomFinalPriceValue.setText(
                    String.valueOf(
                            apartmentData.getExtraCostPerPerson().
                                    multiply(BigDecimal.valueOf(numOfGuests)).
                                    add(apartmentData.getMinRetailPrice())));
        } else {
            rentRoomFinalPriceValue.setText("-");
        }
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

        if (rulesList.contains(apartmentData.getRules())) {
            rulesList.remove(apartmentData.getRules());
            rulesList = parseInput(apartmentData.getRules());
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

        if (amenitiesList.size() == 1 && amenitiesList.contains(apartmentData.getAmenities())) {
            amenitiesList.remove(apartmentData.getAmenities());
            amenitiesList = parseInput(apartmentData.getAmenities());
        }

        // Create an adapter for the amenities
        RulesAdapter amenitiesAdapter = new RulesAdapter(amenitiesList);
        recyclerViewAmenities.setAdapter(amenitiesAdapter);
        recyclerViewAmenities.setLayoutManager(new LinearLayoutManager(this));
    }

    public static ArrayList<String> parseInput(String input) {
        ArrayList<String> resultList = new ArrayList<>();

        // Remove curly braces and split by comma
        String[] parts = input.replaceAll("[{}]", "").split(",");

        for (String part : parts) {
            // Remove leading and trailing whitespace
            String trimmedPart = part.trim();

            // If the part is enclosed in double quotes, remove the quotes
            if (trimmedPart.startsWith("\"") && trimmedPart.endsWith("\"")) {
                trimmedPart = trimmedPart.substring(1, trimmedPart.length() - 1);
            }

            resultList.add(trimmedPart);
        }

        return resultList;
    }

    private void renderHostSection() {
        rentRoomHostNameValue.setText(host.getUsername());

        RestClient restClient = new RestClient(jwtToken);
        ImageAPI imageAPI = restClient.getClient().create(ImageAPI.class);

        imageAPI.getImage(hostId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Bitmap userImageBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            userImageBitmap = getCircularBitmap(userImageBitmap);
                            if (userImageBitmap != null) {
                                hostImage.setImageBitmap(userImageBitmap);
                                hostImage.setPadding(0, 0, 0, 0);
                            }
                        } else {
                            Toast.makeText(RentRoomPage.this, "1 Couldn't get host image", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "1 Couldn't get host image");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(RentRoomPage.this, "2 Couldn't get host image:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "2 Couldn't get host image: " + t.getMessage());
                    }
                });
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

    private void renderReviewSection() {
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

        SliderAdapter imageSliderAdapter = new SliderAdapter();
        SliderView sliderView = findViewById(R.id.cardImageSlider);
        sliderView.setSliderAdapter(imageSliderAdapter);
        sliderView.setIndicatorEnabled(true);
        sliderView.setIndicatorVisibility(true);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        RestClient restClient = new RestClient(jwtToken);
        ImageAPI imageAPI = restClient.getClient().create(ImageAPI.class);

        imageAPI.getApartmentImageIds(apartmentId)
                .enqueue(new Callback<ApartmentImageIdsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApartmentImageIdsResponse> call, @NonNull Response<ApartmentImageIdsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (Long imageId : response.body().getObject()) {
                                getApartmentImage(imageId, new ApartmentImageLoadCallback() {
                                    @Override
                                    public void onImageLoaded(Bitmap apartmentImageBitmap) {
                                        imageSliderAdapter.addItem(apartmentImageBitmap);
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Toast.makeText(RentRoomPage.this, "Error while downloading image: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(RentRoomPage.this, "1 Couldn't get apartment images", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "1 Couldn't get apartment images");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApartmentImageIdsResponse> call, @NonNull Throwable t) {
                        Toast.makeText(RentRoomPage.this, "2 Couldn't get apartment images:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "2 Couldn't get apartment images: " + t.getMessage());
                    }
                });
    }

    private void getApartmentImage(Long imageId, ApartmentImageLoadCallback callback) {
        RestClient restClient = new RestClient(jwtToken);
        ImageAPI imageAPI = restClient.getClient().create(ImageAPI.class);

        imageAPI.getApartmentImageByImageId(imageId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Bitmap userImageBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            if (userImageBitmap != null) {
                                callback.onImageLoaded(userImageBitmap);
                            } else {
                                callback.onError("Couldn't process user image");
                            }
                        } else {
                            callback.onError("Couldn't get user image");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        callback.onError("Couldn't get user image: " + t.getMessage());
                    }
                });
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
        hostImage = findViewById(R.id.rent_room_host_image_view);

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

        chatButton.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
            NavigationUtils.goToChatPage(RentRoomPage.this, userId, jwtToken, roles, RoleName.ROLE_USER.toString());
        });

        profileButton.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Pressed PROFILE BUTTON", Toast.LENGTH_SHORT).show();
            NavigationUtils.goToProfilePage(RentRoomPage.this, userId, jwtToken, roles, RoleName.ROLE_USER.toString());
        });

        roleButton.setOnClickListener(view -> {
            Log.d(TAG, "onClick: pressed role button");
            Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();

            if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                NavigationUtils.goToHostMainPage(RentRoomPage.this, userId, jwtToken, roles);
            } else {
                Toast.makeText(RentRoomPage.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buttonClickListener() {
        seeHostButton.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Pressed SEE HOST BUTTON", Toast.LENGTH_SHORT).show();
            NavigationUtils.goToHostReviewPage(RentRoomPage.this, userId, jwtToken, roles, hostId, apartmentId);
        });

        contactHostButton.setOnClickListener(view -> {
            RestClient restClient = new RestClient(jwtToken);
            ChatAPI chatAPI = restClient.getClient().create(ChatAPI.class);
            ChatSenderReceiverRequest chatSenderReceiverRequest = setChatSenderReceiverRequest();

            chatAPI.getChatIdBySenderReceiver(chatSenderReceiverRequest)
                    .enqueue(new Callback<ChatIdResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<ChatIdResponse> call, @NonNull Response<ChatIdResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getSuccess()) {
                                    chatId = response.body().getObject();
                                    Toast.makeText(view.getContext(), "Pressed CONTACT HOST BUTTON", Toast.LENGTH_SHORT).show();
                                    NavigationUtils.goToIndividualChatPage(RentRoomPage.this, userId, jwtToken, roles, chatId, RoleName.ROLE_USER);
                                } else {
                                    Toast.makeText(RentRoomPage.this, "1 Couldn't get chat with host", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RentRoomPage.this, "2 Couldn't get chat with host", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ChatIdResponse> call, @NonNull Throwable t) {
                            Log.d(TAG, "Failed to connect to server and get chat with host" + apartmentId);
                            Toast.makeText(RentRoomPage.this, "Failed to connect to server and get chat with host", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        makeReservationButton.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Pressed MAKE RESERVATION BUTTON", Toast.LENGTH_SHORT).show();

            RestClient restClient = new RestClient(jwtToken);
            BookingAPI bookingAPI = restClient.getClient().create(BookingAPI.class);
            BookingRequest bookingRequest = setBookingRequest();

            bookingAPI.createBooking(bookingRequest)
                    .enqueue(new Callback<BookingResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().getSuccess()) {
                                        Toast.makeText(RentRoomPage.this, "Reservation successful", Toast.LENGTH_SHORT).show();
                                        NavigationUtils.goToReservationDonePage(RentRoomPage.this, userId, jwtToken, roles);
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
                        public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                            Toast.makeText(RentRoomPage.this, "4 Reservation failed", Toast.LENGTH_SHORT).show();
                        }
                    });


        });

        writeReviewButton.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Pressed WRITE REVIEW BUTTON", Toast.LENGTH_SHORT).show();
            NavigationUtils.goToWriteReviewPage(RentRoomPage.this, userId, jwtToken, roles, apartmentId, hostId);
        });
    }

    private BookingRequest setBookingRequest() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setApartmentId(apartmentId);
        bookingRequest.setCheckInDate(checkInDate);
        bookingRequest.setCheckOutDate(checkOutDate);
        return bookingRequest;
    }

    private ChatSenderReceiverRequest setChatSenderReceiverRequest() {
        ChatSenderReceiverRequest chatSenderReceiverRequest = new ChatSenderReceiverRequest();
        chatSenderReceiverRequest.setSenderId(userId);
        chatSenderReceiverRequest.setReceiverId(hostId);
        return chatSenderReceiverRequest;
    }
}
