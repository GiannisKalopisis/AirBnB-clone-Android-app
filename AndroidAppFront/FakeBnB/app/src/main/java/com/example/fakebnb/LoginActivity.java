package com.example.fakebnb;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.response.SignInResponse;
import com.example.fakebnb.model.request.UserLoginModel;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.UserRegAPI;
import com.example.fakebnb.utils.NavigationUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextView usernameLoginWarn, passwordLoginWarn;
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        setTextWatchers();

        loginButton.setOnClickListener(view -> initRegister());

    }

    private void initRegister() {
        Log.d(TAG, "initRegister: Started");

        if (validateData() && validateInputLength()){

            UserLoginModel userLoginModel = new UserLoginModel();
            userLoginModel.setUsername(usernameEditText.getText().toString());
            userLoginModel.setPassword(passwordEditText.getText().toString());

            RestClient restClient = new RestClient(null);
            UserRegAPI userRegAPI = restClient.getClient().create(UserRegAPI.class);

            userRegAPI.singInUser(userLoginModel)
                .enqueue(new Callback<SignInResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SignInResponse> call, @NonNull Response<SignInResponse> response) {
                        if (response.isSuccessful()) {
                            SignInResponse signInResponse = response.body();
                            if (signInResponse != null) {
                                Log.d(TAG, "onResponse: toString: " + signInResponse);
                                SignInResponse.UserData userData = signInResponse.getObject();

                                // has both roles, so login as USER
                                if (userData.getRoleNames().contains(RoleName.ROLE_USER) && userData.getRoleNames().contains(RoleName.ROLE_HOST)) {
                                    NavigationUtils.goToMainPage(LoginActivity.this, userData.getId(), userData.getJwtToken(), userData.getRoleNames());
                                }
                                // has only user role, so login as USER
                                else if (userData.getRoleNames().contains(RoleName.ROLE_USER)) {
                                    NavigationUtils.goToMainPage(LoginActivity.this, userData.getId(), userData.getJwtToken(), userData.getRoleNames());
                                }
                                // has only host role, so login as HOST
                                else if (userData.getRoleNames().contains(RoleName.ROLE_HOST)) {
                                    NavigationUtils.goToHostMainPage(LoginActivity.this, userData.getId(), userData.getJwtToken(), userData.getRoleNames());
                                }
                                // Error at roles
                                else {
                                    Toast.makeText(LoginActivity.this, "Couldn't login. Wrong user type.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                resetWarnVisibility();
                                Toast.makeText(LoginActivity.this, "Couldn't login. Check your input again or try later", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            resetWarnVisibility();
                            Toast.makeText(LoginActivity.this, "Couldn't login. Check your input again or try later", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SignInResponse> call, @NonNull Throwable t) {
                        resetWarnVisibility();
                        Toast.makeText(LoginActivity.this, "Couldn't connect to server. Check your internet connection.", Toast.LENGTH_SHORT).show();
                        Logger.getLogger(LoginActivity.class.getName()).log(Level.SEVERE, "Error in SignIn occurred!", t);
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
        }
    }

    @SuppressLint("SetTextI18n")
    private boolean validateInputLength() {
        boolean validInput = true;
        if (usernameEditText.getText().toString().length() < 4) {
            usernameLoginWarn.setVisibility(View.VISIBLE);
            usernameLoginWarn.setText("Username must be at least 4 characters");
            validInput = false;
        } else if (usernameEditText.getText().toString().length() > 80) {
            usernameLoginWarn.setVisibility(View.VISIBLE);
            usernameLoginWarn.setText("Username must be at most 80 characters");
            validInput = false;
        }
        if (passwordEditText.getText().toString().length() < 4) {
            passwordLoginWarn.setVisibility(View.VISIBLE);
            passwordLoginWarn.setText("Password must be at least 8 characters");
            validInput = false;
        } else if (passwordEditText.getText().toString().length() > 45) {
            passwordLoginWarn.setVisibility(View.VISIBLE);
            passwordLoginWarn.setText("Password must be at most 45 characters");
            validInput = false;
        }
        return validInput;
    }

    private void setTextWatchers() {
        setTextWatcherUsername();
        setTextWatcherPassword();
    }

    private void setTextWatcherUsername() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                if (usernameEditText.getText().toString().isEmpty()) {
                    usernameLoginWarn.setText("Username cannot be empty");
                    usernameLoginWarn.setVisibility(View.VISIBLE);
                } else {
                    usernameLoginWarn.setVisibility(View.GONE);
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
                    passwordLoginWarn.setText("Password cannot be empty");
                    passwordLoginWarn.setVisibility(View.VISIBLE);
                } else {
                    passwordLoginWarn.setVisibility(View.GONE);
                }
            }
        };
        passwordEditText.addTextChangedListener(textWatcher);
    }


    @SuppressLint("SetTextI18n")
    private boolean validateData() {
        Log.d(TAG, "validateData: Started");
        boolean valid = true;

        if (usernameEditText.getText().toString().isEmpty()) {
            usernameLoginWarn.setVisibility(View.VISIBLE);
            usernameLoginWarn.setText("Enter your username");
            valid = false;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLoginWarn.setVisibility(View.VISIBLE);
            passwordLoginWarn.setText("Enter your password");
            valid = false;
        }
        return valid;
    }

    private void resetWarnVisibility() {
        usernameLoginWarn.setVisibility(View.GONE);
        passwordLoginWarn.setVisibility(View.GONE);
    }

    private void initView() {
        Log.d(TAG, "initViews: started");
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        usernameLoginWarn = findViewById(R.id.usernameLoginWarn);
        passwordLoginWarn = findViewById(R.id.passwordLoginWarn);

        loginButton = findViewById(R.id.loginPageButton);
    }
}
