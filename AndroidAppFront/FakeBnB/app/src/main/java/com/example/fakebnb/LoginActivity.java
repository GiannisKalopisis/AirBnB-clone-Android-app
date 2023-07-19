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

import androidx.appcompat.app.AppCompatActivity;

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

        Toast.makeText(this, "Welcome to LOGIN page", Toast.LENGTH_SHORT).show();

        setTextWatchers();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initRegister();
            }
        });

    }

    private void initRegister() {
        Log.d(TAG, "initRegister: Started");

        // TODO: REMOVE THIS LINE BELOW. ONLY FOR TESTING PURPOSES
        // debug purpose
        Intent main_page_intent = new Intent(getApplicationContext(), MainPageActivity.class);
        startActivity(main_page_intent);

//        if (validateData()) {
//            if (userCredentialsExists(usernameEditText.getText().toString(), passwordEditText.getText().toString())) {
//                Intent main_page_intent = new Intent(getApplicationContext(), MainPageActivity.class);
//                startActivity(main_page_intent);
//            } else {
//                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(LoginActivity.this, "Username and Password must be field", Toast.LENGTH_SHORT).show();
//        }

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

        if (usernameEditText.getText().toString().isEmpty()) {
            usernameLoginWarn.setVisibility(View.VISIBLE);
            usernameLoginWarn.setText("Enter your username");
            return false;
        }
        if (passwordEditText.getText().toString().isEmpty()) {
            passwordLoginWarn.setVisibility(View.VISIBLE);
            passwordLoginWarn.setText("Enter your password");
            return false;
        }
        return true;
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

    private boolean userCredentialsExists(String username, String password) {
        // TODO: change and communicate with BACKEND
        return username.equals("admin") && password.equals("password");
    }
}
