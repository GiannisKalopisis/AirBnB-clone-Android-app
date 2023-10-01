package com.dit.airbnb.util.rating_function;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.UserReg;

public class DirectRatingFunction implements RatingFunction {
    @Override
    public Double getRate(BookingReview bookingReview) {
        return (double) bookingReview.getRating();
    }
}
