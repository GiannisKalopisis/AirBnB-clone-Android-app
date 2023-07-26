package com.dit.airbnb.service;

import com.dit.airbnb.dto.Role;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.exception.AppException;
import com.dit.airbnb.exception.UserExistsException;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.SignInRequest;
import com.dit.airbnb.request.SignUpRequest;
import com.dit.airbnb.response.SignInResponse;
import com.dit.airbnb.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Service
public class UserRegService {

    @Autowired
    private UserRegRepository userRegRepository;

    @Autowired
    private RoleService roleService;


    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void signUpUser(SignUpRequest signUpRequest) {
        userRegRepository.findByUsername(signUpRequest.getUsername())
                .ifPresent((s) -> {
                    throw new UserExistsException("A user with the same username already exists");
                });

        userRegRepository.findByEmail(signUpRequest.getEmail())
                .ifPresent((s) -> {
                    throw new UserExistsException("A user with the same email already exists");
                });

        UserReg userReg = new UserReg(signUpRequest);

        userReg.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Role role = roleService.findByName(signUpRequest.getRoleName())
                .orElseThrow(() -> new AppException("Rol not found"));
        userReg.setRoles(new HashSet<>(){{add(role);}});
        userRegRepository.save(userReg);
    }

    public ResponseEntity<?> signInUser(SignInRequest signInRequest) {
        // Check if the user exists
        UserReg userReg = userRegRepository.findByUsername(signInRequest.getUsername()).
                orElseThrow(() -> new AppException("Invalid username or password."));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getUsername(),
                        signInRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(SignInResponse.builder().id(userReg.getId()).jwtToken(jwt).username(userReg.getUsername())
                .email(userReg.getEmail()).firstName(userReg.getFirstName()).lastName(userReg.getLastName()));
    }

}
