package com.teams.teams.controller;

import com.teams.teams.dto.ApiResponse;
import com.teams.teams.dto.ChannelDto;
import com.teams.teams.dto.CreateChannelRequest;
import com.teams.teams.dto.UpdateChannelRequest;
import com.teams.teams.service.ChannelService;
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
@RequestMapping("/channels")
@Tag(name = "Channels", description = "Channel management and channel operations")
@SecurityRequirement(name = "Bearer Authentication")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @PostMapping
    @Operation(summary = "Create a new channel")
    public ResponseEntity<?> createChannel(@Valid @RequestBody CreateChannelRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        ChannelDto channel = channelService.createChannel(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(channel));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get channel by ID")
    public ResponseEntity<?> getChannelById(@PathVariable Long id) {
        ChannelDto channel = channelService.getChannelById(id);
        return ResponseEntity.ok(ApiResponse.success("Channel retrieved successfully", channel));
    }

    @GetMapping("/team/{teamId}")
    @Operation(summary = "Get all channels in a team")
    public ResponseEntity<?> getTeamChannels(@PathVariable Long teamId, Pageable pageable) {
        Page<ChannelDto> channels = channelService.getTeamChannels(teamId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Team channels retrieved successfully", channels));
    }

    @GetMapping("/search")
    @Operation(summary = "Search channels")
    public ResponseEntity<?> searchChannels(@RequestParam String query, Pageable pageable) {
        Page<ChannelDto> channels = channelService.searchChannels(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search completed", channels));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Update channel")
    public ResponseEntity<?> updateChannel(@PathVariable Long id, @Valid @RequestBody UpdateChannelRequest request) {
        ChannelDto channel = channelService.updateChannel(id, request);
        return ResponseEntity.ok(ApiResponse.success("Channel updated successfully", channel));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Delete channel")
    public ResponseEntity<?> deleteChannel(@PathVariable Long id) {
        channelService.deleteChannel(id);
        return ResponseEntity.ok(ApiResponse.success("Channel deleted successfully"));
    }

    @PostMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Add channel member")
    public ResponseEntity<?> addChannelMember(@PathVariable Long id, @PathVariable Long userId) {
        channelService.addChannelMember(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Channel member added successfully"));
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Remove channel member")
    public ResponseEntity<?> removeChannelMember(@PathVariable Long id, @PathVariable Long userId) {
        channelService.removeChannelMember(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Channel member removed successfully"));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Archive channel")
    public ResponseEntity<?> archiveChannel(@PathVariable Long id) {
        channelService.archiveChannel(id);
        return ResponseEntity.ok(ApiResponse.success("Channel archived successfully"));
    }

    @PostMapping("/{id}/unarchive")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Unarchive channel")
    public ResponseEntity<?> unarchiveChannel(@PathVariable Long id) {
        channelService.unarchiveChannel(id);
        return ResponseEntity.ok(ApiResponse.success("Channel unarchived successfully"));
    }
}

