package com.dit.airbnb.dto;

import com.dit.airbnb.csv_dto.custom.BookingCSV;
import com.dit.airbnb.csv_dto.recommendation.BookingRecCSV;
import com.dit.airbnb.csv_dto.recommendation.BookingReviewRecCSV;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "check_in_date")
    private Date checkInDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(name = "check_out_date")
    private Date checkOutDate;

    @Column(name = "is_reviewed")
    private Boolean isReviewed;

    public Booking(BookingCSV bookingCSV) {
        this.checkInDate = bookingCSV.getCheckInDate();
        this.checkOutDate = bookingCSV.getCheckOutDate();
        this.isReviewed = Boolean.valueOf(bookingCSV.getIsReviewed());
    }

    // external tables
    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    @Getter
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "booking", cascade = CascadeType.ALL)
    private Set<BookingReview> bookingReviews = new HashSet<>();

    @Getter
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_reg_id")
    private UserReg userReg;

    public Booking(Date checkInDate, Date checkOutDate) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.isReviewed = false;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public void setBookingReviews(Set<BookingReview> bookingReviews) {
        this.bookingReviews = bookingReviews;
    }

    public void setUserReg(UserReg userReg) {
        this.userReg = userReg;
    }

}
