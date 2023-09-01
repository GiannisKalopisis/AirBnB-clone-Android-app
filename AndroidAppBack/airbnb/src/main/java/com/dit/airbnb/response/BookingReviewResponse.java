package com.dit.airbnb.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class BookingReviewResponse {

    private String username;

    private Short rating;

    private String description;

}
