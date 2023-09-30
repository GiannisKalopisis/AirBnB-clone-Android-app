package com.dit.airbnb.csv_dto.recommendation;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRecCSV {

    @CsvBindByName(column = "listing_id")
    private Long apartmentId;

    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "date")
    private Date availableDate;

    @CsvBindByName(column = "available")
    private String available;

    @CsvBindByName(column = "price")
    private String price;

}
