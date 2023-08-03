package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {

    @Query(" select case when count(a) > 0 then true else false end " +
           " from Apartment as a inner join a.userRegHost as u " +
           " where a.id = :apartmentId and u.id = :hostId")
    boolean findIfApartmentExists(@Param("apartmentId") Long apartmentId, @Param("hostId") Long hostId);

}
