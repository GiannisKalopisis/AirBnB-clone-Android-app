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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.UserRegisterModel;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.UserRegAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText, firstNameEditText,
            lastNameEditText, emailEditText, phoneNumberEditText, photoEditText;
    private RadioGroup roleGroup;
    private Button registerButton;
    private TextView usernameWarn, passwordWarn, confirmPasswordWarn, firstNameWarn, lastNameWarn,
            emailWarn, phoneWarn, roleWarn, photoWarn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toast.makeText(this, "Welcome to REGISTER page", Toast.LENGTH_SHORT).show();

        initView();
        resetWarnVisibility();
        setTextWatchers();

        registerButtonOnClickListener();
    }

    private void registerButtonOnClickListener() {
        Log.d(TAG, "registerButtonOnClickListener: Started");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetWarnVisibility();

                UserRegisterModel userRegisterModel = setUserRegisterModel();

                if (!initRegister()) {
                    if (!confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(RegisterActivity.this, "Must fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                RestClient restClient = new RestClient();
                UserRegAPI userRegAPI = restClient.getClient().create(UserRegAPI.class);

                userRegAPI.registerUser(userRegisterModel)
                        .enqueue(new Callback<UserRegisterModel>() {
                            @Override
                            public void onResponse(@NonNull Call<UserRegisterModel> call, @NonNull Response<UserRegisterModel> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onResponse: " + response.body());
                                    Intent login_intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(login_intent);
                                } else {
                                    if (response.code() == HttpURLConnection.HTTP_CONFLICT){
                                        String errorBodyString;
                                        JSONObject errorObject;
                                        try {
                                            errorBodyString = Objects.requireNonNull(response.errorBody()).string();
                                            errorObject = new JSONObject(errorBodyString);
                                        } catch (IOException | JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        String errorMessage = errorObject.optString("message");

                                        if (errorMessage.equals("A user with the same username already exists")) {
                                            Toast.makeText(RegisterActivity.this, "A user with the same username already exists", Toast.LENGTH_SHORT).show();
                                            usernameWarn.setText("Username already exists");
                                            usernameWarn.setVisibility(View.VISIBLE);
                                        } else if (errorMessage.equals("A user with the same email already exists")) {
                                            Toast.makeText(RegisterActivity.this, "A user with the same email already exists", Toast.LENGTH_SHORT).show();
                                            emailWarn.setText("Email already exists");
                                            emailWarn.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Couldn't register. Check your input again or try later", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onResponse: " + response.errorBody());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<UserRegisterModel> call, @NonNull Throwable t) {
                                Toast.makeText(RegisterActivity.this, "Unexpected error in backend. Please try later.", Toast.LENGTH_SHORT).show();
                                Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, "Error in Register occurred!", t);
                                Log.d(TAG, "onFailure: " + t.getMessage());
                            }
                        });
            }
        });
    }

    private UserRegisterModel setUserRegisterModel() {
        // TODO: must change and send data to backend

        Log.d(TAG, "setUserRegisterModel: Started");

        UserRegisterModel userRegisterModel = new UserRegisterModel();
        userRegisterModel.setUsername(usernameEditText.getText().toString());
        userRegisterModel.setPassword(passwordEditText.getText().toString());
        userRegisterModel.setFirstName(firstNameEditText.getText().toString());
        userRegisterModel.setLastName(lastNameEditText.getText().toString());
        userRegisterModel.setEmail(emailEditText.getText().toString());
        userRegisterModel.setPhone(phoneNumberEditText.getText().toString());
        userRegisterModel.setRoleName(roleGroup.getCheckedRadioButtonId() == R.id.userRoleCheckBox ? RoleName.ROLE_USER : RoleName.ROLE_HOST);

        Log.d(TAG, "setUserRegisterModel: toString: " + userRegisterModel);
        Log.d(TAG, "setUserRegisterModel: Finished");
        return userRegisterModel;
    }

    private void setTextWatchers() {
        setTextWatcherUsername();
        setTextWatcherPassword();
        setTextWatcherConfirmPassword();
        setTextWatcherFirstName();
        setTextWatcherLastName();
        setTextWatcherEmail();
        setTextWatcherPhone();
        setTextWatcherPhoto();
    }

    private void setTextWatcherUsername() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (usernameEditText.getText().toString().isEmpty()) {
                    usernameWarn.setVisibility(View.VISIBLE);
                } else {
                    usernameWarn.setVisibility(View.GONE);
                }
            }
        };
        usernameEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherPassword() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                if (passwordEditText.getText().toString().isEmpty()) {
                    passwordWarn.setText("Password cannot be empty");
                    passwordWarn.setVisibility(View.VISIBLE);
                } else {
                    passwordWarn.setVisibility(View.GONE);
                }
            }
        };
        passwordEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherConfirmPassword() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                if (confirmPasswordEditText.getText().toString().isEmpty()) {
                    confirmPasswordWarn.setText("Confirm password cannot be empty");
                    confirmPasswordWarn.setVisibility(View.VISIBLE);
                } else {
                    if (!confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                        confirmPasswordWarn.setText("Password does not match");
                        confirmPasswordWarn.setVisibility(View.VISIBLE);
                    } else {
                        confirmPasswordWarn.setVisibility(View.GONE);
                    }
                }
            }
        };
        confirmPasswordEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherFirstName() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (firstNameEditText.getText().toString().isEmpty()) {
                    firstNameWarn.setVisibility(View.VISIBLE);
                } else {
                    firstNameWarn.setVisibility(View.GONE);
                }
            }
        };
        firstNameEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherLastName() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (lastNameEditText.getText().toString().isEmpty()) {
                    lastNameWarn.setVisibility(View.VISIBLE);
                } else {
                    lastNameWarn.setVisibility(View.GONE);
                }
            }
        };
        lastNameEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherEmail() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (emailEditText.getText().toString().isEmpty()) {
                    emailWarn.setVisibility(View.VISIBLE);
                } else {
                    emailWarn.setVisibility(View.GONE);
                }
            }
        };
        emailEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherPhone() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (phoneNumberEditText.getText().toString().isEmpty()) {
                    phoneWarn.setVisibility(View.VISIBLE);
                } else {
                    phoneWarn.setVisibility(View.GONE);
                }
            }
        };
        phoneNumberEditText.addTextChangedListener(textWatcher);
    }

    private void setTextWatcherPhoto() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (photoEditText.getText().toString().isEmpty()) {
                    photoWarn.setVisibility(View.VISIBLE);
                } else {
                    photoWarn.setVisibility(View.GONE);
                }
            }
        };
        photoEditText.addTextChangedListener(textWatcher);
    }

    private boolean initRegister() {
        Log.d(TAG, "initRegister: Started");

        if (validateData()) {
            resetWarnVisibility();
            return true;
        }
        return false;
    }

    private void resetWarnVisibility() {
        usernameWarn.setVisibility(View.GONE);
        passwordWarn.setVisibility(View.GONE);
        confirmPasswordWarn.setVisibility(View.GONE);
        firstNameWarn.setVisibility(View.GONE);
        lastNameWarn.setVisibility(View.GONE);
        emailWarn.setVisibility(View.GONE);
        phoneWarn.setVisibility(View.GONE);
        roleWarn.setVisibility(View.GONE);
        photoWarn.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private boolean validateData() {
        Log.d(TAG, "validateData: Started");

        if (usernameEditText.getText().toString().isEmpty()) {
            usernameWarn.setVisibility(View.VISIBLE);
            usernameWarn.setText("Enter your username");
            return false;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordWarn.setVisibility(View.VISIBLE);
            passwordWarn.setText("Enter your password");
            return false;
        }
        if (confirmPasswordEditText.getText().toString().isEmpty()) {
            confirmPasswordWarn.setVisibility(View.VISIBLE);
            confirmPasswordWarn.setText("Enter the same password");
            return false;
        }
        if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
            confirmPasswordWarn.setVisibility(View.VISIBLE);
            confirmPasswordWarn.setText("Password and confirm password must match");
            return false;
        }
        if (firstNameEditText.getText().toString().isEmpty()) {
            firstNameWarn.setVisibility(View.VISIBLE);
            firstNameWarn.setText("Enter your First Name");
            return false;
        }
        if (lastNameEditText.getText().toString().isEmpty()) {
            lastNameWarn.setVisibility(View.VISIBLE);
            lastNameWarn.setText("Enter your Last Name");
            return false;
        }
        if (emailEditText.getText().toString().isEmpty()) {
            emailWarn.setVisibility(View.VISIBLE);
            emailWarn.setText("Enter your Email");
            return false;
        }
        if (phoneNumberEditText.getText().toString().isEmpty()) {
            phoneWarn.setVisibility(View.VISIBLE);
            phoneWarn.setText("Enter your phone number");
            return false;
        }
        if (photoEditText.getText().toString().isEmpty()) {
            photoWarn.setVisibility(View.VISIBLE);
            photoWarn.setText("Enter your photo");
            return false;
        }
        int i = roleGroup.getCheckedRadioButtonId();
        if (i != R.id.userRoleCheckBox && i != R.id.hostRoleCheckBox) {
            roleWarn.setVisibility(View.VISIBLE);
            roleWarn.setText("Enter a role");
            return false;
        }
        return true;
    }

    private void initView() {
        Log.d(TAG, "initViews: started");
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        photoEditText = findViewById(R.id.photoEditText);

        registerButton = findViewById(R.id.registerButton);

        usernameWarn = findViewById(R.id.usernameWarn);
        passwordWarn = findViewById(R.id.passwordWarn);
        confirmPasswordWarn = findViewById(R.id.confirmPasswordWarn);
        firstNameWarn = findViewById(R.id.firstNameWarn);
        lastNameWarn = findViewById(R.id.lastNameWarn);
        emailWarn = findViewById(R.id.emailWarn);
        phoneWarn = findViewById(R.id.phoneWarn);
        photoWarn = findViewById(R.id.photoWarn);
        roleWarn = findViewById(R.id.roleWarn);

        roleGroup = findViewById(R.id.roleGroup);
    }
}
