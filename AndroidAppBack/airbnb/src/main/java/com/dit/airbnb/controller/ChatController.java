package com.dit.airbnb.controller;

import com.dit.airbnb.request.chat.ChatSenderReceiverRequest;
import com.dit.airbnb.request.chat.MessageRequest;
import com.dit.airbnb.request.chat.OverviewMessageRequest;
import com.dit.airbnb.security.user.CurrentUser;
import com.dit.airbnb.security.user.UserDetailsImpl;
import com.dit.airbnb.service.ChatService;
import com.dit.airbnb.util.PaginationConstants;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping( "/chat/message")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> createMessage(@Valid @RequestBody MessageRequest messageRequest,
                                           @Valid @CurrentUser UserDetailsImpl currentUser) {
        return chatService.createMessage(currentUser, messageRequest);
    }

    @GetMapping(path = "/chat/all", params = {"page", "size"})
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getOverviewMessagesByRegUserId(@RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
                                                    @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_SIZE) int size,
                                                    @Valid @RequestBody OverviewMessageRequest overviewMessageRequest,
                                                    @Valid @CurrentUser UserDetailsImpl currentUser) {
        return chatService.getOverviewMessagesByRegUserId(currentUser, overviewMessageRequest, page, size);
    }

    @GetMapping(path = "/chat/{chatId}", params = {"page", "size"})
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getMessagesByChatId(@RequestParam(value = "page", defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
                                                @RequestParam(value = "size", defaultValue = PaginationConstants.DEFAULT_SIZE) int size,
                                                @PathVariable(value = "chatId") Long chatId,
                                                @Valid @CurrentUser UserDetailsImpl currentUser) {
        return chatService.getMessagesByChatId(chatId, page, size);
    }

    @PostMapping(path = "/chat/senderReceiver")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_HOST')")
    public ResponseEntity<?> getChatIdBySenderReceiver(@Valid @RequestBody ChatSenderReceiverRequest chatSenderReceiverRequest,
                                                       @Valid @CurrentUser UserDetailsImpl currentUser) {
        return chatService.getChatIdBySenderReceiver(chatSenderReceiverRequest);
    }

}
