package com.example.fakebnb;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.request.UserRegisterModel;
import com.example.fakebnb.rest.RestClient;
import com.example.fakebnb.rest.UserRegAPI;
import com.example.fakebnb.utils.ImageUtils;
import com.example.fakebnb.utils.RealPathUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Part;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText, firstNameEditText,
            lastNameEditText, emailEditText, phoneNumberEditText;
    private RadioGroup roleGroup;
    private Button selectImageButton, registerButton;
    private TextView usernameWarn, passwordWarn, confirmPasswordWarn, firstNameWarn, lastNameWarn,
            emailWarn, phoneWarn, roleWarn, photoWarn;

    private ImageView imageView;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String imagePath;
    private Bitmap imageBitmap;
    private Part imagePart;
    /**
     * Variables for MULTIPLE IMAGES
     */
//    private List<Bitmap> imageBitmapList;
//    private RecyclerView imagesRecyclerView;
//    private ImageAdapter imageAdapter;


    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_MEDIA_IMAGES
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toast.makeText(this, "Welcome to REGISTER page", Toast.LENGTH_SHORT).show();

        initView();
        resetWarnVisibility();

        /**
         * Variables for MULTIPLE IMAGES
         */
//        imageBitmapList = new ArrayList<>(); // Initialize the image bitmap list
//        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);
//        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        imageAdapter = new ImageAdapter(imageBitmapList);
//        imagesRecyclerView.setAdapter(imageAdapter);


        setTextWatchers();

        registerButtonOnClickListener();
        imageClickListener();
        setImagePickerLauncher();

    }

    /**
     * Image picker launcher for SINGLE IMAGE
     */
    private void setImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        // At this point, you have the URI of the selected image
//                        Toast.makeText(this, "URI: " + imageUri, Toast.LENGTH_SHORT).show();
                        // You can now proceed to convert the image URI to a byte array or a File object and send it to the backend.
                        imagePath = RealPathUtil.getRealPath(RegisterActivity.this, imageUri);
//                        Toast.makeText(this, "imagePath: " + imagePath, Toast.LENGTH_SHORT).show();
                        imageBitmap = BitmapFactory.decodeFile(imagePath);
                        imageView.setImageBitmap(imageBitmap);
                    }
                }
        );
    }

    /**
     * Multiple images as RecyclerView
     */
//    private void setImagePickerLauncher() {
//        imagePickerLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri imageUri = result.getData().getData();
//                        imagePath = RealPathUtil.getRealPath(RegisterActivity.this, imageUri);
//                        imageBitmap = BitmapFactory.decodeFile(imagePath);
//
//                        // Add the selected image to the layout
//                        imageBitmapList.add(imageBitmap);
//                        imageAdapter.notifyDataSetChanged();
//                    }
//                }
//        );
//    }

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

                MultipartBody.Part imagePart = ImageUtils.getImagePart(imageBitmap, imagePath);

                RestClient restClient = new RestClient(null);
                UserRegAPI userRegAPI = restClient.getClient().create(UserRegAPI.class);

                userRegAPI.registerUser(userRegisterModel.toString(), imagePart)
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
        Set<RoleName> roles = new HashSet<>();
        roles.add(roleGroup.getCheckedRadioButtonId() == R.id.userRoleCheckBox ? RoleName.ROLE_USER : RoleName.ROLE_HOST);
        userRegisterModel.setRoleNames(roles);

        Log.d(TAG, "setUserRegisterModel: toString: " + userRegisterModel);
        Log.d(TAG, "setUserRegisterModel: Finished");
        return userRegisterModel;
    }

    /**
     * Text Watchers
     */

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
                if (imageBitmap == null) {
                    photoWarn.setVisibility(View.VISIBLE);
                } else {
                    photoWarn.setVisibility(View.GONE);
                }
            }
        };
//        imageView.addTextChangedListener(textWatcher);
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
        if (imageBitmap == null) {
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

    /**
     * Same listener for single and multiple images(Recycler view)
     */
    private void imageClickListener() {
        Log.d(TAG, "imageClickListener: Started");

        selectImageButton.setOnClickListener(view -> {

            if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        RegisterActivity.this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            } else {
                Toast.makeText(RegisterActivity.this, "1 Select Image", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(RegisterActivity.this, "2 Select Image", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Access to images is necessary", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }
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

        /**
         * Necessary for images
         */
        selectImageButton = findViewById(R.id.selectImageButton);
        imageView = findViewById(R.id.imageView);

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
