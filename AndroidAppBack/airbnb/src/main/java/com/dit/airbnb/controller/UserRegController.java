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
@RequestMapping("/app")
public class UserRegController {

    @Autowired
    private UserRegService userRegService;

    @PostMapping("/user/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        return userRegService.signUpUser(signUpRequest);
    }

    @PostMapping("/user/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return userRegService.signInUser(signInRequest);
    }


    @PutMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> updateUserReg(@PathVariable(value = "userId") Long userId,
                                           @Valid @RequestBody UserRegUpdateRequest userRegUpdateRequest,
                                           @Valid @CurrentUser UserDetailsImpl currentUser) {
        return userRegService.updateUserRegById(userId, currentUser, userRegUpdateRequest);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getUserReg(@PathVariable(value = "userId") Long userId,
                                        @Valid @CurrentUser UserDetailsImpl currentUser) {
        return userRegService.getUserRegById(userId, currentUser);
    }

    


}
