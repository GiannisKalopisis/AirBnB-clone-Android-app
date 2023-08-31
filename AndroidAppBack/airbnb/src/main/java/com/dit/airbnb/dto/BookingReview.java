package com.dit.airbnb.dto;

import com.dit.airbnb.csv_dto.BookingReviewCSV;
import com.dit.airbnb.request.booking_review.BookingReviewRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_review")
public class BookingReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rating")
    private Short rating;

    @Column(name = "description")
    private String description;

    // external tables
    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_reg_id")
    private UserReg creatorUserReg;

    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    public BookingReview(BookingReviewCSV bookingReviewCSV) {
        this.rating = bookingReviewCSV.getRating();
        this.description = bookingReviewCSV.getDescription();
    }

    public BookingReview(BookingReviewRequest bookingReviewRequest) {
        this.rating = bookingReviewRequest.getRating();
        this.description = bookingReviewRequest.getDescription();
    }

}
