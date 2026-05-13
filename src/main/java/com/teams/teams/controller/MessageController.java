package com.teams.teams.controller;

import com.teams.teams.dto.ApiResponse;
import com.teams.teams.dto.MessageDto;
import com.teams.teams.dto.CreateMessageRequest;
import com.teams.teams.dto.UpdateMessageRequest;
import com.teams.teams.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
@Tag(name = "Messages", description = "Message operations and messaging endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @Operation(summary = "Create a new message")
    public ResponseEntity<?> createMessage(@Valid @RequestBody CreateMessageRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        MessageDto message = messageService.createMessage(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(message));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get message by ID")
    public ResponseEntity<?> getMessageById(@PathVariable Long id) {
        MessageDto message = messageService.getMessageById(id);
        return ResponseEntity.ok(ApiResponse.success("Message retrieved successfully", message));
    }

    @GetMapping("/channel/{channelId}")
    @Operation(summary = "Get all messages in a channel")
    public ResponseEntity<?> getChannelMessages(@PathVariable Long channelId, Pageable pageable) {
        Page<MessageDto> messages = messageService.getChannelMessages(channelId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Channel messages retrieved successfully", messages));
    }

    @GetMapping("/conversation/{conversationId}")
    @Operation(summary = "Get all messages in a conversation")
    public ResponseEntity<?> getConversationMessages(@PathVariable Long conversationId, Pageable pageable) {
        Page<MessageDto> messages = messageService.getConversationMessages(conversationId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Conversation messages retrieved successfully", messages));
    }

    @GetMapping("/{id}/replies")
    @Operation(summary = "Get all replies to a message")
    public ResponseEntity<?> getMessageReplies(@PathVariable Long id, Pageable pageable) {
        Page<MessageDto> replies = messageService.getMessageReplies(id, pageable);
        return ResponseEntity.ok(ApiResponse.success("Message replies retrieved successfully", replies));
    }

    @GetMapping("/search")
    @Operation(summary = "Search messages")
    public ResponseEntity<?> searchMessages(@RequestParam String query, Pageable pageable) {
        Page<MessageDto> messages = messageService.searchMessages(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search completed", messages));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update message")
    public ResponseEntity<?> updateMessage(@PathVariable Long id, @Valid @RequestBody UpdateMessageRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        MessageDto message = messageService.updateMessage(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Message updated successfully", message));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete message")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        messageService.deleteMessage(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Message deleted successfully"));
    }
}

