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
        System.out.println("username: " + signUpRequest.getUsername());
        System.out.println("password: " + signUpRequest.getPassword());
        System.out.println("phone: " + signUpRequest.getPhone());
        System.out.println("email: " + signUpRequest.getEmail());
        System.out.println("firstName: " + signUpRequest.getFirstName());
        System.out.println("lastName: " + signUpRequest.getLastName());
        System.out.println("roleName: " + signUpRequest.getRoleName());
        userRegService.signUpUser(signUpRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Sign Up succeed"));
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        System.out.println("username: " + signInRequest.getUsername());
        System.out.println("password: " + signInRequest.getPassword());
        return userRegService.signInUser(signInRequest);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(1);
    }


}
