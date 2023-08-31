package com.dit.airbnb.service;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.exception.BadRequestException;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.exception.UserExistsException;
import com.dit.airbnb.repository.ApartmentRepository;
import com.dit.airbnb.repository.BookingRepository;
import com.dit.airbnb.repository.BookingReviewRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.booking_review.BookingReviewRequest;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class BookingReviewService {

    @Autowired
    private BookingReviewRepository bookingReviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRegRepository userRegRepository;

    @Autowired
    private ApartmentRepository apartmentRepository;

    public ResponseEntity<?> createBookingReview(UserDetailsImpl currentUser, BookingReviewRequest bookingReviewRequest) {

        Long reviewerId = currentUser.getId();
        UserReg reviewer = userRegRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer", "id", reviewerId));

        Apartment apartment = apartmentRepository.findById(bookingReviewRequest.getApartmentId()).orElseThrow(() -> new ResourceNotFoundException("Apartment", "id", bookingReviewRequest.getApartmentId()));

        List<Booking> bookingList = bookingRepository.findBookingByReviewerIdAndApartmentId(reviewerId, bookingReviewRequest.getApartmentId());

        if (bookingList.isEmpty()) {
            throw new BadRequestException("Can't review an apartment without passing the visit dates or having a booking on it");
        }

        BookingReview bookingReview = new BookingReview(bookingReviewRequest);

        bookingReview.setCreatorUserReg(reviewer);

        bookingReview.setBooking(bookingList.get(0));

        bookingReviewRepository.save(bookingReview);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/{bookingReviewId}")
                .buildAndExpand(bookingReview.getId()).toUri();

        return ResponseEntity.created(uri).body(new ApiResponse(true, "createBookingReview succeed", bookingReview));
    }

    public ResponseEntity<?> ableToReview(UserDetailsImpl currentUser, Long apartmentId) {
        Long reviewerId = currentUser.getId();
        UserReg reviewer = userRegRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer", "id", reviewerId));

        Apartment apartment = apartmentRepository.findById(apartmentId).orElseThrow(() -> new ResourceNotFoundException("Apartment", "id",apartmentId));

        List<Booking> bookingList = bookingRepository.findBookingByReviewerIdAndApartmentId(reviewerId, apartmentId);

        return ResponseEntity.ok(new ApiResponse(true, "ableToReview succeed", !bookingList.isEmpty()));
    }
}
