package com.dit.airbnb.service;

import com.dit.airbnb.dto.Role;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.exception.AppException;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.exception.UserExistsException;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.user_reg.SignInRequest;
import com.dit.airbnb.request.user_reg.SignUpRequest;
import com.dit.airbnb.request.user_reg.UserRegUpdateRequest;
import com.dit.airbnb.response.SignInResponse;
import com.dit.airbnb.response.UserRegResponse;
import com.dit.airbnb.security.jwt.JwtTokenProvider;
import com.dit.airbnb.security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

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
        System.out.println(signUpRequest.getUsername());
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

        Set<Role> roleSet = new HashSet<>();
        for (RoleName roleName: signUpRequest.getRoleNames()) {
            Role role = roleService.findByName(roleName).orElseThrow(() -> new AppException("Role not found."));
            roleSet.add(role);
        }
        userReg.setRoles(new HashSet<>(){{addAll(roleSet);}});
        userRegRepository.save(userReg);

    }

    public SignInResponse signInUser(SignInRequest signInRequest) {
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

        return SignInResponse.builder().id(userReg.getId()).jwtToken(jwt).username(userReg.getUsername())
                .email(userReg.getEmail()).firstName(userReg.getFirstName()).lastName(userReg.getLastName()).build();

    }

    public void updateUserRegById(Long userRegId, UserDetailsImpl userDetails, UserRegUpdateRequest userRegUpdateRequest) {
        if (!userRegId.equals(userDetails.getId())) {
            throw new ResourceNotFoundException("UserReg", "id", userRegId);
        }
        UserReg userReg = userRegRepository.findById(userRegId).orElseThrow(() -> new ResourceNotFoundException("UserReg", "id", userRegId));

        if (userRegUpdateRequest.getFirstName() != null) {
            userReg.setFirstName(userRegUpdateRequest.getFirstName());
        }

        if (userRegUpdateRequest.getLastName() != null) {
            userReg.setLastName(userRegUpdateRequest.getLastName());
        }

        if (userRegUpdateRequest.getUsername() != null) {
            userRegRepository.findByUsername(userReg.getUsername()).ifPresent(user -> { throw new UserExistsException("A user with same username already exists");});
            userReg.setUsername(userReg.getUsername());
        }

        if (userRegUpdateRequest.getPhone() != null) {
            userReg.setPhone(userRegUpdateRequest.getPhone());
        }

        if (userRegUpdateRequest.getPassword() != null) {
            userReg.setPassword(passwordEncoder.encode(userRegUpdateRequest.getPassword()));
        }

        if (userRegUpdateRequest.getEmail() != null) {
            userRegRepository.findByEmail(userRegUpdateRequest.getEmail()).ifPresent((s) -> { throw new UserExistsException("A user with same email already exists ");});
            userReg.setEmail(userRegUpdateRequest.getEmail());
        }

        if (userRegUpdateRequest.getRoleNames() != null) {
            Set<Role> roleSet = new HashSet<>(userReg.getRoles());
            for (RoleName roleName: userRegUpdateRequest.getRoleNames()) {
                Role role = roleService.findByName(roleName).orElseThrow(() -> new AppException("Role not found."));
                roleSet.add(role);
            }
            userReg.setRoles(roleSet);
            userRegRepository.save(userReg);
        }

        userRegRepository.save(userReg);
    }

    public UserRegResponse getUserRegById(Long userRegId, UserDetailsImpl userDetails) {
        if (!userRegId.equals(userDetails.getId())) {
            throw new ResourceNotFoundException("UserReg", "id", userRegId);
        }
        UserReg userReg = userRegRepository.findById(userRegId).orElseThrow(() -> new ResourceNotFoundException("UserReg", "id", userRegId));
        return UserRegResponse.builder().id(userReg.getId()).username(userReg.getUsername())
                .email(userReg.getEmail()).firstName(userReg.getFirstName()).lastName(userReg.getLastName()).build();
    }

}
