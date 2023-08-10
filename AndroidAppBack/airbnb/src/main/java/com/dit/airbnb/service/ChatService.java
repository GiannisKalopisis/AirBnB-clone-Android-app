package com.dit.airbnb.service;

import com.dit.airbnb.dto.Chat;
import com.dit.airbnb.dto.Message;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.repository.ChatRepository;
import com.dit.airbnb.repository.MessageRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.chat.MessageRequest;
import com.dit.airbnb.request.chat.OverviewMessageRequest;
import com.dit.airbnb.response.MessageResponse;
import com.dit.airbnb.response.generic.ApiResponse;
import com.dit.airbnb.response.generic.PagedResponse;
import com.dit.airbnb.security.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRegRepository userRegRepository;

    @Autowired
    private ValidatePageParametersService validatePageParametersService;

    public ResponseEntity<?> createMessage(UserDetailsImpl currentUser, MessageRequest messageRequest) {

        // user
        Long senderUserRegId = currentUser.getId();
        UserReg senderUserReg = userRegRepository.findById(senderUserRegId)
                .orElseThrow(() -> new ResourceNotFoundException("SenderUserReg", "id", senderUserRegId));

        // host
        Long receiverRegUserId = messageRequest.getReceiverUserRegId();
        UserReg receiverUserReg = userRegRepository.findById(receiverRegUserId)
                .orElseThrow(() -> new ResourceNotFoundException("ReceiverUserReg", "id", receiverRegUserId));

        Optional<Message> optMessage = messageRepository.findLastMessageWithSendUserIdAndReceiverUserId(senderUserRegId, receiverRegUserId);

        Message resMessage;
        if (optMessage.isPresent()) {
            Message message = optMessage.get();
            message.setIsLastMessage(false);
            message.setSeen(true);
            messageRepository.save(message);

            resMessage = new Message(messageRequest.getContent());
            resMessage.setSenderUserReg(senderUserReg);
            resMessage.setChat(message.getChat());
            messageRepository.save(resMessage);
        } else {
            Chat chat = new Chat(senderUserReg, receiverUserReg);
            chatRepository.save(chat);

            resMessage = new Message(messageRequest.getContent());
            resMessage.setChat(chat);
            resMessage.setSenderUserReg(senderUserReg);
            messageRepository.save(resMessage);
        }


        URI uri = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/{chatId}")
                .buildAndExpand(resMessage.getChat().getId()).toUri();

        return ResponseEntity.created(uri).body(new ApiResponse(true, "createMessage succeed", resMessage));
    }

    public ResponseEntity<ApiResponse> getOverviewMessagesByRegUserId(UserDetailsImpl currentUser, OverviewMessageRequest messagesRequest, int page, int size) {

        validatePageParametersService.validate(page, size);

        Long userId = currentUser.getId();

        Page<Message> messagePage;
        if (messagesRequest.getRoleName().equals(RoleName.ROLE_USER)) {
            messagePage = messageRepository.findByUserRegIdForSimpleUser(userId, PageRequest.of(page, size, Sort.by("timeSent").descending()));
        } else {
            messagePage = messageRepository.findByUserRegIdForHost(userId, PageRequest.of(page, size, Sort.by("timeSent").descending()));
        }

        PagedResponse<MessageResponse> overviewMessageResponsePagedResponse = createMessagePagedResponse(messagePage);

        return ResponseEntity.ok(new ApiResponse(true, "getOverviewMessagesByRegUserId succeed", overviewMessageResponsePagedResponse));

    }

    public ResponseEntity<ApiResponse> getMessagesByChatId(Long chatId, int page, int size) {

        validatePageParametersService.validate(page, size);

        Page<Message> messagePage = messageRepository.findByChatId(chatId, PageRequest.of(page, size, Sort.by("timeSent").descending()));

        PagedResponse<MessageResponse> messageResponsePagedResponse = createMessagePagedResponse(messagePage);

        return ResponseEntity.ok(new ApiResponse(true, "getMessagesByChatId succeed", messageResponsePagedResponse));

    }

    // constructs paged response
    private PagedResponse<MessageResponse> createMessagePagedResponse(Page<Message> messagePage) {
        if (messagePage.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), messagePage.getNumber(),
                    messagePage.getSize(), messagePage.getTotalElements(),
                    messagePage.getTotalPages(), messagePage.isLast());
        }

        List<MessageResponse> messageResponses = new ArrayList<>();
        for (Message message : messagePage) {
            messageResponses.add(new MessageResponse(message.getSenderUserReg().getUsername(), message.getContent(), message.getSeen()));
        }

        return new PagedResponse<>(messageResponses, messagePage.getNumber(),
                messagePage.getSize(), messagePage.getTotalElements(),
                messagePage.getTotalPages(), messagePage.isLast());
    }

}
