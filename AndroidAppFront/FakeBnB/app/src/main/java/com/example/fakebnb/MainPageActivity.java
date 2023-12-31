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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.Callbacks.SingleRentalImageCallback;
import com.example.fakebnb.adapter.MainPageRentalAdapter;
import com.example.fakebnb.enums.RentalType;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.RentalMainPageModel;
import com.example.fakebnb.model.SearchRentalModel;
import com.example.fakebnb.model.request.SearchRequest;
import com.example.fakebnb.model.response.RecommendationResponse;
import com.example.fakebnb.model.response.SearchPagedResponse;
import com.example.fakebnb.model.response.UserRegResponse;
import com.example.fakebnb.rest.ApartmentAPI;
import com.example.fakebnb.rest.ImageAPI;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.SearchAPI;
import com.example.fakebnb.rest.UserRegAPI;
import com.example.fakebnb.utils.NavigationUtils;
import com.google.gson.Gson;

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

import okhttp3.ResponseBody;
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

    private TextView welcomeMessage, rentInfoMessage, rentalsTextView;
    private ImageView profile_pic_layout;
    private RecyclerView rentalsRecyclerView;
    private Button chatButton, profileButton, roleButton, homePageButton;
    private LinearLayout searchFieldsLayout;
    private boolean isSearchFieldsLayoutVisible = false;

    // search variables
    private EditText districtEditText, cityEditText, countryEditText, checkInDate, checkOutDate;
    private RadioGroup rentalTypeGroup;
    private Spinner numGuestsSpinner;
    private Button searchFieldsButton;
    private List<SearchRentalModel> searchRentalResponseList = new ArrayList<>();
    private boolean searchIsOn = false, isFirstSearch = true;

    // pagination
    private final ArrayList<RentalMainPageModel> rentals = new ArrayList<>();
    private final MainPageRentalAdapter rentalAdapter = new MainPageRentalAdapter(this, rentals);
    private boolean isLoading = false, isLastPage = false;
    private int currentPage = 0;
    private final int size = 5; // Keeps track of the current page


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        initView();
        searchIsOn = false;
        isFirstSearch = true;

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
                                homeButtonClickListener();
                                homePageButton.setVisibility(View.GONE);
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

        rentalsRecyclerView.setAdapter(rentalAdapter);
        rentalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initially load the first batch of data
        loadRecommendedData();
        loadMoreRentalsOnScroll();
        setGuestNumSpinnerValues();
        searchFieldsButtonListener();
    }

    /**
     * PAGINATION METHODS
     */
    private void loadRecommendedData() {
        isLoading = true;

        RestClient restClient = new RestClient(jwtToken);
        ApartmentAPI apartmentAPI = restClient.getClient().create(ApartmentAPI.class);

        apartmentAPI.getRecommendedApartments(userId)
                .enqueue(new Callback<RecommendationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RecommendationResponse> call, @NonNull Response<RecommendationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (searchIsOn) {
                                rentalAdapter.deleteRentals();
                                rentalAdapter.setShowPriceVisibility(false);
                            }
                            searchRentalResponseList = response.body().getObject();
                            for (SearchRentalModel searchRentalModel : searchRentalResponseList) {
                                rentalAdapter.addNewRental(new RentalMainPageModel(searchRentalModel.getDescription(),
                                        searchRentalModel.getDistrict() + ", " + searchRentalModel.getCity(),
                                        searchRentalModel.getTotalCost() + " €",
                                        searchRentalModel.getAvgRating().floatValue(),
                                        searchRentalModel.getId()));
                                getSingleRentalImage(searchRentalModel.getId(), new SingleRentalImageCallback() {
                                    @Override
                                    public void onImageLoaded(Bitmap rentalImageBitmap) {
                                        rentalAdapter.addNewRentalSingleImage(searchRentalModel.getId(), rentalImageBitmap);
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Toast.makeText(MainPageActivity.this, "Error while downloading image: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(MainPageActivity.this, "Couldn't get the recommended rentals", Toast.LENGTH_SHORT).show();
                            Log.e("RECOMMENDATION_API_CALL", "Couldn't get the recommended rentals");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RecommendationResponse> call, @NonNull Throwable t) {
                        Toast.makeText(MainPageActivity.this, "Failed to connect to server and get the recommended rentals: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("RECOMMENDATION_API_CALL search", "Failed to connect to server and get the recommended rentals: " + t.getMessage());
                    }
                });

        isLoading = false;
    }

    private void loadMoreRentalsOnScroll() {
        rentalsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) rentalsRecyclerView.getLayoutManager();
                int visibleItemCount = Objects.requireNonNull(layoutManager).getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && !isLastPage && !isSearchFieldsLayoutVisible) {
                    if (searchIsOn) {
                        fetchSearchRentals();
                        currentPage++;
                    }
                }
            }
        });
    }

    /**
     * SEARCH METHODS
     */

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

    @SuppressLint("SetTextI18n")
    public void searchFieldsButtonListener() {
        searchFieldsButton.setOnClickListener(view -> {
            if (notAllSearchFieldsCompleted()) {
                Toast.makeText(view.getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
            isSearchFieldsLayoutVisible = false;
            searchFieldsLayout.setVisibility(View.GONE);
            currentPage = 0;
            searchIsOn = true;
            rentInfoMessage.setVisibility(View.GONE);
            rentalsTextView.setText("Top Search Results");
            homePageButton.setVisibility(View.VISIBLE);
            rentals.clear();
            rentalAdapter.notifyDataSetChanged();
            isFirstSearch = true;
            fetchSearchRentals();
        });
    }

    private void fetchSearchRentals() {

        isLoading = true;
        SearchRequest searchRequest = createSearchRequest();

        RestClient restClient = new RestClient(jwtToken);
        SearchAPI searchAPI = restClient.getClient().create(SearchAPI.class);

        Gson gson = new Gson();
        Log.d(TAG, gson.toJson(searchRequest));

        searchAPI.search(searchRequest, currentPage, size)
                .enqueue(new Callback<SearchPagedResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SearchPagedResponse> call, @NonNull Response<SearchPagedResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getSuccess()) {
                                searchRentalResponseList = response.body().getObject().getContent();
                                isLastPage = response.body().getObject().isLast();
                                if (isFirstSearch) {
                                    rentalAdapter.deleteRentals();
                                    rentalAdapter.setShowPriceVisibility(true);
                                    isFirstSearch = false;
                                }
                                if (searchRentalResponseList != null && !searchRentalResponseList.isEmpty()) {
                                    for (SearchRentalModel searchRentalModel : searchRentalResponseList) {
                                        rentalAdapter.addNewRental(new RentalMainPageModel(searchRentalModel.getDescription(),
                                                searchRentalModel.getDistrict() + ", " + searchRentalModel.getCity(),
                                                searchRentalModel.getTotalCost() + " €",
                                                searchRentalModel.getAvgRating().floatValue(),
                                                searchRentalModel.getId()));
                                        getSingleRentalImage(searchRentalModel.getId(), new SingleRentalImageCallback() {
                                            @Override
                                            public void onImageLoaded(Bitmap rentalImageBitmap) {
                                                rentalAdapter.addNewRentalSingleImage(searchRentalModel.getId(), rentalImageBitmap);
                                            }

                                            @Override
                                            public void onError(String errorMessage) {
                                                Toast.makeText(MainPageActivity.this, "Error while downloading image: " + errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                                rentalAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MainPageActivity.this, "Couldn't get the searched rentals", Toast.LENGTH_SHORT).show();
                                Log.e("API_CALL search", "Couldn't get the searched rentals");
                            }
                        } else {
                            Toast.makeText(MainPageActivity.this, "Couldn't get the searched rentals", Toast.LENGTH_SHORT).show();
                            Log.e("API_CALL search", "Couldn't get the searched rentals");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SearchPagedResponse> call, @NonNull Throwable t) {
                        Toast.makeText(MainPageActivity.this, "Failed to connect to server and get the searched rentals: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("API_CALL search", "Failed to connect to server and get the searched rentals: " + t.getMessage());
                    }
                });

        currentPage++;
        isLoading = false;
    }

    private void getSingleRentalImage(Long rentalId, SingleRentalImageCallback callback) {
        RestClient restClient = new RestClient(jwtToken);
        ImageAPI imageAPI = restClient.getClient().create(ImageAPI.class);

        imageAPI.getSingleApartmentImage(rentalId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Bitmap rentalImageBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            if (rentalImageBitmap != null) {
                                callback.onImageLoaded(rentalImageBitmap);
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

    private SearchRequest createSearchRequest() {
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.setDistrict(districtEditText.getText().toString());
        searchRequest.setCity(cityEditText.getText().toString());
        searchRequest.setCountry(countryEditText.getText().toString());
        searchRequest.setAvailableStartDate(checkInDate.getText().toString());
        searchRequest.setAvailableEndDate(checkOutDate.getText().toString());
        searchRequest.setRentalType(rentalTypeGroup.getCheckedRadioButtonId() == R.id.roomRentTypeCheckBox ? RentalType.RENTAL_ROOM : RentalType.RENTAL_HOUSE);
        searchRequest.setNumberOfGuests(Integer.parseInt(numGuestsSpinner.getSelectedItem().toString()));

        return searchRequest;
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
            new Handler().postDelayed(() -> searchFieldsLayout.setVisibility(View.GONE), 100); // Adjust the delay time as needed
            isSearchFieldsLayoutVisible = false;
        } else {
            // Show the search fields layout with animation
            new Handler().postDelayed(() -> searchFieldsLayout.setVisibility(View.VISIBLE), 100); // Adjust the delay time as needed
            isSearchFieldsLayoutVisible = true;
        }
    }

    private void onDatesClicked() {
        Log.d(TAG, "onDatesClicked: started");

        checkInDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    // on below line we are passing context.
                    MainPageActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, (monthOfYear + 1), dayOfMonth);
                        checkInDate.setText(formattedDate);
                    },
                    year, month, day);
            // not allow older dates to be selected
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            if (!checkOutDate.getText().toString().isEmpty()) {
                String checkOutDateText = checkOutDate.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date checkOutDate = dateFormat.parse(checkOutDateText);
                    datePickerDialog.getDatePicker().setMaxDate(Objects.requireNonNull(checkOutDate).getTime() - 1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // display date picker dialog.
            datePickerDialog.show();
        });

        checkOutDate.setOnClickListener(v -> {

            if (checkInDate.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(), "Please select check in date first", Toast.LENGTH_SHORT).show();
                return;
            }

            final Calendar c = Calendar.getInstance();

            // Get the selected check-in date from startDateEditText and parse it to Calendar.
            String checkInDateText = checkInDate.getText().toString();
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
                    MainPageActivity.this,
                    (view, year12, monthOfYear, dayOfMonth) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year12, (monthOfYear + 1), dayOfMonth);
                        checkOutDate.setText(formattedDate);
                    },
                    year, month, day);
            // not allow older dates to be selected
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            // display date picker dialog.
            datePickerDialog.show();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        searchIsOn = false;
        isFirstSearch = true;
        isSearchFieldsLayoutVisible = false;
        currentPage = 0;
        isLastPage = false;
    }

    /**
     * WELCOME DATA METHODS
     */

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void getAndSetWelcomeData() {
        Log.d(TAG, "getAndSetWelcomeData: started");

        // get username and picture
        welcomeMessage.setText("Welcome, " + userRegData.getUsername());

        RestClient restClient = new RestClient(jwtToken);
        ImageAPI imageAPI = restClient.getClient().create(ImageAPI.class);

        imageAPI.getImage(userId)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Bitmap userImageBitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            userImageBitmap = getCircularBitmap(userImageBitmap);
                            if (userImageBitmap != null) {
                                profile_pic_layout.setImageBitmap(userImageBitmap);
                                profile_pic_layout.setPadding(0, 0, 0, 0);
                            }
                        } else {
                            Toast.makeText(MainPageActivity.this, "Couldn't get user image", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "1 Couldn't get user image");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(MainPageActivity.this, "Couldn't get user image:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "2 Couldn't get user image: " + t.getMessage());
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

    private void initView() {
        Log.d(TAG, "initViews: started");

        // Get a reference to the search container
        searchFieldsLayout = findViewById(R.id.searchFieldsLayout);

        // Initializing TextView
        welcomeMessage = findViewById(R.id.welcomeMessage);
        profile_pic_layout = findViewById(R.id.profile_pic_image_view);
        rentInfoMessage = findViewById(R.id.rentInfoMessage);
        rentalsTextView = findViewById(R.id.rentalsTextView);

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

        // Go to home page button
        homePageButton = findViewById(R.id.homePageButton);

        // Initializing bottom bar buttons
        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
    }

    private void homeButtonClickListener() {
        Log.d(TAG, "homeButtonClickListener: started");

        homePageButton.setOnClickListener(view -> {
            searchIsOn = false;
            isFirstSearch = true;
            NavigationUtils.goToMainPage(MainPageActivity.this, userId, jwtToken, roles);
        });
    }

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");

        chatButton.setOnClickListener(view -> {
            NavigationUtils.goToChatPage(MainPageActivity.this, userId, jwtToken, roles, RoleName.ROLE_USER.toString());
        });

        profileButton.setOnClickListener(view -> {
            NavigationUtils.goToProfilePage(MainPageActivity.this, userId, jwtToken, roles, RoleName.ROLE_USER.toString());
        });

        roleButton.setOnClickListener(view -> {
            Log.d(TAG, "onClick: pressed role button");

            if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                NavigationUtils.goToHostMainPage(MainPageActivity.this, userId, jwtToken, roles);
            } else {
                Toast.makeText(MainPageActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Long rentalId) {
        NavigationUtils.goToRentRoomPage(MainPageActivity.this, userId, jwtToken, roles,
                                            rentalId, checkInDate, checkOutDate, numGuestsSpinner);
    }
}
