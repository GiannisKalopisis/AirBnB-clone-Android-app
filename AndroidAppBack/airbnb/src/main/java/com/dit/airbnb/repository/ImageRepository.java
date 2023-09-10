package com.dit.airbnb.repository;

import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Long>  {

    @Query("select im from Image im where im.apartment.id = :apartmentId")
    List<Image> findByApartmentId(@Param("apartmentId") Long apartmentId);

    @Query("select im from Image im where im.userReg.id = :userId")
    Image findByUserRegId(@Param("userId") Long userId);


}
