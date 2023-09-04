package com.dit.airbnb.request.booking_review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingReviewRequest {

    private Long apartmentId;

    private Short rating;

    private String description;

}
