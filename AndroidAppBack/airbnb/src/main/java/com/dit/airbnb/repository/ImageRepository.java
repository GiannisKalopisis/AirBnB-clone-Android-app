package com.dit.airbnb.repository;

import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Long>  {
}
