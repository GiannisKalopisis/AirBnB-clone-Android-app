package com.dit.airbnb.controller;

import com.dit.airbnb.request.user_reg.SignInRequest;
import com.dit.airbnb.request.user_reg.SignUpRequest;
import com.dit.airbnb.request.user_reg.UserRegUpdateRequest;
import com.dit.airbnb.response.SignInResponse;
import com.dit.airbnb.response.UserRegResponse;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.security.user.CurrentUser;
import com.dit.airbnb.security.user.UserDetailsImpl;
import com.dit.airbnb.service.UserRegService;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class UserRegController {

    @Autowired
    private UserRegService userRegService;

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        userRegService.signUpUser(signUpRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Sign Up succeed"));
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        SignInResponse signInResponse = userRegService.signInUser(signInRequest);
        return ResponseEntity.ok(new ApiResponse(true, "signIn succeed", signInResponse));
    }


    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER, ROLE_HOST')")
    public ResponseEntity<?> updateUserReg(@PathVariable(value = "userId") Long userId,
                                           @Valid @RequestBody UserRegUpdateRequest userRegUpdateRequest,
                                           @Valid @CurrentUser UserDetailsImpl currentUser) {
        userRegService.updateUserRegById(userId, currentUser, userRegUpdateRequest);
        return ResponseEntity.ok(new ApiResponse(true, "updateUserReg succeed"));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER, ROLE_HOST')")
    public ResponseEntity<?> getUserReg(@PathVariable(value = "userId") Long userId,
                                        @Valid @CurrentUser UserDetailsImpl currentUser) {
        UserRegResponse userRegResponse = userRegService.getUserRegById(userId, currentUser);
        return ResponseEntity.ok(new ApiResponse(true, "getUserReg succeed", userRegResponse));
    }

}
