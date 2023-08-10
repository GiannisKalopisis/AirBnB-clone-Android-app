package com.dit.airbnb.dto;

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

    @Column(name = "check_in_date")
    private Date checkInDate;

    @Column(name = "check_out_date")
    private Date checkOutDate;

    @Column(name = "is_reviewed")
    private Boolean isReviewed;

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
