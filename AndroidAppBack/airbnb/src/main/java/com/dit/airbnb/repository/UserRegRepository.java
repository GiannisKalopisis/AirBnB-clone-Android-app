package com.dit.airbnb.repository;

import com.dit.airbnb.dto.UserReg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRegRepository extends JpaRepository<UserReg, Long> {

    Optional<UserReg> findByUsername(String username);

    Optional<UserReg> findByEmail(String email);

}