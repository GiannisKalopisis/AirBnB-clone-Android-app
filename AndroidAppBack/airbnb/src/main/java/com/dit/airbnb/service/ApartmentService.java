package com.dit.airbnb.service;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.repository.ApartmentRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.apartment.ApartmentRequest;
import com.dit.airbnb.response.ApartmentResponse;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Service
public class ApartmentService {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private UserRegRepository userRegRepository;

    public ResponseEntity<?> createApartment(UserDetailsImpl currentUser, ApartmentRequest apartmentRequest) {
        Long userId = currentUser.getId();
        UserReg userReg = userRegRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserReg", "id", userId));

        Apartment apartment = new Apartment(apartmentRequest);

        apartment.setUserRegHost(userReg);

        apartmentRepository.save(apartment);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/{apartmentId}")
                .buildAndExpand(apartment.getId()).toUri();

        return ResponseEntity.created(uri).body(new ApiResponse(true, "createApartment succeed", apartment));
    }

    public ResponseEntity<?> updateApartmentById(Long apartmentId, UserDetailsImpl currentUser, ApartmentRequest apartmentRequest) {

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", apartmentId));

        authorizationService.isHostOfTheApartment(currentUser.getId(), apartmentId);

        apartment.updateApartment(apartmentRequest);

        apartmentRepository.save(apartment);

        return ResponseEntity.ok().body(new ApiResponse(true, "updateApartment succeed", apartment));
    }

    public ResponseEntity<?> getApartmentById(Long apartmentId, UserDetailsImpl currentUser) {

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", apartmentId));

        authorizationService.isHostOfTheApartment(currentUser.getId(), apartmentId);

        ApartmentResponse apartmentResponse = new ApartmentResponse(
                apartment.getAmenities(), apartment.getAddress(), apartment.getCountry(), apartment.getCity(), apartment.getDistrict(), apartment.getAvailableStartDate(),
                apartment.getAvailableEndDate(), apartment.getMaxVisitors(), apartment.getMinRetailPrice(),
                apartment.getExtraCostPerPerson(), apartment.getDescription(), apartment.getNumberOfBeds(),
                apartment.getNumberOfBedrooms(), apartment.getNumberOfBathrooms(), apartment.getNumberOfLivingRooms(),
                apartment.getArea(), apartment.getGeoLat(), apartment.getGeoLong(), apartment.getRules(), apartment.getRentalType());

        return ResponseEntity.ok().body(new ApiResponse(true, "getApartmentById", apartmentResponse));
    }

}
