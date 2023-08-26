package com.dit.airbnb.request.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date checkInDate;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date checkOutDate;

}
