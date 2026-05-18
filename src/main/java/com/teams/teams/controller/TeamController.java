package com.teams.teams.controller;

import com.teams.teams.dto.ApiResponse;
import com.teams.teams.dto.TeamDto;
import com.teams.teams.dto.CreateTeamRequest;
import com.teams.teams.dto.UpdateTeamRequest;
import com.teams.teams.service.TeamService;
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
@RequestMapping("/teams")
@Tag(name = "Teams", description = "Team management and workspace operations")
@SecurityRequirement(name = "Bearer Authentication")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    @Operation(summary = "Create a new team")
    public ResponseEntity<?> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        TeamDto team = teamService.createTeam(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(team));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<?> getTeamById(@PathVariable Long id) {
        TeamDto team = teamService.getTeamById(id);
        return ResponseEntity.ok(ApiResponse.success("Team retrieved successfully", team));
    }

    @GetMapping
    @Operation(summary = "Get all teams with pagination")
    public ResponseEntity<?> getAllTeams(Pageable pageable) {
        Page<TeamDto> teams = teamService.getAllTeams(pageable);
        return ResponseEntity.ok(ApiResponse.success("Teams retrieved successfully", teams));
    }

    @GetMapping("/my-teams")
    @Operation(summary = "Get current user's teams")
    public ResponseEntity<?> getUserTeams(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        Page<TeamDto> teams = teamService.getUserTeams(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("User teams retrieved successfully", teams));
    }

    @GetMapping("/search")
    @Operation(summary = "Search teams")
    public ResponseEntity<?> searchTeams(@RequestParam String query, Pageable pageable) {
        Page<TeamDto> teams = teamService.searchTeams(query, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search completed", teams));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Update team")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @Valid @RequestBody UpdateTeamRequest request) {
        TeamDto team = teamService.updateTeam(id, request);
        return ResponseEntity.ok(ApiResponse.success("Team updated successfully", team));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Delete team")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok(ApiResponse.success("Team deleted successfully"));
    }

    @PostMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Add team member")
    public ResponseEntity<?> addTeamMember(@PathVariable Long id, @PathVariable Long userId) {
        teamService.addTeamMember(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Team member added successfully"));
    }

    @DeleteMapping("/{id}/members/{userId}")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_ORG_ADMIN')")
    @Operation(summary = "Remove team member")
    public ResponseEntity<?> removeTeamMember(@PathVariable Long id, @PathVariable Long userId) {
        teamService.removeTeamMember(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Team member removed successfully"));
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Archive team")
    public ResponseEntity<?> archiveTeam(@PathVariable Long id) {
        teamService.archiveTeam(id);
        return ResponseEntity.ok(ApiResponse.success("Team archived successfully"));
    }

    @PostMapping("/{id}/unarchive")
    @PreAuthorize("hasRole('ROLE_TEAM_OWNER') or hasRole('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Unarchive team")
    public ResponseEntity<?> unarchiveTeam(@PathVariable Long id) {
        teamService.unarchiveTeam(id);
        return ResponseEntity.ok(ApiResponse.success("Team unarchived successfully"));
    }
}

