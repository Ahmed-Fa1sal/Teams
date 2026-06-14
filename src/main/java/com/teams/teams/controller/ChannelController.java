package com.teams.teams.controller;

import com.teams.teams.dto.ApiResponse;
import com.teams.teams.dto.ChannelDto;
import com.teams.teams.dto.CreateChannelRequest;
import com.teams.teams.dto.UpdateChannelRequest;
import com.teams.teams.service.ChannelService;
import com.teams.teams.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/channels")
@Tag(name = "Channels", description = "Channel management and channel operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ChannelController {

    private final ChannelService channelService;
    private final CurrentUserService currentUserService;

    public ChannelController(ChannelService channelService, CurrentUserService currentUserService) {
        this.channelService = channelService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/{teamId}")
    @Operation(summary = "Create a new channel")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<?> createChannel(@PathVariable Long teamId, @Valid @RequestBody CreateChannelRequest request) {
        Long userId = currentUserService.getCurrentUserId();

        ChannelDto channel = channelService.createChannel(teamId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(channel));
    }

    @GetMapping("/{teamId}/{id}")
    @Operation(summary = "Get channel by ID")
    @PreAuthorize("@teamSecurityService.isTeamMember(#teamId) or @teamSecurityService.isTeamOwner(#teamId) or @teamSecurityService.canManageTeam(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<?> getChannelById(@PathVariable Long teamId, @PathVariable Long id) {
        ChannelDto channel = channelService.getChannelById(id);
        return ResponseEntity.ok(ApiResponse.success("Channel retrieved successfully", channel));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get all channels in a team")
    @PreAuthorize("@teamSecurityService.isTeamMember(#teamId) or @teamSecurityService.isTeamOwner(#teamId) or @teamSecurityService.canManageTeam(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<?> getTeamChannels(@PathVariable Long teamId, Pageable pageable) {
        Page<ChannelDto> channels = channelService.getTeamChannels(teamId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Team channels retrieved successfully", channels));
    }

    @GetMapping("/my-channels")
    @Operation(summary = "Get all channels accessible by the current user")
    public ResponseEntity<?> getMyChannels(Pageable pageable) {
        Long userId = currentUserService.getCurrentUserId();
        Page<ChannelDto> channels = channelService.getUserChannels(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("User channels retrieved successfully", channels));
    }

    @GetMapping("/{teamId}/search")
    @Operation(summary = "Search channels")
    @PreAuthorize("@teamSecurityService.isTeamMember(#teamId) or @teamSecurityService.isTeamOwner(#teamId) or @teamSecurityService.canManageTeam(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    public ResponseEntity<?> searchChannels(@PathVariable Long teamId, @RequestParam String query, Pageable pageable) {
        Page<ChannelDto> channels = channelService.searchChannels(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search completed", channels));
    }

    @PutMapping("/{teamId}/{id}")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Update channel")
    public ResponseEntity<?> updateChannel(@PathVariable Long teamId, @PathVariable Long id, @Valid @RequestBody UpdateChannelRequest request) {
        Long userId = currentUserService.getCurrentUserId();

        ChannelDto channel = channelService.updateChannel(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Channel updated successfully", channel));
    }

    @DeleteMapping("/{teamId}/{id}")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Delete channel")
    public ResponseEntity<?> deleteChannel(@PathVariable Long teamId, @PathVariable Long id) {
        channelService.deleteChannel(id);
        return ResponseEntity.ok(ApiResponse.success("Channel deleted successfully"));
    }

    @PostMapping("/{teamId}/{id}/members/{userId}")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId) or @teamSecurityService.canManageTeam(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Add channel member")
    public ResponseEntity<?> addChannelMember(@PathVariable Long teamId, @PathVariable Long id, @PathVariable Long userId) {
        Long actorId = currentUserService.getCurrentUserId();

        channelService.addChannelMember(id, userId, actorId);
        return ResponseEntity.ok(ApiResponse.success("Channel member added successfully"));
    }

    @DeleteMapping("/{teamId}/{id}/members/{userId}")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId) or @teamSecurityService.canManageTeam(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Remove channel member")
    public ResponseEntity<?> removeChannelMember(@PathVariable Long teamId, @PathVariable Long id, @PathVariable Long userId) {
        Long actorId = currentUserService.getCurrentUserId();

        channelService.removeChannelMember(id, userId, actorId);
        return ResponseEntity.ok(ApiResponse.success("Channel member removed successfully"));
    }

    @PostMapping("/{teamId}/{id}/archive")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Archive channel")
    public ResponseEntity<?> archiveChannel(@PathVariable Long teamId, @PathVariable Long id) {
        channelService.archiveChannel(id);
        return ResponseEntity.ok(ApiResponse.success("Channel archived successfully"));
    }

    @PostMapping("/{teamId}/{id}/unarchive")
    @PreAuthorize("@teamSecurityService.isTeamOwner(#teamId) or hasAuthority('ROLE_ORG_ADMIN') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Unarchive channel")
    public ResponseEntity<?> unarchiveChannel(@PathVariable Long teamId, @PathVariable Long id) {
        channelService.unarchiveChannel(id);
        return ResponseEntity.ok(ApiResponse.success("Channel unarchived successfully"));
    }
}

