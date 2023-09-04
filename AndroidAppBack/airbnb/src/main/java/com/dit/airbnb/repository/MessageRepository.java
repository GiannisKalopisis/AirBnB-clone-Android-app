package com.dit.airbnb.repository;

import com.dit.airbnb.dto.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends PagingAndSortingRepository<Message, Long>, JpaRepository<Message, Long> {

    @Query( " select m" +
            " from UserReg as u inner join u.firstSenderChats as c inner join c.messages as m" +
            " where (u.id = :senderUserRegId) and (c.firstReceiverUserReg.id = :receiverUserRegId) and (m.isLastMessage = true)")
    Optional<Message> findLastMessageWithSendUserIdAndReceiverUserId(@Param("senderUserRegId") Long senderUserRegId, @Param("receiverUserRegId") Long receiverUserRegId);

    @Query( " select m" +
            " from UserReg as u inner join u.firstSenderChats as c inner join c.messages as m" +
            " where (u.id = :userRegId) and (m.isLastMessage = true) ORDER BY m.timeSent DESC")
    Page<Message> findByUserRegIdForSimpleUser(@Param("userRegId") Long userRegId, Pageable pageable);

    @Query( " select m" +
            " from UserReg as u inner join u.firstReceiverChats as c inner join c.messages as m" +
            " where (u.id = :userRegId) and (m.isLastMessage = true)  ORDER BY m.timeSent DESC")
    Page<Message> findByUserRegIdForHost(@Param("userRegId") Long userRegId, Pageable pageable);

    @Query("select m from Message as m where m.chat.id = :chatId")
    Page<Message> findByChatId(@Param("chatId") Long chatId, Pageable pageable);
}
