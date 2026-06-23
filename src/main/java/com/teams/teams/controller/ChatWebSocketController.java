package com.teams.teams.controller;

import com.teams.teams.dto.MessageDto;
import com.teams.teams.dto.SendChatMessageRequest;
import com.teams.teams.service.ConversationService;
import com.teams.teams.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CurrentUserService currentUserService;

    /**
     * Send to:      /app/conversations/{conversationId}/send
     * Broadcast to: /topic/conversations/{conversationId}
     */
    @MessageMapping("/conversations/{conversationId}/send")
    public void sendMessage(
            @DestinationVariable Long conversationId,
            @Payload SendChatMessageRequest request,
            Principal principal) {

        if (!(principal instanceof Authentication auth)) {
            throw new IllegalStateException("Unauthenticated WebSocket connection");
        }

        // Populate the SecurityContext from the WebSocket session principal so that
        // CurrentUserService (and any downstream service) resolves the correct sender
        // the same way the REST path does via JwtAuthenticationFilter.
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            Long senderId = currentUserService.getCurrentUserId();
            MessageDto saved = conversationService.sendMessage(conversationId, request, senderId);
            messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, saved);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
