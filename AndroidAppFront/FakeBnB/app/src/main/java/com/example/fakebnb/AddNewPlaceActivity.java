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
import android.widget.RadioButton;
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

import com.example.fakebnb.Callbacks.AddApartmentCallback;
import com.example.fakebnb.adapter.ImageAdapter;
import com.example.fakebnb.enums.RentalType;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.request.ApartmentRequest;
import com.example.fakebnb.model.response.ApartmentResponse;
import com.example.fakebnb.rest.ApartmentAPI;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.utils.RealPathUtil;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private EditText addPlaceAddressEditText, startDateEditText, endDateEditText,
            addPlaceMaxVisitorsEditText, addPlaceMinPriceEditText, addPlaceExtraCostEditText,
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

    /**
     * Variables for IMAGE UPLOAD
     */
    private Button selectImageButton;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String imagePath;
    private Bitmap imageBitmap;
    private List<Bitmap> imageBitmapList;
    private RecyclerView imagesRecyclerView;
    private ImageAdapter imageAdapter;
    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_MEDIA_IMAGES
    };


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

        /**
         * Variables for MULTIPLE IMAGES
         */
        imageBitmapList = new ArrayList<>(); // Initialize the image bitmap list
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageAdapter = new ImageAdapter(imageBitmapList);
        imagesRecyclerView.setAdapter(imageAdapter);

        imageClickListener();
        setImagePickerLauncher();
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
                        imagePath = RealPathUtil.getRealPath(AddNewPlaceActivity.this, imageUri);
                        imageBitmap = BitmapFactory.decodeFile(imagePath);

                        // Add the selected image to the layout
                        imageBitmapList.add(imageBitmap);
                        imageAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    /**
     * Same listener for single and multiple images(Recycler view)
     */
    private void imageClickListener() {
        Log.d(TAG, "imageClickListener: Started");

        selectImageButton.setOnClickListener(view -> {

            if (ActivityCompat.checkSelfPermission(AddNewPlaceActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        AddNewPlaceActivity.this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }

            Toast.makeText(AddNewPlaceActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
    }


    /**
     * GOOGLE MAPS methods
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


    /**
     * Text Watchers
     */

    private void setTextWatchers() {
        setTextWatcherAddress();
        setTextWatcherStartDate();
        setTextWatcherEndDate();
        setTextWatcherMaxVisitors();
        setTextWatcherMinPrice();
        setTextWatcherExtraCost();
//        setTextWatcherPhotoUpload();
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

//    private void setTextWatcherPhotoUpload() {
//        TextWatcher textWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (addPlacePhotoUploadEditText.getText().toString().isEmpty()) {
//                    addPlaceWarningPhotoUpload.setVisibility(View.VISIBLE);
//                } else {
//                    addPlaceWarningPhotoUpload.setVisibility(View.GONE);
//                }
//            }
//        };
//        addPlacePhotoUploadEditText.addTextChangedListener(textWatcher);
//    }

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
                                String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                                startDateEditText.setText(formattedDate);
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

                // Get the selected check-in date from startDateEditText and parse it to Calendar.
                String checkInDateText = startDateEditText.getText().toString();
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
                        AddNewPlaceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                                endDateEditText.setText(formattedDate);
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
                    Toast.makeText(view.getContext(), "222 Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(view.getContext(), "Pressed ADD PLACE BUTTON", Toast.LENGTH_SHORT).show();

                ApartmentRequest apartmentRequest = setApartmentRequestData(view);
                if (apartmentRequest == null) {
                    Toast.makeText(view.getContext(), "111 Please fill all the fields correctly", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendDataToDatabase(apartmentRequest, new AddApartmentCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(view.getContext(), "Apartment added successfully", Toast.LENGTH_SHORT).show();
                        Intent host_main_page_intent = new Intent(getApplicationContext(), HostMainPageActivity.class);
                        host_main_page_intent.putExtra("user_id", userId);
                        host_main_page_intent.putExtra("user_jwt", jwtToken);
                        ArrayList<String> roleList = new ArrayList<>();
                        for (RoleName role : roles) {
                            roleList.add(role.toString());
                        }
                        host_main_page_intent.putStringArrayListExtra("user_roles", roleList);
                        startActivity(host_main_page_intent);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(view.getContext(), "Error adding apartment, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Nullable
    private ApartmentRequest setApartmentRequestData(View view) {

        ApartmentRequest apartmentRequest = new ApartmentRequest();

        // TODO: had to add address field in backend, address can be a concatenation of address, district, city, country

        // country, city, district
        apartmentRequest.setCountry(addPlaceAddressEditText.getText().toString());
        apartmentRequest.setCity(addPlaceAddressEditText.getText().toString());
        apartmentRequest.setDistrict(addPlaceAddressEditText.getText().toString());

        // maxVisitors
        try {
            int maxVisitors = Integer.parseInt(addPlaceMaxVisitorsEditText.getText().toString());
            apartmentRequest.setMaxVisitors(maxVisitors);
        } catch (NumberFormatException e) {
            Toast.makeText(view.getContext(), "Max visitors must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        // minRetailPrice
        try {
            BigDecimal minPrice = new BigDecimal(addPlaceMinPriceEditText.getText().toString());
            apartmentRequest.setMinRetailPrice(minPrice);
        } catch (NumberFormatException e) {
            Toast.makeText(view.getContext(), "Min price must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        // extraCost of every person
        try {
            BigDecimal extraCost = new BigDecimal(addPlaceExtraCostEditText.getText().toString());
            apartmentRequest.setExtraCostPerPerson(extraCost);
        } catch (NumberFormatException e) {
            Toast.makeText(view.getContext(), "Extra cost must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        // TODO: add them to the apartment data model in backend
        String rules = addPlaceRulesEditText.getText().toString();

        // description, no need to convert
        apartmentRequest.setDescription(addPlaceDescriptionEditText.getText().toString());

        // number of beds
        try {
            short beds = Short.parseShort(addPlaceBedsEditText.getText().toString());
            apartmentRequest.setNumberOfBeds(beds);
        } catch (NumberFormatException e) {
            Toast.makeText(view.getContext(), "Beds must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        // number of bedrooms
        try {
            short bedrooms = Short.parseShort(addPlaceBedroomsEditText.getText().toString());
            apartmentRequest.setNumberOfBedrooms(bedrooms);
        } catch (NumberFormatException e) {
            Toast.makeText(view.getContext(), "Bedrooms must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        // number of bathrooms
        try {
            short bathrooms = Short.parseShort(addPlaceBathroomsEditText.getText().toString());
            apartmentRequest.setNumberOfBathrooms(bathrooms);
        } catch (NumberFormatException e) {
            Toast.makeText(view.getContext(), "Bathrooms must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        // number of living rooms
        try {
            short livingRooms = Short.parseShort(addPlaceLivingRoomsEditText.getText().toString());
            apartmentRequest.setNumberOfLivingRooms(livingRooms);
        } catch (NumberFormatException e) {
            Toast.makeText(view.getContext(), "Living rooms must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        // size of area
        try {
            BigDecimal area = new BigDecimal(addPlaceAreaEditText.getText().toString());
            apartmentRequest.setArea(area);
        } catch (NumberFormatException e) {
            Toast.makeText(view.getContext(), "Area must be a number", Toast.LENGTH_SHORT).show();
            return null;
        }

        apartmentRequest.setAvailableStartDate(startDateEditText.getText().toString());
        apartmentRequest.setAvailableEndDate(endDateEditText.getText().toString());

        // latitude & longitude of address
        LatLng location = getLocationFromAddress(addPlaceAddressEditText.getText().toString());
        apartmentRequest.setGeoLat(new BigDecimal(location.latitude));
        apartmentRequest.setGeoLong(new BigDecimal(location.longitude));

        // rentalType (ROOM or HOUSE)
        apartmentRequest.setRentalType(addPlaceRentalTypeRadioGroup.getCheckedRadioButtonId() == R.id.roomTypeRadioButton ? RentalType.RENTAL_ROOM : RentalType.RENTAL_HOUSE);

        return apartmentRequest;
    }

    private void sendDataToDatabase(ApartmentRequest apartmentRequest, AddApartmentCallback apartmentCallback) {
        RestClient restClient = new RestClient(jwtToken);
        ApartmentAPI apartmentAPI = restClient.getClient().create(ApartmentAPI.class);

        apartmentAPI.createApartment(apartmentRequest)
                .enqueue(new Callback<ApartmentResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApartmentResponse> call, @NonNull Response<ApartmentResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Apartment added successfully");
                            apartmentCallback.onSuccess();
                        } else {
                            Log.d(TAG, "Error adding apartment");
                            apartmentCallback.onFailure("Error adding apartment");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApartmentResponse> call, @NonNull Throwable t) {
                        Log.d(TAG, "Error adding apartment: " + t.getMessage());
                        apartmentCallback.onFailure(t.getMessage());
                    }
        });
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
        if (imageBitmapList == null || imageBitmapList.isEmpty()) {
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
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        addPlaceMaxVisitorsEditText = findViewById(R.id.addPlaceMaxVisitorsEditText);
        addPlaceMinPriceEditText = findViewById(R.id.addPlaceMinPriceEditText);
        addPlaceExtraCostEditText = findViewById(R.id.addPlaceExtraCostEditText);
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

        /**
         * PHOTO ONLY
         */
        selectImageButton = findViewById(R.id.selectImageButton);

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
                Intent chat_intent = new Intent(AddNewPlaceActivity.this, ChatActivity.class);
                chat_intent.putExtra("user_id", userId);
                chat_intent.putExtra("user_jwt", jwtToken);
                chat_intent.putExtra("user_current_role", RoleName.ROLE_HOST.toString());
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
                Intent profile_intent = new Intent(AddNewPlaceActivity.this, ProfileActivity.class);
                profile_intent.putExtra("user_id", userId);
                profile_intent.putExtra("user_jwt", jwtToken);
                profile_intent.putExtra("user_current_role", RoleName.ROLE_HOST.toString());
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
                Log.d(TAG, "onClick: role button pressed");
                Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();

                if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                    // to be at this activity he has the user role
                    Intent main_page_intent = new Intent(AddNewPlaceActivity.this, MainPageActivity.class);
                    main_page_intent.putExtra("user_id", userId);
                    main_page_intent.putExtra("user_jwt", jwtToken);
                    ArrayList<String> roleList = new ArrayList<>();
                    for (RoleName role : roles) {
                        roleList.add(role.toString());
                    }
                    main_page_intent.putExtra("user_roles", roleList);
                    startActivity(main_page_intent);
                } else {
                    Toast.makeText(AddNewPlaceActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
