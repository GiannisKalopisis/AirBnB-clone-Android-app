package com.dit.airbnb.repository;

import com.dit.airbnb.dto.BookingReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingReviewRepository extends JpaRepository<BookingReview, Long> {
}
