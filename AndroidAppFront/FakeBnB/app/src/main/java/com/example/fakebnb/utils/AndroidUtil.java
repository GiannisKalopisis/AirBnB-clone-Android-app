package com.example.fakebnb.utils;

import android.content.Intent;

import com.example.fakebnb.MainPageActivity;
import com.example.fakebnb.enums.RoleName;
import com.example.fakebnb.model.UserModel;

import java.util.ArrayList;

public class AndroidUtil {

    public static void passUserModelAsIntent(Intent intent, UserModel userModel) {
        intent.putExtra("username", userModel.getUsername());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        String username = intent.getStringExtra("username");
        return new UserModel(username);
    }
}
