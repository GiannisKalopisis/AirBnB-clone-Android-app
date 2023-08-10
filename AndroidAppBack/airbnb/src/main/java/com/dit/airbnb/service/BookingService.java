package com.dit.airbnb.service;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.repository.ApartmentRepository;
import com.dit.airbnb.repository.BookingRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.apartment.ApartmentRequest;
import com.dit.airbnb.request.booking.BookingRequest;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    @Autowired
    private UserRegRepository userRegRepository;

    public ResponseEntity<?> createBooking(UserDetailsImpl currentUser, BookingRequest bookingRequest) {

        Long userId = currentUser.getId();
        UserReg userReg = userRegRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserReg", "id", userId));

        Long apartmentId = bookingRequest.getApartmentId();
        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", apartmentId));

        Booking booking = Booking.builder().checkInDate(bookingRequest.getCheckInDate()).checkOutDate(bookingRequest.getCheckOutDate()).userReg(userReg).build();

        booking.setApartment(apartment);
        bookingRepository.save(booking);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/{bookingId}")
                .buildAndExpand(userReg.getId()).toUri();

        return ResponseEntity.created(uri).body(new ApiResponse(true, "createBooking succeed", booking));
    }

}
