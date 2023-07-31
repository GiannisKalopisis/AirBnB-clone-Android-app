package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Booking;
import com.dit.airbnb.dto.UserReg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
