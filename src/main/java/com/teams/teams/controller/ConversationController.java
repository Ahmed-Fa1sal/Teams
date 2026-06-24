package com.teams.teams.controller;

import com.teams.teams.dto.*;
import com.teams.teams.service.ConversationService;
import com.teams.teams.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(
        name = "Chat – Conversations",
        description = """
                Unified chat system supporting three conversation types:
                - **DIRECT** – private 1-to-1 conversation between two users
                - **TEAM** – shared conversation automatically created with each team
                - **CHANNEL** – shared conversation automatically created with each channel

                TEAM and CHANNEL conversations are created automatically when the parent
                entity is created. Use the GET endpoints below to retrieve them.

                All endpoints require a valid JWT Bearer token.
                Real-time messaging is available via WebSocket/STOMP at `/ws`
                (send: `/app/conversations/{id}/send`, subscribe: `/topic/conversations/{id}`).
                """
)
@SecurityRequirement(name = "Bearer Authentication")
public class ConversationController {

    private final ConversationService conversationService;
    private final CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // MY conversations
    // -------------------------------------------------------------------------

    @GetMapping("/conversations/my")
    @Operation(
            summary = "Get all conversations for the authenticated user",
            description = """
                    Returns a paginated list of every conversation (DIRECT, TEAM, CHANNEL)
                    in which the authenticated user is a member.

                    Results are sorted by latest activity: the most recent non-deleted message
                    timestamp takes precedence; conversations with no messages fall back to
                    `updatedAt`.

                    Pagination defaults: `page=0`, `size=20`
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of conversations returned"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class)))
    })
    public ResponseEntity<?> getMyConversations(
            @Parameter(hidden = true) Pageable pageable) {

        Long userId = currentUserService.getCurrentUserId();
        Page<ConversationDto> page = conversationService.getMyConversations(userId, pageable);
        return ResponseEntity.ok(
                com.teams.teams.dto.ApiResponse.success("Conversations retrieved successfully",
                        PagedResponse.of(page)));
    }

    // -------------------------------------------------------------------------
    // DIRECT conversation
    // -------------------------------------------------------------------------

    @PostMapping("/conversations/direct")
    @Operation(
            summary = "Start a direct (1-to-1) conversation",
            description = """
                    Creates a DIRECT conversation between the authenticated user and the target
                    user. If a direct conversation between these two users already exists, it is
                    returned instead of creating a duplicate.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conversation created (or existing one returned)",
                    content = @Content(schema = @Schema(implementation = ConversationDto.class))),
            @ApiResponse(responseCode = "400", description = "targetUserId missing or same as own ID",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Target user not found",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class)))
    })
    public ResponseEntity<?> createDirectConversation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(examples = @ExampleObject(value = "{\"targetUserId\": 2}"))
            )
            @Valid @RequestBody CreateDirectConversationRequest request) {

        Long requesterId = currentUserService.getCurrentUserId();
        ConversationDto conversation = conversationService.createDirectConversation(
                requesterId, request.getTargetUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.teams.teams.dto.ApiResponse.created(conversation));
    }

    // -------------------------------------------------------------------------
    // TEAM conversation
    // -------------------------------------------------------------------------

    @GetMapping("/teams/{teamId}/conversation")
    @PreAuthorize("@teamSecurityService.isTeamMember(#teamId)")
    @Operation(
            summary = "Get the team conversation",
            description = """
                    Returns the TEAM conversation that was automatically created when the team
                    was created. The caller must be a member of the team.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team conversation returned",
                    content = @Content(schema = @Schema(implementation = ConversationDto.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not a team member",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Team or its conversation not found",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class)))
    })
    public ResponseEntity<?> getTeamConversation(
            @Parameter(description = "ID of the team", example = "1", required = true)
            @PathVariable Long teamId) {

        ConversationDto conversation = conversationService.getTeamConversation(teamId);
        return ResponseEntity.ok(
                com.teams.teams.dto.ApiResponse.success("Team conversation retrieved", conversation));
    }

    // -------------------------------------------------------------------------
    // CHANNEL conversation
    // -------------------------------------------------------------------------

    @GetMapping("/channels/{channelId}/conversation")
    @PreAuthorize("@conversationSecurity.canAccessChannelConversation(#channelId)")
    @Operation(
            summary = "Get the channel conversation",
            description = """
                    Returns the CHANNEL conversation that was automatically created when the
                    channel was created.
                    Access requires channel membership, or team membership on a public channel.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Channel conversation returned",
                    content = @Content(schema = @Schema(implementation = ConversationDto.class))),
            @ApiResponse(responseCode = "403", description = "Caller does not have access to this channel",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Channel or its conversation not found",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class)))
    })
    public ResponseEntity<?> getChannelConversation(
            @Parameter(description = "ID of the channel", example = "1", required = true)
            @PathVariable Long channelId) {

        ConversationDto conversation = conversationService.getChannelConversation(channelId);
        return ResponseEntity.ok(
                com.teams.teams.dto.ApiResponse.success("Channel conversation retrieved", conversation));
    }

    // -------------------------------------------------------------------------
    // Messages
    // -------------------------------------------------------------------------

    @GetMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("@conversationSecurity.isConversationMember(#conversationId)")
    @Operation(
            summary = "Get messages in a conversation",
            description = """
                    Returns a paginated list of messages for the given conversation.
                    Only members of the conversation may call this endpoint.

                    Pagination defaults: `page=0`, `size=20`, `sort=createdAt,desc`
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of messages returned"),
            @ApiResponse(responseCode = "403", description = "Caller is not a conversation member",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conversation not found",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class)))
    })
    public ResponseEntity<?> getConversationMessages(
            @Parameter(description = "ID of the conversation", example = "1", required = true)
            @PathVariable Long conversationId,
            @Parameter(hidden = true) Pageable pageable) {

        Page<MessageDto> messages = conversationService.getConversationMessages(
                conversationId, pageable);
        return ResponseEntity.ok(
                com.teams.teams.dto.ApiResponse.success("Messages retrieved successfully", messages));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("@conversationSecurity.isConversationMember(#conversationId)")
    @Operation(
            summary = "Send a message to a conversation",
            description = """
                    Saves a new message in the conversation and returns the persisted `MessageDto`.
                    Only members of the conversation may post.

                    > **Tip:** For real-time delivery use the WebSocket endpoint instead:
                    > send to `/app/conversations/{conversationId}/send` and
                    > subscribe to `/topic/conversations/{conversationId}`.
                    > The WebSocket handler also persists the message before broadcasting.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message saved and returned",
                    content = @Content(schema = @Schema(implementation = MessageDto.class))),
            @ApiResponse(responseCode = "400", description = "Blank content or invalid replyToId",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not a conversation member",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "Conversation or reply-to message not found",
                    content = @Content(schema = @Schema(implementation = com.teams.teams.dto.ApiResponse.class)))
    })
    public ResponseEntity<?> sendMessage(
            @Parameter(description = "ID of the conversation", example = "1", required = true)
            @PathVariable Long conversationId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"content\": \"Hello team!\", \"replyToId\": null}"
                            )
                    )
            )
            @Valid @RequestBody SendChatMessageRequest request) {

        Long senderId = currentUserService.getCurrentUserId();
        MessageDto message = conversationService.sendMessage(conversationId, request, senderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.teams.teams.dto.ApiResponse.created(message));
    }
}
