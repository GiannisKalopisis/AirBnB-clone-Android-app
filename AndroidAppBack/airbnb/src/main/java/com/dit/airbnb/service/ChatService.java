package com.dit.airbnb.service;

import com.dit.airbnb.dto.Chat;
import com.dit.airbnb.dto.Message;
import com.dit.airbnb.dto.UserReg;
import com.dit.airbnb.dto.enums.RoleName;
import com.dit.airbnb.exception.ResourceNotFoundException;
import com.dit.airbnb.repository.ChatRepository;
import com.dit.airbnb.repository.MessageRepository;
import com.dit.airbnb.repository.UserRegRepository;
import com.dit.airbnb.request.chat.ChatSenderReceiverRequest;
import com.dit.airbnb.request.chat.MessageRequest;
import com.dit.airbnb.request.chat.OverviewMessageRequest;
import com.dit.airbnb.response.ChatInfoResponse;
import com.dit.airbnb.response.MessageResponse;
import com.dit.airbnb.response.OverviewMessageResponse;
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

        // RoleName roleName = RoleName.getRoleName(messagesRequest.getRoleName());
        RoleName roleName = messagesRequest.getRoleName();

        Page<Message> messagePage;
        assert roleName != null;
        if (roleName.equals(RoleName.ROLE_USER)) {
            messagePage = messageRepository.findByUserRegIdForSimpleUser(userId, PageRequest.of(page, size, Sort.by("timeSent").descending()));
        } else {
            messagePage = messageRepository.findByUserRegIdForHost(userId, PageRequest.of(page, size, Sort.by("timeSent").descending()));
        }

        PagedResponse<OverviewMessageResponse> overviewMessageResponsePagedResponse = createOverviewMessagePagedResponse(messagePage);

        return ResponseEntity.ok(new ApiResponse(true, "getOverviewMessagesByRegUserId succeed", overviewMessageResponsePagedResponse));

    }

    public ResponseEntity<ApiResponse> getMessagesByChatId(Long chatId, int page, int size) {

        validatePageParametersService.validate(page, size);

        Page<Message> messagePage = messageRepository.findByChatId(chatId, PageRequest.of(page, size, Sort.by("timeSent").descending()));

        PagedResponse<MessageResponse> messageResponsePagedResponse = createMessagePagedResponse(messagePage);

        return ResponseEntity.ok(new ApiResponse(true, "getMessagesByChatId succeed", messageResponsePagedResponse));

    }

    // constructs paged response
    private PagedResponse<OverviewMessageResponse> createOverviewMessagePagedResponse(Page<Message> messagePage) {
        if (messagePage.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), messagePage.getNumber(),
                    messagePage.getSize(), messagePage.getTotalElements(),
                    messagePage.getTotalPages(), messagePage.isLast());
        }

        List<OverviewMessageResponse> messageResponses = new ArrayList<>();
        for (Message message : messagePage) {
            messageResponses.add(new OverviewMessageResponse(message.getChat().getId(), message.getSenderUserReg().getUsername(), message.getContent(), message.getSeen()));
        }

        return new PagedResponse<>(messageResponses, messagePage.getNumber(),
                messagePage.getSize(), messagePage.getTotalElements(),
                messagePage.getTotalPages(), messagePage.isLast());
    }

    private PagedResponse<MessageResponse> createMessagePagedResponse(Page<Message> messagePage) {
        if (messagePage.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), messagePage.getNumber(),
                    messagePage.getSize(), messagePage.getTotalElements(),
                    messagePage.getTotalPages(), messagePage.isLast());
        }

        List<MessageResponse> messageResponses = new ArrayList<>();
        for (Message message : messagePage) {
            messageResponses.add(new MessageResponse(message.getSenderUserReg().getUsername(), message.getContent()));
        }

        return new PagedResponse<>(messageResponses, messagePage.getNumber(),
                messagePage.getSize(), messagePage.getTotalElements(),
                messagePage.getTotalPages(), messagePage.isLast());
    }

    public ResponseEntity<?> getChatIdBySenderReceiver(ChatSenderReceiverRequest chatSenderReceiverRequest) {
        UserReg sender = userRegRepository.findById(chatSenderReceiverRequest.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sender", "id", chatSenderReceiverRequest.getSenderId()));

        UserReg receiver = userRegRepository.findById(chatSenderReceiverRequest.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver", "id", chatSenderReceiverRequest.getReceiverId()));

        Optional<Chat> optionalChat = chatRepository.findByFirstSenderUserRegIdAndFirstReceiverUserRegId(chatSenderReceiverRequest.getSenderId(), chatSenderReceiverRequest.getReceiverId());
        Chat chat;

        if (optionalChat.isEmpty()) {
            chat = new Chat(sender, receiver);
            chatRepository.save(chat);
        } else {
            chat = optionalChat.get();
        }

        return ResponseEntity.ok(new ApiResponse(true, "getChatIdBySenderReceiver succeed", chat.getId()));

    }

    public ResponseEntity<?> getChatInfoByChatId(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow( () -> new ResourceNotFoundException("Chat", "id", chatId));
        ChatInfoResponse chatInfoResponse = new ChatInfoResponse(chat.getFirstSenderUserReg().getId(), chat.getFirstSenderUserReg().getUsername(), chat.getFirstReceiverUserReg().getId(), chat.getFirstReceiverUserReg().getUsername());

        return ResponseEntity.ok(new ApiResponse(true, "getChatInfoByChatId succeed", chatInfoResponse));
    }

}
