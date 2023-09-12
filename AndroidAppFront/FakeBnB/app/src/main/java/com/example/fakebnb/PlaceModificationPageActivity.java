package com.example.fakebnb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.Callbacks.ApartmentImageLoadCallback;
import com.example.fakebnb.adapter.ImageDeleteAdapter;
import com.example.fakebnb.enums.RentalType;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.request.ApartmentRequest;
import com.example.fakebnb.model.response.ApartmentImageIdsResponse;
import com.example.fakebnb.model.response.ApartmentResponse;
import com.example.fakebnb.rest.ApartmentAPI;
import com.example.fakebnb.rest.ImageAPI;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.utils.ImageUtils;
import com.example.fakebnb.utils.NavigationUtils;
import com.example.fakebnb.utils.RealPathUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceModificationPageActivity extends AppCompatActivity {

    private static final String TAG = "PlaceModificationPage";

    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private Long rentalId;
    private ApartmentResponse.ApartmentData apartmentData;


    // warning TextView messages
    private TextView modifyPlaceWarningAddress, modifyPlaceWarningDates,
            modifyPlaceWarningMaxVisitors, modifyPlaceWarningMinPrice, modifyPlaceWarningExtraCost,
            modifyPlaceWarningRentalType, modifyPlaceWarningPhotoUpload, modifyPlaceWarningRules,
            modifyPlaceWarningDescription, modifyPlaceWarningBeds, modifyPlaceWarningBedrooms,
            modifyPlaceWarningBathrooms, modifyPlaceWarningLivingRooms, modifyPlaceWarningArea,
            modifyPlaceWarningDistrict, modifyPlaceWarningCity, modifyPlaceWarningCountry,
            modifyPlaceWarningAmenities;

    // EditTexts fields
    private EditText modifyPlaceAddress, modifyPlaceStartDate, modifyPlaceEndDate,
            modifyPlaceMaxVisitors, modifyPlaceMinPrice, modifyPlaceExtraCost,
            modifyPlacePhotoUpload, modifyPlaceRules,
            modifyPlaceDescription, modifyPlaceBeds, modifyPlaceBedrooms,
            modifyPlaceBathrooms, modifyPlaceLivingRooms, modifyPlaceArea,
            modifyPlaceDistrict, modifyPlaceCity, modifyPlaceCountry, modifyPlaceAmenities;

    private RadioGroup modifyPlaceRentalTypeRadioGroup;
    // MapView
    private MapView modifyPlaceMapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private boolean isMapReady = false;
    private String addressToShowOnMap;
    public GoogleMap googleMap;

    // page buttons
    private Button savePlaceChangesButton, deletePlaceButton;

    // bottom buttons
    private Button chatButton, profileButton, roleButton;

    /**
     * Variables for IMAGE UPLOAD
     */
    private Button selectImageButton;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String imagePath;
    private Bitmap imageBitmap;
    private List<Bitmap> imageBitmapList;
    private List<Bitmap> newImages = new ArrayList<>();
    private RecyclerView imagesRecyclerView;
    private ImageDeleteAdapter imageAdapter;

    private List<Long> imageIds = new ArrayList<>();

    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_MEDIA_IMAGES
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_modification);

        initView();

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

        bottomBarClickListeners();
        resetWarnVisibility();
        setTextWatchers();
        onDatesClicked();
        modificationButtonsClickListener();
        savePlaceChangesButton.setVisibility(View.GONE);

        /**
         * Variables for MULTIPLE IMAGES
         */
        imageBitmapList = new ArrayList<>(); // Initialize the image bitmap list
        imageAdapter = new ImageDeleteAdapter(imageBitmapList);
        imagesRecyclerView = findViewById(R.id.modifyImageRecyclerView);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(PlaceModificationPageActivity.this));
        imagesRecyclerView.setAdapter(imageAdapter);

        RestClient restClient = new RestClient(jwtToken);
        ImageAPI imageAPI = restClient.getClient().create(ImageAPI.class);

        imageAPI.getApartmentImageIds(rentalId)
                .enqueue(new Callback<ApartmentImageIdsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApartmentImageIdsResponse> call, @NonNull Response<ApartmentImageIdsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            imageIds.addAll(response.body().getObject());
                            for (Long imageId : response.body().getObject()) {
                                getApartmentImage(imageId, new ApartmentImageLoadCallback() {
                                    @Override
                                    public void onImageLoaded(Bitmap apartmentImageBitmap) {
                                        imageAdapter.addStoredItem(apartmentImageBitmap, imageId);
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Toast.makeText(PlaceModificationPageActivity.this, "Error while downloading image: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(PlaceModificationPageActivity.this, "1 Couldn't get apartment images", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "1 Couldn't get apartment images");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApartmentImageIdsResponse> call, @NonNull Throwable t) {
                        Toast.makeText(PlaceModificationPageActivity.this, "2 Couldn't get apartment images:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "2 Couldn't get apartment images: " + t.getMessage());
                    }
                });

        imageClickListener();
        setImagePickerLauncher();

        // THOSE 2 LINES ARE MUST BE BEFORE API CALL TO WORK GOOGLE MAPS AND NOT CRASH ONRESUME METHOD
        checkGoogleAPIAvailability();
        modifyPlaceMapView.onCreate(savedInstanceState);

//        RestClient restClient = new RestClient(jwtToken);
        ApartmentAPI apartmentAPI = restClient.getClient().create(ApartmentAPI.class);

        apartmentAPI.getApartmentInfo(rentalId)
                .enqueue(new Callback<ApartmentResponse>() {
                    @Override
                    public void onResponse(@NonNull retrofit2.Call<ApartmentResponse> call, @NonNull retrofit2.Response<ApartmentResponse> response) {
                        if (response.isSuccessful()) {
                            ApartmentResponse apartmentResponse = response.body();
                            if (apartmentResponse != null) {
                                apartmentData = apartmentResponse.getObject();

                                setDataToViews();
                                // Check for location permissions and request if not granted
                                if (ContextCompat.checkSelfPermission(PlaceModificationPageActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(PlaceModificationPageActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                } else {
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
                                }
                            } else {
                                // Handle unsuccessful response
                                Toast.makeText(PlaceModificationPageActivity.this, "Couldn't get info of apartment.", Toast.LENGTH_SHORT).show();
                                Log.d("API_CALL", "GetApartmentInfo failed");
                                goToHostMainPage();
                            }
                        } else {
                            // Handle unsuccessful response
                            Toast.makeText(PlaceModificationPageActivity.this, "Couldn't get info of apartment.", Toast.LENGTH_SHORT).show();
                            Log.d("API_CALL", "GetApartmentInfo failed");
                            goToHostMainPage();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<ApartmentResponse> call, @NonNull Throwable t) {

                        // Handle failure
                        Toast.makeText(PlaceModificationPageActivity.this, "Failed to communicate with server. Couldn't get info of apartment.", Toast.LENGTH_SHORT).show();
                        Log.e("API_CALL", "Error: " + t.getMessage());
                        goToHostMainPage();
                    }
                });

    }

    /**
     * Get images of apartment from server
     */
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

    /**
     * Same listener for single and multiple images(Recycler view)
     */
    private void imageClickListener() {
        Log.d(TAG, "imageClickListener: Started");

        selectImageButton.setOnClickListener(view -> {

            if (ActivityCompat.checkSelfPermission(PlaceModificationPageActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        PlaceModificationPageActivity.this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            } else {
                Toast.makeText(PlaceModificationPageActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }
        });
    }

    /**
     * Multiple images as RecyclerView
     */
    private void setImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imagePath = RealPathUtil.getRealPath(PlaceModificationPageActivity.this, imageUri);
                        imageBitmap = BitmapFactory.decodeFile(imagePath);

                        // Add the selected image to the layout
                        imageAdapter.addNewImage(imageBitmap);
//                        imageBitmapList.add(imageBitmap);
//                        newImages.add(imageBitmap);
//                        imageAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    private void goToHostMainPage() {
        Intent host_main_page_intent = new Intent(PlaceModificationPageActivity.this, HostMainPageActivity.class);
        host_main_page_intent.putExtra("user_id", userId);
        host_main_page_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        host_main_page_intent.putExtra("user_roles", roleList);
        startActivity(host_main_page_intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, initialize the map
                modifyPlaceMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap map) {
                        googleMap = map;
                        isMapReady = true;
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        if (addressToShowOnMap != null) {
                            showAddressOnMap(addressToShowOnMap);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Not rendering map", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(PlaceModificationPageActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Access to images is necessary", Toast.LENGTH_SHORT).show();
                Intent host_main_page_intent = new Intent(getApplicationContext(), HostMainPageActivity.class);
                host_main_page_intent.putExtra("user_id", userId);
                host_main_page_intent.putExtra("user_jwt", jwtToken);
                ArrayList<String> roleList = new ArrayList<>();
                for (RoleName role : roles) {
                    roleList.add(role.toString());
                }
                host_main_page_intent.putExtra("user_roles", roleList);
                startActivity(host_main_page_intent);
            }
        }
    }

    /**
     * GOOGLE MAP METHODS
     */

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


    /**
     * DATA MANIPULATION METHODS
     */

    private void setDataToViews() {
        modifyPlaceAddress.setText(apartmentData.getAddress());
        modifyPlaceDistrict.setText(apartmentData.getDistrict());
        modifyPlaceCity.setText(apartmentData.getCity());
        modifyPlaceCountry.setText(apartmentData.getCountry());
        modifyPlaceStartDate.setText(dateFormation(String.valueOf(apartmentData.getAvailableStartDate())));
        modifyPlaceEndDate.setText(dateFormation(String.valueOf(apartmentData.getAvailableEndDate())));
        modifyPlaceMaxVisitors.setText(String.valueOf(apartmentData.getMaxVisitors()));
        modifyPlaceMinPrice.setText(String.valueOf(apartmentData.getMinRetailPrice()));
        modifyPlaceExtraCost.setText(String.valueOf(apartmentData.getExtraCostPerPerson()));
        modifyPlaceRules.setText(apartmentData.getRules());
        modifyPlaceAmenities.setText(apartmentData.getAmenities());
        modifyPlaceDescription.setText(apartmentData.getDescription());
        modifyPlaceBeds.setText(String.valueOf(apartmentData.getNumberOfBeds()));
        modifyPlaceBedrooms.setText(String.valueOf(apartmentData.getNumberOfBedrooms()));
        modifyPlaceBathrooms.setText(String.valueOf(apartmentData.getNumberOfBathrooms()));
        modifyPlaceLivingRooms.setText(String.valueOf(apartmentData.getNumberOfLivingRooms()));
        modifyPlaceArea.setText(String.valueOf(apartmentData.getArea()));

        if (apartmentData.getRentalType() == RentalType.RENTAL_ROOM) {
            modifyPlaceRentalTypeRadioGroup.check(R.id.roomTypeRadioButton);
        } else {
            modifyPlaceRentalTypeRadioGroup.check(R.id.houseTypeRadioButton);
        }
    }

    private String dateFormation(String unformattedDate) {
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.US);
            Date inputDate = inputDateFormat.parse(unformattedDate);

            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("tr", "TR")); // Turkish locale
            outputDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
            return inputDate != null ? outputDateFormat.format(inputDate) : "Error in formatting date";
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private ApartmentRequest setUpdateApartmentValues() {
        ApartmentRequest apartmentRequest = new ApartmentRequest();

        apartmentRequest.setAddress(modifyPlaceAddress.getText().toString());
        apartmentRequest.setDistrict(modifyPlaceDistrict.getText().toString());
        apartmentRequest.setCity(modifyPlaceCity.getText().toString());
        apartmentRequest.setCountry(modifyPlaceCountry.getText().toString());
        apartmentRequest.setAvailableStartDate(modifyPlaceStartDate.getText().toString());
        apartmentRequest.setAvailableEndDate(modifyPlaceEndDate.getText().toString());
        apartmentRequest.setRules(modifyPlaceRules.getText().toString());
        apartmentRequest.setAmenities(modifyPlaceAmenities.getText().toString());
        apartmentRequest.setDescription(modifyPlaceDescription.getText().toString());
        apartmentRequest.setDeleteImageIds(imageAdapter.getDeletedImageIds());

        try {
            apartmentRequest.setMaxVisitors(Integer.parseInt(modifyPlaceMaxVisitors.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Max visitors must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            apartmentRequest.setMinRetailPrice(new BigDecimal(modifyPlaceMinPrice.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Minimum rental price must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            apartmentRequest.setExtraCostPerPerson(new BigDecimal(modifyPlaceExtraCost.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Extra cost per person must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            apartmentRequest.setNumberOfBeds(Short.parseShort(modifyPlaceBeds.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Number of beds must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            apartmentRequest.setNumberOfBedrooms(Short.parseShort(modifyPlaceBedrooms.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Number of bedrooms must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            apartmentRequest.setNumberOfBathrooms(Short.parseShort(modifyPlaceBathrooms.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Number of bathrooms must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            apartmentRequest.setNumberOfLivingRooms(Short.parseShort(modifyPlaceLivingRooms.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Number of living rooms must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            apartmentRequest.setArea(new BigDecimal(modifyPlaceArea.getText().toString()));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Area must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        LatLng location = getLocationFromAddress(modifyPlaceAddress.getText().toString());

        try {
            apartmentRequest.setGeoLat(new BigDecimal(location.latitude));
        } catch (NullPointerException e) {
            Toast.makeText(this, "Latitude of address is not valid", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            apartmentRequest.setGeoLong(new BigDecimal(location.longitude));
        } catch (NullPointerException e) {
            Toast.makeText(this, "Longitude of address is not valid", Toast.LENGTH_SHORT).show();
            return null;
        }

        apartmentRequest.setRentalType(modifyPlaceRentalTypeRadioGroup.getCheckedRadioButtonId() == R.id.roomTypeRadioButton ? RentalType.RENTAL_ROOM : RentalType.RENTAL_HOUSE);

        return apartmentRequest;
    }

    /**
     * TEXT WATCHERS
     */
    private void setTextWatchers() {
        Log.d(TAG, "setTextWatchers: started");

        setTextWatcherAddress();
        setTextWatcherDistrict();
        setTextWatcherCity();
        setTextWatcherCountry();
        setTextWatcherStartDate();
        setTextWatcherEndDate();
        setTextWatcherMaxVisitors();
        setTextWatcherMinPrice();
        setTextWatcherExtraCost();
//        setTextWatcherPhotoUpload();
        setTextWatcherRules();
        setTextWatcherAmenities();
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
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                    addressToShowOnMap = concatAddressToShowOnMap();
                    if (isMapReady && googleMap != null) {
                        showAddressOnMap(addressToShowOnMap);
                    }
                }
            }
        };
        modifyPlaceAddress.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherDistrict() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!modifyPlaceDistrict.getText().toString().isEmpty()) {
                    modifyPlaceWarningDistrict.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceDistrict.getText().toString().isEmpty()) {
                    modifyPlaceWarningDistrict.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningDistrict.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                    addressToShowOnMap = concatAddressToShowOnMap();
                    if (isMapReady && googleMap != null) {
                        showAddressOnMap(addressToShowOnMap);
                    }
                }
            }
        };
        modifyPlaceDistrict.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherCity() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!modifyPlaceCity.getText().toString().isEmpty()) {
                    modifyPlaceWarningCity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceCity.getText().toString().isEmpty()) {
                    modifyPlaceWarningCity.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningCity.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                    addressToShowOnMap = concatAddressToShowOnMap();
                    if (isMapReady && googleMap != null) {
                        showAddressOnMap(addressToShowOnMap);
                    }
                }
            }
        };
        modifyPlaceCity.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherCountry() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!modifyPlaceCountry.getText().toString().isEmpty()) {
                    modifyPlaceWarningCountry.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceCountry.getText().toString().isEmpty()) {
                    modifyPlaceWarningCountry.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningCountry.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                    addressToShowOnMap = concatAddressToShowOnMap();
                    if (isMapReady && googleMap != null) {
                        showAddressOnMap(addressToShowOnMap);
                    }
                }
            }
        };
        modifyPlaceCountry.addTextChangedListener(textWatcher);
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

    private void setTextWatcherAmenities() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!modifyPlaceAmenities.getText().toString().isEmpty()) {
                    modifyPlaceWarningAmenities.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (modifyPlaceAmenities.getText().toString().isEmpty()) {
                    modifyPlaceWarningAmenities.setVisibility(View.VISIBLE);
                    savePlaceChangesButton.setVisibility(View.GONE);
                } else {
                    modifyPlaceWarningAmenities.setVisibility(View.GONE);
                    savePlaceChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        modifyPlaceAmenities.addTextChangedListener(textWatcher);
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

    private String concatAddressToShowOnMap() {
        String address = "";
        if (!modifyPlaceAddress.getText().toString().isEmpty()) {
            address = modifyPlaceAddress.getText().toString();
        }
        if (!modifyPlaceDistrict.getText().toString().isEmpty()) {
            if (!address.isEmpty()) {
                address += ", " + modifyPlaceDistrict.getText().toString();
            } else {
                address = modifyPlaceDistrict.getText().toString();
            }
        }
        if (!modifyPlaceCity.getText().toString().isEmpty()) {
            if (!address.isEmpty()) {
                address += ", " + modifyPlaceCity.getText().toString();
            } else {
                address = modifyPlaceCity.getText().toString();
            }
        }
        if (!modifyPlaceCountry.getText().toString().isEmpty()) {
            if (!address.isEmpty()) {
                address += ", " + modifyPlaceCountry.getText().toString();
            } else {
                address = modifyPlaceCountry.getText().toString();
            }
        }
        return address;
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
        modifyPlaceWarningDistrict = findViewById(R.id.modifyPlaceWarningDistrict);
        modifyPlaceWarningCity = findViewById(R.id.modifyPlaceWarningCity);
        modifyPlaceWarningCountry = findViewById(R.id.modifyPlaceWarningCountry);
        modifyPlaceWarningDates = findViewById(R.id.modifyPlaceWarningDates);
        modifyPlaceWarningMaxVisitors = findViewById(R.id.modifyPlaceWarningMaxVisitors);
        modifyPlaceWarningMinPrice = findViewById(R.id.modifyPlaceWarningMinPrice);
        modifyPlaceWarningExtraCost = findViewById(R.id.modifyPlaceWarningExtraCost);
        modifyPlaceWarningRentalType = findViewById(R.id.modifyPlaceWarningRentalType);
        modifyPlaceWarningPhotoUpload = findViewById(R.id.modifyPlaceWarningPhotoUpload);
        modifyPlaceWarningRules = findViewById(R.id.modifyPlaceWarningRules);
        modifyPlaceWarningAmenities = findViewById(R.id.modifyPlaceWarningAmenities);
        modifyPlaceWarningDescription = findViewById(R.id.modifyPlaceWarningDescription);
        modifyPlaceWarningBeds = findViewById(R.id.modifyPlaceWarningBeds);
        modifyPlaceWarningBedrooms = findViewById(R.id.modifyPlaceWarningBedrooms);
        modifyPlaceWarningBathrooms = findViewById(R.id.modifyPlaceWarningBathrooms);
        modifyPlaceWarningLivingRooms = findViewById(R.id.modifyPlaceWarningLivingRooms);
        modifyPlaceWarningArea = findViewById(R.id.modifyPlaceWarningArea);

        // initialize EditTexts fields
        modifyPlaceAddress = findViewById(R.id.modifyPlaceAddressEditText);
        modifyPlaceDistrict = findViewById(R.id.modifyPlaceDistrictEditText);
        modifyPlaceCity = findViewById(R.id.modifyPlaceCityEditText);
        modifyPlaceCountry = findViewById(R.id.modifyPlaceCountryEditText);
        modifyPlaceStartDate = findViewById(R.id.modifyStartDateEditText);
        modifyPlaceEndDate = findViewById(R.id.modifyEndDateEditText);
        modifyPlaceMaxVisitors = findViewById(R.id.modifyPlaceMaxVisitorsEditText);
        modifyPlaceMinPrice = findViewById(R.id.modifyPlaceMinPriceEditText);
        modifyPlaceExtraCost = findViewById(R.id.modifyPlaceExtraCostEditText);
        modifyPlaceRentalTypeRadioGroup = findViewById(R.id.modifyPlaceRentalTypeRadioGroup);

        /**
         * PHOTO ONLY
         */
        selectImageButton = findViewById(R.id.modifySelectImageButton);

        modifyPlaceRules = findViewById(R.id.modifyPlaceRulesEditText);
        modifyPlaceAmenities = findViewById(R.id.modifyPlaceAmenitiesEditText);
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

    private void onDatesClicked() {
        Log.d(TAG, "onDatesClicked: started");

        modifyPlaceStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        PlaceModificationPageActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                                modifyPlaceStartDate.setText(formattedDate);
                            }
                        },
                        year, month, day);
                // not allow older dates to be selected
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                // display date picker dialog.
                datePickerDialog.show();
            }
        });

        modifyPlaceEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (modifyPlaceStartDate.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please select check in date first", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Calendar c = Calendar.getInstance();

                // Get the selected check-in date from startDateEditText and parse it to Calendar.
                String checkInDateText = modifyPlaceStartDate.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date checkInDate = dateFormat.parse(checkInDateText);
                    c.setTime(Objects.requireNonNull(checkInDate));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        PlaceModificationPageActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                                modifyPlaceEndDate.setText(formattedDate);
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

    private void modificationButtonsClickListener() {
        Log.d(TAG, "modificationButtonsClickListener: started");


        savePlaceChangesButton.setOnClickListener(view -> {
            resetWarnVisibility();

            Gson gson = new Gson();
            ApartmentRequest apartmentRequest = setUpdateApartmentValues();
            if (apartmentRequest == null) {
                Toast.makeText(this, "Please fill correctly all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            RestClient restClient = new RestClient(jwtToken);
            ApartmentAPI apartmentAPI = restClient.getClient().create(ApartmentAPI.class);
            if (!imageAdapter.getNewImages().isEmpty()) {
                apartmentAPI.updateApartmentWithImage(rentalId, gson.toJson(apartmentRequest), ImageUtils.getImageParts(imageAdapter.getNewImages()))
                        .enqueue(new Callback<ApartmentResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<ApartmentResponse> call, @NonNull Response<ApartmentResponse> response) {
                                if (response.isSuccessful()) {
                                    // Handle successful response
                                    ApartmentResponse apartmentResponse = response.body();
                                    if (apartmentResponse != null) {
                                        Log.d("API_CALL", "UpdateApartment successful");
                                        Toast.makeText(PlaceModificationPageActivity.this, "Rental modified correctly", Toast.LENGTH_SHORT).show();
                                        NavigationUtils.goToHostMainPage(PlaceModificationPageActivity.this, userId, jwtToken, roles);
                                    } else {
                                        // Handle unsuccessful response
                                        Toast.makeText(PlaceModificationPageActivity.this, "1 Couldn't update apartment.", Toast.LENGTH_SHORT).show();
                                        Log.d("API_CALL", "UpdateApartment failed");
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Toast.makeText(PlaceModificationPageActivity.this, "2 Couldn't update apartment.", Toast.LENGTH_SHORT).show();
                                    Log.d("API_CALL", "UpdateApartment failed");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ApartmentResponse> call, @NonNull Throwable t) {
                                // Handle failure
                                Toast.makeText(PlaceModificationPageActivity.this,
                                        "Failed to communicate with server. Couldn't update apartment.",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("API_CALL", "Error: " + t.getMessage());
                            }
                        });
            } else {
                apartmentAPI.updateApartment(rentalId, gson.toJson(apartmentRequest))
                        .enqueue(new Callback<ApartmentResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<ApartmentResponse> call, @NonNull Response<ApartmentResponse> response) {
                                if (response.isSuccessful()) {
                                    // Handle successful response
                                    ApartmentResponse apartmentResponse = response.body();
                                    if (apartmentResponse != null) {
                                        Log.d("API_CALL", "UpdateApartment successful");
                                        Toast.makeText(PlaceModificationPageActivity.this, "Rental modified correctly", Toast.LENGTH_SHORT).show();
                                        NavigationUtils.goToHostMainPage(PlaceModificationPageActivity.this, userId, jwtToken, roles);
                                    } else {
                                        // Handle unsuccessful response
                                        Toast.makeText(PlaceModificationPageActivity.this, "1 Couldn't update apartment.", Toast.LENGTH_SHORT).show();
                                        Log.d("API_CALL", "UpdateApartment failed");
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Toast.makeText(PlaceModificationPageActivity.this, "2 Couldn't update apartment.", Toast.LENGTH_SHORT).show();
                                    Log.d("API_CALL", "UpdateApartment failed");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ApartmentResponse> call, @NonNull Throwable t) {
                                // Handle failure
                                Toast.makeText(PlaceModificationPageActivity.this,
                                        "Failed to communicate with server. Couldn't update apartment.",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("API_CALL", "Error: " + t.getMessage());
                            }
                        });
            }


        });

        deletePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete place from database
                resetWarnVisibility();
                RestClient restClient = new RestClient(jwtToken);
                ApartmentAPI apartmentAPI = restClient.getClient().create(ApartmentAPI.class);

                apartmentAPI.deleteApartment(rentalId)
                        .enqueue(new Callback<ApartmentResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<ApartmentResponse> call, @NonNull Response<ApartmentResponse> response) {
                                if (response.isSuccessful()) {
                                    // Handle successful response
                                    ApartmentResponse apartmentResponse = response.body();
                                    if (apartmentResponse != null) {
                                        Toast.makeText(PlaceModificationPageActivity.this, "Rental deleted correctly", Toast.LENGTH_SHORT).show();
                                        Log.d("API_CALL", "UpdateApartment successful");
                                        NavigationUtils.goToHostMainPage(PlaceModificationPageActivity.this, userId, jwtToken, roles);
                                    } else {
                                        // Handle unsuccessful response
                                        Toast.makeText(PlaceModificationPageActivity.this, "1 Couldn't delete rental.", Toast.LENGTH_SHORT).show();
                                        Log.d("API_CALL", "UpdateApartment failed");
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Toast.makeText(PlaceModificationPageActivity.this, "2 Couldn't delete rental.", Toast.LENGTH_SHORT).show();
                                    Log.d("API_CALL", "UpdateApartment failed");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ApartmentResponse> call, @NonNull Throwable t) {
                                // Handle failure
                                Toast.makeText(PlaceModificationPageActivity.this,
                                        "Failed to communicate with server. Couldn't delete rental.",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("API_CALL", "Error: " + t.getMessage());
                            }
                        });
            }
        });
    }

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");


        chatButton.setOnClickListener(view -> {
            resetWarnVisibility();
            Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
            Intent chat_intent = new Intent(PlaceModificationPageActivity.this, ChatActivity.class);
            chat_intent.putExtra("user_id", userId);
            chat_intent.putExtra("user_jwt", jwtToken);
            chat_intent.putExtra("user_current_role", RoleName.ROLE_HOST.toString());
            ArrayList<String> roleList = new ArrayList<>();
            for (RoleName role : roles) {
                roleList.add(role.toString());
            }
            chat_intent.putExtra("user_roles", roleList);
            startActivity(chat_intent);
        });

        profileButton.setOnClickListener(view -> {
            resetWarnVisibility();
            Toast.makeText(view.getContext(), "Pressed PROFILE BUTTON", Toast.LENGTH_SHORT).show();
            Intent profile_intent = new Intent(PlaceModificationPageActivity.this, ProfileActivity.class);
            profile_intent.putExtra("user_id", userId);
            profile_intent.putExtra("user_jwt", jwtToken);
            profile_intent.putExtra("user_current_role", RoleName.ROLE_HOST.toString());
            ArrayList<String> roleList = new ArrayList<>();
            for (RoleName role : roles) {
                roleList.add(role.toString());
            }
            profile_intent.putStringArrayListExtra("user_roles", roleList);
            startActivity(profile_intent);
        });

        roleButton.setOnClickListener(view -> {
            resetWarnVisibility();
            Log.d(TAG, "onClick: role button pressed");
            Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();

            if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                // to be at this activity he has the user role
                Intent main_page_intent = new Intent(PlaceModificationPageActivity.this, MainPageActivity.class);
                main_page_intent.putExtra("user_id", userId);
                main_page_intent.putExtra("user_jwt", jwtToken);
                ArrayList<String> roleList = new ArrayList<>();
                for (RoleName role : roles) {
                    roleList.add(role.toString());
                }
                main_page_intent.putExtra("user_roles", roleList);
                startActivity(main_page_intent);
            } else {
                Toast.makeText(PlaceModificationPageActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
