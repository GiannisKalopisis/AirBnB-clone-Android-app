package com.dit.airbnb.controller;

import com.dit.airbnb.request.booking.BookingRequest;
import com.dit.airbnb.request.booking_review.BookingReviewRequest;
import com.dit.airbnb.security.user.CurrentUser;
import com.dit.airbnb.security.user.UserDetailsImpl;
import com.dit.airbnb.service.BookingReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class BookingReviewController {

    @Autowired
    private BookingReviewService bookingReviewService;

    @PostMapping( "/bookingReview")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingReviewRequest bookingReviewRequest,
                                           @Valid @CurrentUser UserDetailsImpl currentUser) {
        return bookingReviewService.createBookingReview(currentUser, bookingReviewRequest);
    }

    @GetMapping( "/bookingReview/{apartmentId}/ableToReview")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> ableToReview(@PathVariable(value = "apartmentId") Long apartmentId,
                                          @Valid @CurrentUser UserDetailsImpl currentUser) {
        return bookingReviewService.ableToReview(currentUser, apartmentId);
    }


}
