package com.dit.airbnb.util.rating_function;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.UserReg;

public interface RatingFunction {

    Double getRate(UserReg userReg, Apartment apartment, Booking booking, BookingReview bookingReview);

}
