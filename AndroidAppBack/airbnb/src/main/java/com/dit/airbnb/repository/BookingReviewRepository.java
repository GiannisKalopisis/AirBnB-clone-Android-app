package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingReviewRepository extends PagingAndSortingRepository<BookingReview, Long>, JpaRepository<BookingReview, Long> {

    @Query( " select br" +
            " from Apartment as a inner join a.bookings as b inner join b.bookingReviews as br" +
            " where a.id = :apartmentId")
    Page<BookingReview> findBookingReviewsByApartmentId(@Param("apartmentId") Long apartmentId, Pageable pageable);

}
