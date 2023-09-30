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
public class BookingReviewRecCSV {

    @CsvBindByName(column = "listing_id")
    private Long apartmentId;

    @CsvBindByName(column = "id")
    private Long reviewId;

    @CsvBindByName(column = "reviewer_id")
    private Long reviewerId;

    @CsvBindByName(column = "reviewer_name")
    private String reviewerName;

    @CsvBindByName(column = "comments")
    private String comment;

    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "date")
    private Date reviewDate;

}
