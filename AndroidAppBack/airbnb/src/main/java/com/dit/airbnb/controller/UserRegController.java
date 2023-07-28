package com.dit.airbnb.controller;

import com.dit.airbnb.request.SignInRequest;
import com.dit.airbnb.request.SignUpRequest;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.service.UserRegService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
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
        return userRegService.signInUser(signInRequest);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(1);
    }


}
