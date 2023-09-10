package com.dit.airbnb.service;

import com.dit.airbnb.dto.Image;
import com.dit.airbnb.dto.Role;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.exception.AppException;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.exception.UserExistsException;
import com.dit.airbnb.repository.ImageRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.user_reg.SignInRequest;
import com.dit.airbnb.request.user_reg.SignUpRequest;
import com.dit.airbnb.request.user_reg.UserRegUpdateRequest;
import com.dit.airbnb.response.SignInResponse;
import com.dit.airbnb.response.UserRegResponse;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.security.jwt.JwtTokenProvider;
import com.dit.airbnb.security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    public ResponseEntity<?> signUpUser(SignUpRequest signUpRequest, MultipartFile image) {
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

        // Store image
        String imageName = imageService.store(image);
        if (imageName.contains(".png")) {
            imageName = imageName.split(".png")[0] + (ImageService.imageCounter) + ".png";
            ImageService.imageCounter++;
        } else {
            imageName = imageName + (ImageService.imageCounter++);
        }
        Image imageIn = new Image(imageName);
        imageIn.setUserReg(userReg);
        imageRepository.save(imageIn);

        userReg.setImage(imageIn);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/{userId}")
                .buildAndExpand(userReg.getId()).toUri();

        return ResponseEntity.created(uri).body(new ApiResponse(true, "signUpUser succeed", userReg));
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

        Set<RoleName> roleNames = new HashSet<>();
        for (Role role: userReg.getRoles()) {
            roleNames.add(role.getName());
        }

        SignInResponse signInResponseRes = SignInResponse.builder().id(userReg.getId()).jwtToken(jwt).username(userReg.getUsername())
                .email(userReg.getEmail()).firstName(userReg.getFirstName()).lastName(userReg.getLastName()).roleNames(roleNames).build();

        return ResponseEntity.ok(new ApiResponse(true, "signIn succeed", signInResponseRes));

    }

    public ResponseEntity<?> updateUserRegById(Long userRegId, UserDetailsImpl userDetails, UserRegUpdateRequest userRegUpdateRequest) {
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

        if (userRegUpdateRequest.getPhone() != null) {
            userReg.setPhone(userRegUpdateRequest.getPhone());
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

        return ResponseEntity.ok(new ApiResponse(true, "updateUserReg succeed", userReg));
    }

    public ResponseEntity<?> getUserRegById(Long userRegId, UserDetailsImpl userDetails) {
        //if (!userRegId.equals(userDetails.getId())) {
        //    throw new ResourceNotFoundException("UserReg", "id", userRegId);
        //}
        UserReg userReg = userRegRepository.findById(userRegId).orElseThrow(() -> new ResourceNotFoundException("UserReg", "id", userRegId));
        UserRegResponse userRegResponseRes = new UserRegResponse(userReg.getId(), userReg.getFirstName(),
                userReg.getLastName(), userReg.getUsername(), userReg.getEmail(), userReg.getPhone());

        return ResponseEntity.ok(new ApiResponse(true, "getUserReg succeed", userRegResponseRes));
    }

}
