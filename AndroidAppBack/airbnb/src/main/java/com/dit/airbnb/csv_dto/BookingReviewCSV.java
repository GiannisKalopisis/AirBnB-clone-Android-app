package com.dit.airbnb.csv_dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingReviewCSV {

    @CsvBindByName(column = "reviewerId")
    private Long reviewerId;

    @CsvBindByName(column = "bookingId")
    private Long bookingId;

    @CsvBindByName(column = "rating")
    private Short rating;

    @CsvBindByName(column = "description")
    private String description;
}
