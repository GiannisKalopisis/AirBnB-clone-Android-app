package com.dit.airbnb.response;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class HostRentalsMainPageInfoResponse {

    private Long id;

    private String country;

    private String city;

    private String district;

    private String description;

    private Double avgApartmentRating;

}