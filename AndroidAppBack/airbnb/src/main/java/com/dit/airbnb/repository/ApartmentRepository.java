package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Apartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends PagingAndSortingRepository<Apartment, Long>, JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {

    @Query(" select case when count(a) > 0 then true else false end " +
           " from Apartment as a inner join a.userRegHost as u " +
           " where a.id = :apartmentId and u.id = :hostId")
    boolean findIfApartmentExists(@Param("apartmentId") Long apartmentId, @Param("hostId") Long hostId);

    @Query(" select a " +
           " from Apartment as a" +
           " where a.userRegHost.id = :hostId")
    Page<Apartment> findApartmentsByHostId(@Param("hostId") Long hostId, Pageable pageable);

}
