package com.teams.teams.controller;

import com.teams.teams.domain.User;
import com.teams.teams.dto.MessageDto;
import com.teams.teams.dto.SendChatMessageRequest;
import com.teams.teams.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send to:      /app/conversations/{conversationId}/send
     * Broadcast to: /topic/conversations/{conversationId}
     */
    @MessageMapping("/conversations/{conversationId}/send")
    public void sendMessage(
            @DestinationVariable Long conversationId,
            @Payload SendChatMessageRequest request,
            Principal principal) {

        Long senderId = extractUserId(principal);
        MessageDto saved = conversationService.sendMessage(conversationId, request, senderId);
        messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, saved);
    }

    private Long extractUserId(Principal principal) {
        if (principal instanceof Authentication auth &&
                auth.getPrincipal() instanceof User user) {
            return user.getId();
        }
        throw new IllegalStateException("Unauthenticated WebSocket connection");
    }
}
