package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Chat;
import com.dit.airbnb.dto.Image;
import com.dit.airbnb.dto.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository  extends PagingAndSortingRepository<Chat, Long>, JpaRepository<Chat, Long> {


}
