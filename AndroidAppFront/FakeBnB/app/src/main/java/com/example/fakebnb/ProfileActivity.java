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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // warning text fields
    private TextView profileNameWarn, profileEmailWarn, profileAddressWarn, profilePhoneWarn, profilePhotoWarn;
    // editable text
    private EditText profileNameEditText, profileEmailEditText, profileAddressEditText, profilePhoneEditText, profilePhotoEditText;
    // Linear View buttons
    private Button saveProfileInfoChangesButton, becomeHostButton;

    // bottom bar buttons
    private Button chatButton, profileButton, roleButton;

    private final boolean isAlreadyHost = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toast.makeText(this, "Welcome to your profile", Toast.LENGTH_SHORT).show();


        initView();
        bottomBarClickListeners();
        resetWarnVisibility();

        // Set values
        getSetDataFromBackend();

        setTextValues();
        setTextWatchers();

        // Initially hide the save button
        saveProfileInfoChangesButton.setVisibility(View.GONE);

        setButtonSaveClickListener();

        // Add host button only if USER has not the role of host
        setBecomeHostButton();
    }

    private void getSetDataFromBackend() {
        //TODO: get the data from backend
        //TODO: render the data to frontend

        // get from backend
        profileNameEditText.setText("Jane McNeil");
        profileEmailEditText.setText("jane_mcneil@gmail.com");
        profileAddressEditText.setText("Athens str. 95");
        profilePhoneEditText.setText("6987458632");
        profilePhotoEditText.setText("photo_profile_path.png");
    }

    private void setBecomeHostButton() {
        if (isAlreadyHost) {
            becomeHostButton.setVisibility(View.GONE);
        } else {
            becomeHostButton.setOnClickListener(view -> {
                resetWarnVisibility();
                Toast.makeText(view.getContext(), "You became host", Toast.LENGTH_SHORT).show();
                // send backend request
                Intent host_main_page_intent = new Intent(getApplicationContext(), HostMainPageActivity.class);
                startActivity(host_main_page_intent);
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTextValues() {
        // Set initial text for TextView fields, get it from backend
        profileNameEditText.setText("Jane McNeil");
        profileEmailEditText.setText("jane85@gmail.com");
        profileAddressEditText.setText("Athens str. 95");
        profilePhoneEditText.setText("6987458632");
        profilePhotoEditText.setText("photo_profile_path.png");
    }

    private void setButtonSaveClickListener() {
        saveProfileInfoChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Name: " + profileNameEditText.getText().toString());
                Log.d(TAG, "Email: " + profileEmailEditText.getText().toString());
                Log.d(TAG, "Address: " + profileAddressEditText.getText().toString());
                Log.d(TAG, "Phone: " + profilePhoneEditText.getText().toString());
                Log.d(TAG, "Photo: " + profilePhotoEditText.getText().toString());

                Toast.makeText(ProfileActivity.this, "Saving changes to Database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTextWatchers() {
        setTextWatcherEmail();
        setTextWatcherAddress();
        setTextWatcherPhone();
        setTextWatcherPhoto();
        setTextWatcherName();
    }

    private void setTextWatcherName() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!profileNameEditText.getText().toString().isEmpty()) {
                    profileNameWarn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                Toast.makeText(ProfileActivity.this, "Username text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
                if (profileNameEditText.getText().toString().isEmpty()) {
                    profileNameWarn.setVisibility(View.VISIBLE);
                    saveProfileInfoChangesButton.setVisibility(View.GONE);
                } else {
                    saveProfileInfoChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        profileNameEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherEmail() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!profileEmailEditText.getText().toString().isEmpty()) {
                    profileEmailWarn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                Toast.makeText(ProfileActivity.this, "Email text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
                if (profileEmailEditText.getText().toString().isEmpty()) {
                    profileEmailWarn.setVisibility(View.VISIBLE);
                    saveProfileInfoChangesButton.setVisibility(View.GONE);
                } else {
                    saveProfileInfoChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        profileEmailEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherAddress() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!profileAddressEditText.getText().toString().isEmpty()) {
                    profileAddressWarn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                Toast.makeText(ProfileActivity.this, "Address text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
                if (profileAddressEditText.getText().toString().isEmpty()) {
                    profileAddressWarn.setVisibility(View.VISIBLE);
                    saveProfileInfoChangesButton.setVisibility(View.GONE);
                } else {
                    saveProfileInfoChangesButton.setVisibility(View.VISIBLE);
                }
            }
        };
        profileAddressEditText.addTextChangedListener(textWatcher);
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
                Toast.makeText(ProfileActivity.this, "Phone text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ProfileActivity.this, "Photo text watcher: afterTextChanged", Toast.LENGTH_SHORT).show();
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

        // Initializing warning text
        profileNameWarn = findViewById(R.id.profileNameWarn);
        profileEmailWarn = findViewById(R.id.profileEmailWarn);
        profileAddressWarn = findViewById(R.id.profileAddressWarn);
        profilePhoneWarn = findViewById(R.id.profilePhoneWarn);
        profilePhotoWarn = findViewById(R.id.profilePhotoWarn);

        // Initializing editable text
        profileNameEditText = findViewById(R.id.profileNameEditText);
        profileEmailEditText = findViewById(R.id.profileEmailEditText);
        profileAddressEditText = findViewById(R.id.profileAddressEditText);
        profilePhoneEditText = findViewById(R.id.profilePhoneEditText);
        profilePhotoEditText = findViewById(R.id.profilePhotoEditText);

        // Initializing profile buttons
        saveProfileInfoChangesButton = findViewById(R.id.saveProfileInfoChangesButton);
        becomeHostButton = findViewById(R.id.becomeHostButton);

        // Initializing bottom bar buttons
        chatButton = findViewById(R.id.chatButton);
        profileButton = findViewById(R.id.profileButton);
        roleButton = findViewById(R.id.roleButton);
    }

    private void resetWarnVisibility() {
        Log.d(TAG, "resetWarnVisibility: started");

        profileNameWarn.setVisibility(View.GONE);
        profileEmailWarn.setVisibility(View.GONE);
        profileAddressWarn.setVisibility(View.GONE);
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
                Intent chat_intent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(chat_intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetWarnVisibility();
                Toast.makeText(view.getContext(), "Pressed PROFILE BUTTON", Toast.LENGTH_SHORT).show();
                Intent profile_intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile_intent);
            }
        });

        roleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetWarnVisibility();
                Toast.makeText(view.getContext(), "Pressed ROLE BUTTON", Toast.LENGTH_SHORT).show();
                // TODO: NEED TO CHANGE AND NOT GO TO HOST_REVIEW_PAGE. ONLY FOR TESTING PURPOSE!!!
                Intent profile_intent = new Intent(getApplicationContext(), HostReviewPageActivity.class);
                startActivity(profile_intent);
            }
        });
    }
}
