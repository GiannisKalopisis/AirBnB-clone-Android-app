package com.dit.airbnb.request.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class BookingRequest {

    @NotNull
    private Long apartmentId;

    @NotNull
    private Date checkInDate;

    @NotNull
    private Date checkOutDate;

}
