package com.dit.airbnb.csv_dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingCSV {

    @CsvBindByName(column = "userId")
    private Long userId;

    @CsvBindByName(column = "checkInDate")
    private Date checkInDate;

    @CsvBindByName(column = "checkOutDate")
    private Date checkOutDate;

    @CsvBindByName(column = "isReviewed")
    private Boolean isReviewed;


}
