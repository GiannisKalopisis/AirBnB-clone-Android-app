package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Apartment;
import com.dit.airbnb.dto.BookingReview;
import com.dit.airbnb.dto.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository  extends JpaRepository<Image, Long>, PagingAndSortingRepository<Image, Long> {

    @Query("select im.id from Image im where im.apartment.id = :apartmentId")
    List<Long> findByApartmentId(@Param("apartmentId") Long apartmentId);

    @Query("select im from Image im where im.userReg.id = :userId")
    List<Image> findByUserRegId(@Param("userId") Long userId);

    Optional<Image> findFirstByApartmentId(@Param("apartmentId") Long apartmentId);

}
