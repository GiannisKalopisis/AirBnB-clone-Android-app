package com.example.fakebnb.utils;

import android.content.Context;
import android.content.Intent;

import com.example.fakebnb.MainPageActivity;
import com.example.fakebnb.enums.RoleName;

import java.util.ArrayList;


public class NavigationUtils {

    public static void goToMainPage(Context context, String userId, String jwtToken, ArrayList<RoleName> roles) {
        Intent main_page_intent = new Intent(context, MainPageActivity.class);
        main_page_intent.putExtra("user_id", userId);
        main_page_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        main_page_intent.putStringArrayListExtra("user_roles", roleList);
        context.startActivity(main_page_intent);
    }
}
