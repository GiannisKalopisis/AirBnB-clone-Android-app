package com.dit.airbnb.repository;

import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository  extends JpaRepository<Image, Long>  {
}
