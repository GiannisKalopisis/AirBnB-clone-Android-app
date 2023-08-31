package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.UserReg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query( " select b " +
            " from UserReg as u, Apartment as a, Booking as b" +
            " where u.id = :reviewerId and a.id = :apartmentId and u.id = b.userReg.id and a.id = b.apartment.id and current_date() > b.checkOutDate")
    List<Booking> findBookingByReviewerIdAndApartmentId(@Param("reviewerId") Long reviewerId, @Param("apartmentId") Long apartmentId);

}
