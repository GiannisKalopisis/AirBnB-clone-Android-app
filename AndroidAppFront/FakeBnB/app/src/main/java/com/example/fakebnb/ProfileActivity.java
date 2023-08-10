package com.example.fakebnb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.request.UserRegUpdateRequest;
import com.example.fakebnb.model.response.UserRegResponse;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.UserRegAPI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // User variables for main page layout
    private Long userId;
    private String jwtToken;
    private Set<RoleName> roles;
    private RoleName currentRole;
    private UserRegResponse.UserRegData userRegData;

    // warning text fields
    private TextView profileUserUsernameView, profileUserEmailView;
    private TextView profileFirstNameWarn, profileLastNameWarn, profilePhoneWarn, profilePhotoWarn;
    // editable text
    private EditText profileFirstNameEditText, profileLastNameEditText, profilePhoneEditText, profilePhotoEditText;
    // Linear View buttons
    private Button saveProfileInfoChangesButton, takeExtraRoleButton;

    // bottom bar buttons
    private Button chatButton, profileButton, roleButton;

    private final boolean isAlreadyHost = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toast.makeText(this, "Welcome to your profile", Toast.LENGTH_SHORT).show();

        initView();

        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getSerializableExtra("user_id", Long.class);
            jwtToken = intent.getSerializableExtra("user_jwt", String.class);
            currentRole = RoleName.valueOf(intent.getStringExtra("user_current_role"));
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
                                setData();
                                saveProfileInfoChangesButton.setVisibility(View.GONE);
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


        // Set values
        resetWarnVisibility();
        setTextWatchers();

        setButtonSaveClickListener();

        setTakeRoleButton();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {

        profileUserUsernameView.setText(userRegData.getUsername());
        profileUserEmailView.setText(userRegData.getEmail());

        profileFirstNameEditText.setText(userRegData.getFirstName());
        profileLastNameEditText.setText(userRegData.getLastName());
        profilePhoneEditText.setText(userRegData.getPhone());

        // get from backend
        profilePhotoEditText.setText("photo_profile_path.png");
    }

    @SuppressLint("SetTextI18n")
    private void setTakeRoleButton() {
        if (roles.contains(RoleName.ROLE_HOST) && roles.contains(RoleName.ROLE_USER)) {
            takeExtraRoleButton.setVisibility(View.GONE);
        } else {
            if (roles.contains(RoleName.ROLE_HOST)) {
                takeExtraRoleButton.setText("Become a User");

            } else {
                takeExtraRoleButton.setText("Become a Host");
            }

            takeExtraRoleButton.setOnClickListener(view -> {
                resetWarnVisibility();

                Set<RoleName> newRole = new HashSet<>();
                newRole.add(RoleName.ROLE_USER);
                newRole.add(RoleName.ROLE_HOST);

                UserRegUpdateRequest userRegUpdateRequest = new UserRegUpdateRequest();
                userRegUpdateRequest.setRoleNames(newRole);

                RestClient restClient = new RestClient(jwtToken);
                UserRegAPI userRegAPI = restClient.getClient().create(UserRegAPI.class);

                userRegAPI.updateUserReg(userId, userRegUpdateRequest)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.d("API_CALL", "UpdateUserReg successful");
                                    Toast.makeText(view.getContext(), "New role added", Toast.LENGTH_SHORT).show();

                                    if (roles.contains(RoleName.ROLE_HOST)) {
                                        roles.add(RoleName.ROLE_USER);
                                        // add user role so go to user's main page
                                        Intent main_page_intent = new Intent(ProfileActivity.this, MainPageActivity.class);
                                        main_page_intent.putExtra("user_id", userId);
                                        main_page_intent.putExtra("user_jwt", jwtToken);
                                        ArrayList<String> roleList = new ArrayList<>();
                                        for (RoleName role : roles) {
                                            roleList.add(role.toString());
                                        }
                                        main_page_intent.putExtra("user_roles", roleList);
                                        startActivity(main_page_intent);
                                    } else {
                                        roles.add(RoleName.ROLE_HOST);
                                        // add host role so go to host's main page
                                        Intent host_main_page_intent = new Intent(ProfileActivity.this, HostMainPageActivity.class);
                                        host_main_page_intent.putExtra("user_id", userId);
                                        host_main_page_intent.putExtra("user_jwt", jwtToken);
                                        ArrayList<String> roleList = new ArrayList<>();
                                        for (RoleName role : roles) {
                                            roleList.add(role.toString());
                                        }
                                        host_main_page_intent.putExtra("user_roles", roleList);
                                        startActivity(host_main_page_intent);
                                    }
                                } else {
                                    // Handle unsuccessful response
                                    Log.d("API_CALL", "UpdateUserReg failed");
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                // Handle failure
                                Log.e("API_CALL", "Error: " + t.getMessage());
                            }
                        });
            });
        }
    }

    private void setButtonSaveClickListener() {
        saveProfileInfoChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ProfileActivity.this, "Saving changes to Database", Toast.LENGTH_SHORT).show();

                UserRegUpdateRequest userRegUpdateRequest = setUpdateUserRegRequestValues();
                // TODO: add photo update

                RestClient restClient = new RestClient(jwtToken);
                UserRegAPI userRegAPI = restClient.getClient().create(UserRegAPI.class);

                userRegAPI.updateUserReg(userId, userRegUpdateRequest)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d("API_CALL", "User's info updated successfully");
                                Toast.makeText(ProfileActivity.this, "User's info updated successfully", Toast.LENGTH_SHORT).show();
                                saveProfileInfoChangesButton.setVisibility(View.GONE);
                            } else {
                                Log.e("API_CALL", "Couldn't update the user's info");
                                Toast.makeText(ProfileActivity.this, "Couldn't update the user's info", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Log.e("API_CALL", "Error: " + t.getMessage());
                            Toast.makeText(ProfileActivity.this, "Error at update user's info", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    }

    @NonNull
    private UserRegUpdateRequest setUpdateUserRegRequestValues() {
        UserRegUpdateRequest userRegUpdateRequest = new UserRegUpdateRequest();
        userRegUpdateRequest.setFirstName(profileFirstNameEditText.getText().toString());
        userRegUpdateRequest.setLastName(profileLastNameEditText.getText().toString());
        userRegUpdateRequest.setPhone(profilePhoneEditText.getText().toString());
        return userRegUpdateRequest;
    }

    private void setTextWatchers() {
        setTextWatcherFirstName();
        setTextWatcherLastName();
        setTextWatcherPhone();
        setTextWatcherPhoto();
    }

    private void setTextWatcherFirstName() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!profileFirstNameEditText.getText().toString().isEmpty()) {
                    profileFirstNameWarn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
//                Toast.makeText(ProfileActivity.this, "First name text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
                if (profileFirstNameEditText.getText().toString().isEmpty()) {
                    profileFirstNameWarn.setVisibility(View.VISIBLE);
                    saveProfileInfoChangesButton.setVisibility(View.GONE);
                } else {
                    saveProfileInfoChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        profileFirstNameEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherLastName() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!profileLastNameEditText.getText().toString().isEmpty()) {
                    profileLastNameWarn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
//                Toast.makeText(ProfileActivity.this, "Last name text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
                if (profileLastNameEditText.getText().toString().isEmpty()) {
                    profileLastNameWarn.setVisibility(View.VISIBLE);
                    saveProfileInfoChangesButton.setVisibility(View.GONE);
                } else {
                    saveProfileInfoChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        profileLastNameEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherPhone() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!profilePhoneEditText.getText().toString().isEmpty()) {
                    profilePhoneWarn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
//                Toast.makeText(ProfileActivity.this, "Phone text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
                if (profilePhoneEditText.getText().toString().isEmpty()) {
                    profilePhoneWarn.setVisibility(View.VISIBLE);
                    saveProfileInfoChangesButton.setVisibility(View.GONE);
                } else {
                    saveProfileInfoChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        profilePhoneEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherPhoto() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!profilePhotoEditText.getText().toString().isEmpty()) {
                    profilePhotoWarn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
//                Toast.makeText(ProfileActivity.this, "Photo text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
                if (profilePhotoEditText.getText().toString().isEmpty()) {
                    profilePhotoWarn.setVisibility(View.VISIBLE);
                    saveProfileInfoChangesButton.setVisibility(View.GONE);
                } else {
                    saveProfileInfoChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        profilePhotoEditText.addTextChangedListener(textWatcher);
    }

    private void initView() {
        Log.d(TAG, "initViews: started");

        // Initializing uneditable text
        profileUserUsernameView = findViewById(R.id.profileUserUsernameView);
        profileUserEmailView = findViewById(R.id.profileUserEmailView);

        // Initializing warning text
        profileFirstNameWarn = findViewById(R.id.profileFirstNameWarn);
        profileLastNameWarn = findViewById(R.id.profileLastNameWarn);
        profilePhoneWarn = findViewById(R.id.profilePhoneWarn);
        profilePhotoWarn = findViewById(R.id.profilePhotoWarn);

        // Initializing editable text
        profileFirstNameEditText = findViewById(R.id.profileFirstNameEditText);
        profileLastNameEditText = findViewById(R.id.profileLastNameEditText);
        profilePhoneEditText = findViewById(R.id.profilePhoneEditText);
        profilePhotoEditText = findViewById(R.id.profilePhotoEditText);

        // Initializing profile buttons
        saveProfileInfoChangesButton = findViewById(R.id.saveProfileInfoChangesButton);
        takeExtraRoleButton = findViewById(R.id.takeExtraRoleButton);

        // Initializing bottom bar buttons
        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
    }

    private void resetWarnVisibility() {
        Log.d(TAG, "resetWarnVisibility: started");

        profileFirstNameWarn.setVisibility(View.GONE);
        profileLastNameWarn.setVisibility(View.GONE);
        profilePhoneWarn.setVisibility(View.GONE);
        profilePhotoWarn.setVisibility(View.GONE);
    }

    private void bottomBarClickListeners() {
        Log.d(TAG, "bottomBarClickListeners: started");

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetWarnVisibility();
                Toast.makeText(view.getContext(), "Pressed CHAT BUTTON", Toast.LENGTH_SHORT).show();
                Intent chat_intent = new Intent(ProfileActivity.this, ChatActivity.class);
                chat_intent.putExtra("user_id", userId);
                chat_intent.putExtra("user_jwt", jwtToken);
                chat_intent.putExtra("user_current_role", currentRole.toString());
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
                resetWarnVisibility();
                Toast.makeText(view.getContext(), "Pressed PROFILE BUTTON", Toast.LENGTH_SHORT).show();
                Intent profile_intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                profile_intent.putExtra("user_id", userId);
                profile_intent.putExtra("user_jwt", jwtToken);
                profile_intent.putExtra("user_current_role", currentRole.toString());
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
                    resetWarnVisibility();
                    if (currentRole == RoleName.ROLE_USER) {
                        Intent host_main_page_intent = new Intent(ProfileActivity.this, HostMainPageActivity.class);
                        host_main_page_intent.putExtra("user_id", userId);
                        host_main_page_intent.putExtra("user_jwt", jwtToken);
                        ArrayList<String> roleList = new ArrayList<>();
                        for (RoleName role : roles) {
                            roleList.add(role.toString());
                        }
                        host_main_page_intent.putExtra("user_roles", roleList);
                        startActivity(host_main_page_intent);
                    } else if (currentRole == RoleName.ROLE_HOST) {
                        Intent main_page_intent = new Intent(ProfileActivity.this, MainPageActivity.class);
                        main_page_intent.putExtra("user_id", userId);
                        main_page_intent.putExtra("user_jwt", jwtToken);
                        ArrayList<String> roleList = new ArrayList<>();
                        for (RoleName role : roles) {
                            roleList.add(role.toString());
                        }
                        main_page_intent.putExtra("user_roles", roleList);
                        startActivity(main_page_intent);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Do not have another role in the app to change", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
