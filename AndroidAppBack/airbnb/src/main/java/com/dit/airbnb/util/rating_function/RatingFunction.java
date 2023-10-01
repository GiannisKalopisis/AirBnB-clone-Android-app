package com.dit.airbnb.util.rating_function;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.util.MaxMinApartmentValues;

public interface RatingFunction {

    default Double getRate(BookingReview bookingReview) {
        return 0.0;
    }

    default Double getRate(Apartment apartment, BookingReview bookingReview, MaxMinApartmentValues maxMinApartmentValues) {
        return 0.0;
    }
}
