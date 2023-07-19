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

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText, firstNameEditText, lastNameEditText, emailEditText, phoneNumberEditText;
    private RadioGroup roleGroup;
    private Button registerButton;
    private TextView usernameWarn, passwordWarn, confirmPasswordWarn, firstNameWarn, lastNameWarn, emailWarn, phoneWarn, roleWarn;


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
                String registerResponse = userRegisterSuccess();
                switch (registerResponse) {
                    case "OK":
                        Toast.makeText(RegisterActivity.this, "New user registered successfully", Toast.LENGTH_SHORT).show();
                        sendDataToDatabase();
                        Intent main_page_intent = new Intent(getApplicationContext(), MainPageActivity.class);
                        startActivity(main_page_intent);
                        break;
                    case "NOT_ALL_FIELDS_COMPLETED":
                        Toast.makeText(RegisterActivity.this, "Must fill all fields", Toast.LENGTH_SHORT).show();
                        break;
                    case "USERNAME_IN_USE":
                        Toast.makeText(RegisterActivity.this, "Please enter an other username", Toast.LENGTH_SHORT).show();
                        break;
                    case "PASSWORDS_NOT_MATCH":
                        Toast.makeText(RegisterActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
                        break;
                    case "REGISTER_ERROR":
                        Toast.makeText(RegisterActivity.this, "Unexpected error in backend. Please try later.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void sendDataToDatabase() {
        // TODO: must change and send data to backend

        Log.d(TAG, "sendDataToDatabase: Started");

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String role;
        int roleId = roleGroup.getCheckedRadioButtonId();
        if (roleId == R.id.userRoleCheckBox) {
            role = "User";
        } else {
            role = "Host";
        }

        Log.d(TAG, "sendDataToDatabase: username: " + username);
        Log.d(TAG, "sendDataToDatabase: password: " + password);
        Log.d(TAG, "sendDataToDatabase: firstName: " + firstName);
        Log.d(TAG, "sendDataToDatabase: lastName: " + lastName);
        Log.d(TAG, "sendDataToDatabase: email: " + email);
        Log.d(TAG, "sendDataToDatabase: phoneNumber: " + phoneNumber);
        Log.d(TAG, "sendDataToDatabase: role: " + role);
    }

    private void setTextWatchers() {
        setTextWatcherUsername();
        setTextWatcherPassword();
        setTextWatcherConfirmPassword();
        setTextWatcherFirstName();
        setTextWatcherLastName();
        setTextWatcherEmail();
        setTextWatcherPhone();
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

        registerButton = findViewById(R.id.registerButton);

        usernameWarn = findViewById(R.id.usernameWarn);
        passwordWarn = findViewById(R.id.passwordWarn);
        confirmPasswordWarn = findViewById(R.id.confirmPasswordWarn);
        firstNameWarn = findViewById(R.id.firstNameWarn);
        lastNameWarn = findViewById(R.id.lastNameWarn);
        emailWarn = findViewById(R.id.emailWarn);
        phoneWarn = findViewById(R.id.phoneWarn);
        roleWarn = findViewById(R.id.roleWarn);

        roleGroup = findViewById(R.id.roleGroup);
    }

    private String userRegisterSuccess() {
        // Communicate with BACKEND and check the fields
        if (!initRegister()) {
            if (!confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                return "PASSWORDS_DO_NOT_MATCH";
            }
            return "NOT_ALL_FIELDS_COMPLETED";
        }
//        if (error in register) {
//            return "REGISTER_ERROR";
//        }
//        if (username exists) {
//            return "USERNAME_IN_USE";
//        }
        return "OK";
    }

//    private boolean allFieldsFilled() {
//
//    }
}
