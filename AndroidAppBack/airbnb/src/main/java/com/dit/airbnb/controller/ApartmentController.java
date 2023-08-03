package com.dit.airbnb.controller;

import com.dit.airbnb.request.apartment.ApartmentRequest;
import com.dit.airbnb.request.user_reg.UserRegUpdateRequest;
import com.dit.airbnb.security.user.CurrentUser;
import com.dit.airbnb.security.user.UserDetailsImpl;
import com.dit.airbnb.service.ApartmentService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class ApartmentController {

    @Autowired
    private ApartmentService apartmentService;

    @PostMapping(path = "/apartment")
    @PreAuthorize("hasRole('ROLE_HOST')")
    public ResponseEntity<?> createApartment(@RequestParam("apartmentRequest") ApartmentRequest apartmentRequest,
                                             @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.createApartment(currentUser, apartmentRequest);
    }

    @PutMapping("/apartment/{apartmentId}")
    @PreAuthorize("hasRole('ROLE_HOST')")
    public ResponseEntity<?> updateApartment(@PathVariable(value = "apartmentId") Long apartmentId,
                                             @Valid @RequestBody ApartmentRequest apartmentRequest,
                                             @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.updateApartmentById(apartmentId, currentUser, apartmentRequest);
    }

    @GetMapping("/apartment/{apartmentId}")
    @PreAuthorize("hasRole('ROLE_HOST')")
    public ResponseEntity<?> getApartmentInfo(@PathVariable(value = "apartmentId") Long apartmentId,
                                              @Valid @CurrentUser UserDetailsImpl currentUser) {
        return apartmentService.getApartmentById(apartmentId, currentUser);
    }




}
