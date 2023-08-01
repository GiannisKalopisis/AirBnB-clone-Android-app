package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
