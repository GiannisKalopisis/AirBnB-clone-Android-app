package com.example.fakebnb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fakebnb.Callbacks.SingleRentalImageCallback;
import com.example.fakebnb.adapter.HostMainPageRentalAdapter;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.HostRentalMainPageModel;
import com.example.fakebnb.model.RentalModel;
import com.example.fakebnb.model.response.ApartmentPagedResponse;
import com.example.fakebnb.model.response.UserRegResponse;
import com.example.fakebnb.rest.ApartmentAPI;
import com.example.fakebnb.rest.ImageAPI;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.UserRegAPI;
import com.example.fakebnb.utils.NavigationUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostMainPageActivity extends AppCompatActivity implements HostMainPageRecyclerViewInterface {

    private static final String TAG = "HostMainPageActivity";

    // User variables for main page layout
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private UserRegResponse.UserRegData userRegData;

    private TextView welcomeMessage;
    private ImageView host_profile_pic_layout;
    private RecyclerView hostRentalsRecyclerView;
    private Button chatButton, profileButton, roleButton, addRentalButton;

    // Pagination variables
    private final ArrayList<HostRentalMainPageModel> hostRentals = new ArrayList<>();
    private List<RentalModel> rentalsResponseList = new ArrayList<>();
    private HostMainPageRentalAdapter rentalAdapter;
    private boolean isLoading = false, isLastPage = false;
    private int currentPage = 0;
    private final int size = 4; // Keeps track of the current page


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_main_page);

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

        getUserData();
        bottomBarClickListeners();
        addButtonClickListener();

        rentalAdapter = new HostMainPageRentalAdapter(this, hostRentals);
        hostRentalsRecyclerView.setAdapter(rentalAdapter);
        hostRentalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMoreData();
        loadHostRentalsOnScroll();
    }


    /**
     * Get and set USER DATA
     */
    private void getUserData() {
        RestClient restClient = new RestClient(jwtToken);
        UserRegAPI userRegAPI = restClient.getClient().create(UserRegAPI.class);

        userRegAPI.getUserReg(userId)
                .enqueue(new Callback<UserRegResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserRegResponse> call, @NonNull Response<UserRegResponse> response) {
                        if (response.isSuccessful()) {
                            UserRegResponse userRegResponse = response.body();
                            if (userRegResponse != null) {
                                Log.d(TAG, "GetUserReg successful");
                                userRegData = userRegResponse.getObject();
                                getAndSetWelcomeData();
                            } else {
                                Log.d(TAG, "Couldn't get user's data");
                                Toast.makeText(HostMainPageActivity.this, "Couldn't get user's data", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Couldn't get user's data");
                            Toast.makeText(HostMainPageActivity.this, "Couldn't get user's data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserRegResponse> call, @NonNull Throwable t) {
                        Log.e(TAG, "Failed to communicate with server and get user's data: " + t.getMessage());
                        Toast.makeText(HostMainPageActivity.this, "Failed to communicate with server and get user's data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void getAndSetWelcomeData() {
        Log.d(TAG, "getAndSetWelcomeData: started");

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
                                host_profile_pic_layout.setImageBitmap(userImageBitmap);
                                host_profile_pic_layout.setPadding(0, 0, 0, 0);
                            }
                        } else {
                            Toast.makeText(HostMainPageActivity.this, "Couldn't get user image", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Couldn't get user image");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(HostMainPageActivity.this, "Couldn't get user image:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Couldn't get user image: " + t.getMessage());
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

    /**
     * Pagination methods
     */
    private void loadHostRentalsOnScroll() {
        hostRentalsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) hostRentalsRecyclerView.getLayoutManager();
                int visibleItemCount = Objects.requireNonNull(layoutManager).getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0  && !isLastPage) {
                    // Load more data when the user is near the end of the list
                    loadMoreData();
                }
            }
        });
    }

    private void loadMoreData() {
        isLoading = true;

        RestClient restClient = new RestClient(jwtToken);
        ApartmentAPI apartmentAPI = restClient.getClient().create(ApartmentAPI.class);

        apartmentAPI.getHostApartments(userId, currentPage, size)
                .enqueue(new Callback<ApartmentPagedResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApartmentPagedResponse> call, @NonNull Response<ApartmentPagedResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().getSuccess()) {
                                rentalsResponseList = response.body().getObject().getContent();
                                isLastPage = response.body().getObject().isLast();
                                if (rentalsResponseList != null) {
                                    for (RentalModel rental : rentalsResponseList) {
                                        rentalAdapter.addNewRental(new HostRentalMainPageModel(rental.getDescription(),
                                                rental.getDistrict() + ", " + rental.getCity(),
                                                rental.getAvgApartmentRating().floatValue(),
                                                rental.getId()));
                                        getSingleRentalImage(rental.getId(), new SingleRentalImageCallback() {
                                            @Override
                                            public void onImageLoaded(Bitmap rentalImageBitmap) {
                                                rentalAdapter.addNewRentalSingleImage(rental.getId(), rentalImageBitmap);
                                            }

                                            @Override
                                            public void onError(String errorMessage) {
                                                Log.d(TAG, "onError while downloading image: " + errorMessage);
                                            }
                                        });
                                    }
                                }
                                rentalAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(HostMainPageActivity.this, "Couldn't get rentals", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(HostMainPageActivity.this, "Couldn't get rentals", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApartmentPagedResponse> call, @NonNull Throwable t) {

                    }
                });


//        hostRentals.addAll(newData);
        rentalAdapter.notifyDataSetChanged();

        isLoading = false;
        currentPage++;
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

    private void addButtonClickListener() {
        addRentalButton.setOnClickListener(view -> {
            NavigationUtils.goToAddNewPlacePage(HostMainPageActivity.this, userId, jwtToken, roles);
        });
    }

    private void initView() {
        Log.d(TAG, "initView: started");

        // Initializing TextView
        welcomeMessage = findViewById(R.id.welcomeHostMessage);
        host_profile_pic_layout = findViewById(R.id.profile_pic_image_view);

        // Initializing recycler view
        hostRentalsRecyclerView = findViewById(R.id.hostRentalsRecyclerView);

        // Initializing add rental button
        addRentalButton = findViewById(R.id.addRentalButton);

        // Initializing bottom bar buttons
        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
    }

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");

        chatButton.setOnClickListener(view -> {
            NavigationUtils.goToChatPage(HostMainPageActivity.this, userId, jwtToken, roles, RoleName.ROLE_HOST.toString());
        });

        profileButton.setOnClickListener(view -> {
            NavigationUtils.goToProfilePage(HostMainPageActivity.this, userId, jwtToken, roles, RoleName.ROLE_HOST.toString());
        });

        roleButton.setOnClickListener(view -> {
            if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
                NavigationUtils.goToMainPage(HostMainPageActivity.this, userId, jwtToken, roles);
            } else {
                Toast.makeText(HostMainPageActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Long rentalId) {
        NavigationUtils.goToPlaceModificationPage(HostMainPageActivity.this, userId, jwtToken, roles, rentalId);
    }
}
