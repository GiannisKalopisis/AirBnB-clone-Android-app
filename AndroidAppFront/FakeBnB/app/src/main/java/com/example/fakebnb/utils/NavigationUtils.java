package com.example.fakebnb.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.fakebnb.AddNewPlaceActivity;
import com.example.fakebnb.ChatActivity;
import com.example.fakebnb.HostMainPageActivity;
import com.example.fakebnb.HostReviewPageActivity;
import com.example.fakebnb.IndividualChatActivity;
import com.example.fakebnb.MainPageActivity;
import com.example.fakebnb.PlaceModificationPageActivity;
import com.example.fakebnb.ProfileActivity;
import com.example.fakebnb.RentRoomPage;
import com.example.fakebnb.ReservationDoneActivity;
import com.example.fakebnb.WriteReviewActivity;
import com.example.fakebnb.enums.RoleName;

import java.util.ArrayList;
import java.util.Set;


public class NavigationUtils {

    public static void goToMainPage(Context context, Long userId, String jwtToken, Set<RoleName> roles) {
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

    public static void goToHostMainPage(Context context, Long userId, String jwtToken, Set<RoleName> roles) {
        Intent main_page_intent = new Intent(context, HostMainPageActivity.class);
        main_page_intent.putExtra("user_id", userId);
        main_page_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        main_page_intent.putStringArrayListExtra("user_roles", roleList);
        context.startActivity(main_page_intent);
    }
    
    public static void goToChatPage(Context context, Long userId, String jwtToken, Set<RoleName> roles, String currentRole) {
        Intent chat_intent = new Intent(context, ChatActivity.class);
        chat_intent.putExtra("user_id", userId);
        chat_intent.putExtra("user_jwt", jwtToken);
        chat_intent.putExtra("user_current_role", currentRole);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        chat_intent.putExtra("user_roles", roleList);
        context.startActivity(chat_intent);
    }
    
    public static void goToProfilePage(Context context, Long userId, String jwtToken, Set<RoleName> roles, String currentRole) {
        Intent profile_intent = new Intent(context, ProfileActivity.class);
        profile_intent.putExtra("user_id", userId);
        profile_intent.putExtra("user_jwt", jwtToken);
        profile_intent.putExtra("user_current_role", currentRole);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        profile_intent.putStringArrayListExtra("user_roles", roleList);
        context.startActivity(profile_intent);
    }
    
    public static void goToIndividualChatPage(Context context, Long userId, String jwtToken, Set<RoleName> roles, Long chatId, RoleName currentRole) {
        Intent individual_chat_intent = new Intent(context, IndividualChatActivity.class);
        individual_chat_intent.putExtra("user_id", userId);
        individual_chat_intent.putExtra("user_jwt", jwtToken);
        individual_chat_intent.putExtra("chat_id", chatId);
        individual_chat_intent.putExtra("user_current_role", currentRole.toString());
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        individual_chat_intent.putExtra("user_roles", roleList);
        context.startActivity(individual_chat_intent);
    }
    
    public static void goToAddNewPlacePage(Context context, Long userId, String jwtToken, Set<RoleName> roles) {
        Intent add_new_place_intent = new Intent(context, AddNewPlaceActivity.class);
        add_new_place_intent.putExtra("user_id", userId);
        add_new_place_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        add_new_place_intent.putExtra("user_roles", roleList);
        context.startActivity(add_new_place_intent);
    }
    
    public static void goToPlaceModificationPage(Context context, Long userId, String jwtToken, Set<RoleName> roles, Long rentalId) {
        Intent place_modification_intent = new Intent(context, PlaceModificationPageActivity.class);
        place_modification_intent.putExtra("user_id", userId);
        place_modification_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        place_modification_intent.putExtra("user_roles", roleList);
        place_modification_intent.putExtra("rental_id", rentalId);
        context.startActivity(place_modification_intent);
    }

    public static void goToHostReviewPage(Context context, Long userId, String jwtToken, Set<RoleName> roles, Long hostId, Long apartmentId) {
        Intent see_host_intent = new Intent(context, HostReviewPageActivity.class);
        see_host_intent.putExtra("user_id", userId);
        see_host_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        see_host_intent.putExtra("user_roles", roleList);
        see_host_intent.putExtra("host_id", hostId);
        see_host_intent.putExtra("apartment_id", apartmentId);
        context.startActivity(see_host_intent);
    }

    public static void goToReservationDonePage(Context context, Long userId, String jwtToken, Set<RoleName> roles) {
        Intent make_reservation_intent = new Intent(context, ReservationDoneActivity.class);
        make_reservation_intent.putExtra("user_id", userId);
        make_reservation_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        make_reservation_intent.putExtra("user_roles", roleList);
        context.startActivity(make_reservation_intent);
    }

    public static void goToWriteReviewPage(Context context, Long userId, String jwtToken, Set<RoleName> roles, Long apartmentId, Long hostId) {
        Intent write_review_intent = new Intent(context, WriteReviewActivity.class);
        write_review_intent.putExtra("user_id", userId);
        write_review_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        write_review_intent.putExtra("user_roles", roleList);
        write_review_intent.putExtra("rental_id", apartmentId);
        write_review_intent.putExtra("host_id", hostId);
        context.startActivity(write_review_intent);
    }

    public static void goToRentRoomPage(Context context, Long userId, String jwtToken,
                                        Set<RoleName> roles, Long rentalId, EditText checkInDate,
                                        EditText checkOutDate, Spinner numGuestsSpinner) {
        Intent rent_room_intent = new Intent(context, RentRoomPage.class);
        rent_room_intent.putExtra("user_id", userId);
        rent_room_intent.putExtra("user_jwt", jwtToken);
        ArrayList<String> roleList = new ArrayList<>();
        for (RoleName role : roles) {
            roleList.add(role.toString());
        }
        rent_room_intent.putExtra("user_roles", roleList);
        rent_room_intent.putExtra("rental_id", rentalId);
        rent_room_intent.putExtra("check_in_date", checkInDate.getText().toString());
        rent_room_intent.putExtra("check_out_date", checkOutDate.getText().toString());
        Integer numGuests;
        try {
            numGuests = Integer.parseInt(numGuestsSpinner.getSelectedItem().toString());
        } catch (NumberFormatException e) {
            numGuests = null;
        }
        rent_room_intent.putExtra("num_of_guests", numGuests);
        context.startActivity(rent_room_intent);
    }
}
