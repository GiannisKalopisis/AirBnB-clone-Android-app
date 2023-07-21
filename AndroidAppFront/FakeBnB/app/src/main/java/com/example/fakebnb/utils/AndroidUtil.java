package com.example.fakebnb.utils;

import android.content.Intent;

import com.example.fakebnb.model.UserModel;

public class AndroidUtil {

    public static void passUserModelAsIntent(Intent intent, UserModel userModel) {
        intent.putExtra("username", userModel.getUsername());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        String username = intent.getStringExtra("username");
        return new UserModel(username);
    }
}
