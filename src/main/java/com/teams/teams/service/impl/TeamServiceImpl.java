package com.teams.teams.service.impl;

import com.teams.teams.domain.Team;
import com.teams.teams.domain.User;
import com.teams.teams.dto.TeamDto;
import com.teams.teams.dto.CreateTeamRequest;
import com.teams.teams.dto.UpdateTeamRequest;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.TeamRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.TeamService;
import com.teams.teams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public TeamDto createTeam(CreateTeamRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + ownerId));

        Team team = Team.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .owner(owner)
                .build();

        team.addMember(owner);
        Team savedTeam = teamRepository.save(team);
        return toTeamDto(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDto getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        return toTeamDto(team);
    }

    @Override
    public TeamDto updateTeam(Long id, UpdateTeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            team.setName(request.getName());
        }
        if (request.getDescription() != null) {
            team.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            team.setImageUrl(request.getImageUrl());
        }
        if (request.getIsPublic() != null) {
            team.setIsPublic(request.getIsPublic());
        }
        if (request.getArchived() != null) {
            team.setArchived(request.getArchived());
        }

        Team updatedTeam = teamRepository.save(team);
        return toTeamDto(updatedTeam);
    }

    @Override
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        teamRepository.delete(team);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamDto> getAllTeams(Pageable pageable) {
        return teamRepository.findAll(pageable)
                .map(this::toTeamDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamDto> getUserTeams(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return teamRepository.findByMembersContainingOrOwnerId(user, userId, pageable)
                .map(this::toTeamDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamDto> searchTeams(String query, Pageable pageable) {
        return teamRepository.searchByNameOrDescription(query, pageable)
                .map(this::toTeamDto);
    }

    @Override
    public void addTeamMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        team.addMember(user);
        teamRepository.save(team);
    }

    @Override
    public void removeTeamMember(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        team.removeMember(user);
        teamRepository.save(team);
    }

    @Override
    public void archiveTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        team.setArchived(true);
        teamRepository.save(team);
    }

    @Override
    public void unarchiveTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        team.setArchived(false);
        teamRepository.save(team);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDto toTeamDto(Team team) {
        if (team == null) {
            return null;
        }

        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .imageUrl(team.getImageUrl())
                .owner(userService.toUserDto(team.getOwner()))
                .isPublic(team.getIsPublic())
                .archived(team.getArchived())
                .members(team.getMembers().stream()
                        .map(userService::toUserDto)
                        .collect(java.util.stream.Collectors.toSet()))
                .memberCount(team.getMembers().size())
                .channelCount(team.getChannels().size())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }
}




