package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Chat;
import com.dit.airbnb.dto.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository  extends JpaRepository<Chat, Long>  {
}
