package com.dit.airbnb.util.rating_function;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.util.MaxMinApartmentValues;

public class LogWeightedRatingFunction implements RatingFunction {

    private static final double ratingWeight = 0.2;
    private static final double maxVisitorsWeight = 0.1;
    private static final double extraCostPerPersonWeight = 0.2;
    private static final double minRetailPriceWeight = 0.3;
    private static final double numberOfPlacesWeight = 0.2;

    @Override
    public Double getRate(Apartment apartment, BookingReview bookingReview, MaxMinApartmentValues maxMinApartmentValues) {
        Short numberOfBedrooms = apartment.getNumberOfBedrooms();
        Short numberOfBeds = apartment.getNumberOfBeds();
        Short numberOfBathrooms = apartment.getNumberOfBathrooms();
        Short numberOfLivingRooms =  apartment.getNumberOfLivingRooms();
        double numOfPlaces = ( (numberOfBedrooms != null ? numberOfBedrooms : 0) + (numberOfBeds != null ? numberOfBeds : 0) + (numberOfBathrooms != null ? numberOfBathrooms : 0) + (numberOfLivingRooms != null ? numberOfLivingRooms : 0) ) / 4.0;
        return  (bookingReview != null ? ratingWeight * bookingReview.getRating() : 0.0) +
                (maxVisitorsWeight * ((apartment.getMaxVisitors() - 1.0) / (maxMinApartmentValues.maxVisitorsValue - 0.1) * 5) +
                (extraCostPerPersonWeight * (apartment.getExtraCostPerPerson().doubleValue() / maxMinApartmentValues.maxExtraCostPerPersonValue) * 5)) +
                (minRetailPriceWeight * (apartment.getMinRetailPrice().doubleValue() / maxMinApartmentValues.maxRetailPriceValue) * 5) +
                (numberOfPlacesWeight * ((numOfPlaces - 0.25) / (maxMinApartmentValues.maxNumberOfPlacesValue - 0.25)) * 5);
    }

}
