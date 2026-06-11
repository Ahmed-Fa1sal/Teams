package com.teams.teams.service;

import com.teams.teams.domain.Team;
import com.teams.teams.dto.CreateTeamRequest;
import com.teams.teams.dto.TeamDto;
import com.teams.teams.dto.UpdateTeamRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamService {
    TeamDto createTeam(CreateTeamRequest request, Long ownerId);
    TeamDto getTeamById(Long id);
    TeamDto updateTeam(Long id, UpdateTeamRequest request);
    TeamDto updateTeam(Long id, UpdateTeamRequest request, Long actorId);
    void deleteTeam(Long id);
    Page<TeamDto> getAllTeams(Pageable pageable);
    Page<TeamDto> getUserTeams(Long userId, Pageable pageable);
    Page<TeamDto> searchTeams(String query, Pageable pageable);
    void addTeamMember(Long teamId, Long userId);
    void addTeamMember(Long teamId, Long userId, Long actorId);
    void removeTeamMember(Long teamId, Long userId);
    void removeTeamMember(Long teamId, Long userId, Long actorId);
    void archiveTeam(Long id);
    void unarchiveTeam(Long id);
    TeamDto toTeamDto(Team team);
}

