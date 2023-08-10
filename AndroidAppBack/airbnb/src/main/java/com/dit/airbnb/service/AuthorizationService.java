package com.dit.airbnb.service;

import com.dit.airbnb.exception.NotAuthorizedException;
import com.dit.airbnb.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    private ApartmentRepository apartmentRepository;

    public void isHostOfTheApartment(Long hostId, Long apartmentId) {
        if (!apartmentRepository.findIfApartmentExists(apartmentId, hostId)) {
            throw new NotAuthorizedException("Not authorized to update the apartment with id = " + apartmentId + ", not the owner");
        }
    }
}
