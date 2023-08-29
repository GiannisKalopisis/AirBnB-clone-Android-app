package com.dit.airbnb.service;

import com.dit.airbnb.repository.BookingRepository;
import com.dit.airbnb.repository.BookingReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class BookingReviewService {

    @Autowired
    private BookingReviewRepository bookingReviewRepository;

    @Autowired
    private BookingRepository bookingRepository;



}
