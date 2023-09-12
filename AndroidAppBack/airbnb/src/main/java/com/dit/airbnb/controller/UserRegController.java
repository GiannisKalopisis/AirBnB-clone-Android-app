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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/app")
public class UserRegController {

    @Autowired
    private UserRegService userRegService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/user/signUp")
    public ResponseEntity<?> signUp(@RequestParam(value = "signUpRequest") String signUpRequest,
                                    @RequestParam(value = "image") MultipartFile image)
            throws JsonParseException, JsonMappingException, IOException {
        SignUpRequest signUpRequestReal = objectMapper.readValue(signUpRequest, SignUpRequest.class);
        return userRegService.signUpUser(signUpRequestReal, image);
    }

    @PostMapping("/user/signIn")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return userRegService.signInUser(signInRequest);
    }


    @PutMapping("/user/image/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> updateUserRegWithImage(@PathVariable(value = "userId") Long userId,
                                           @RequestParam String userRegUpdateRequest,
                                           @RequestParam(value = "image") MultipartFile image,
                                           @Valid @CurrentUser UserDetailsImpl currentUser)
                    throws JsonParseException, JsonMappingException, IOException {
        UserRegUpdateRequest userRegUpdateReal = objectMapper.readValue(userRegUpdateRequest, UserRegUpdateRequest.class);
        return userRegService.updateUserRegById(userId, currentUser, userRegUpdateReal, image);
    }

    @PutMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> updateUserReg(@PathVariable(value = "userId") Long userId,
                                           @RequestParam String userRegUpdateRequest,
                                           @Valid @CurrentUser UserDetailsImpl currentUser)
            throws JsonParseException, JsonMappingException, IOException {
        UserRegUpdateRequest userRegUpdateReal = objectMapper.readValue(userRegUpdateRequest, UserRegUpdateRequest.class);
        return userRegService.updateUserRegById(userId, currentUser, userRegUpdateReal);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getUserReg(@PathVariable(value = "userId") Long userId,
                                        @Valid @CurrentUser UserDetailsImpl currentUser) {
        return userRegService.getUserRegById(userId, currentUser);
    }

    

}
