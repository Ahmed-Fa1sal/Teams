package com.teams.teams.service.impl;

import com.teams.teams.domain.Team;
import com.teams.teams.domain.TeamMember;
import com.teams.teams.domain.TeamMemberRole;
import com.teams.teams.domain.User;
import com.teams.teams.dto.CreateTeamRequest;
import com.teams.teams.dto.TeamDto;
import com.teams.teams.dto.TeamMemberDto;
import com.teams.teams.dto.UpdateTeamRequest;
import com.teams.teams.exception.BadRequestException;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.TeamMemberRepository;
import com.teams.teams.repository.TeamRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.TeamService;
import com.teams.teams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
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

        team.addMember(owner, TeamMemberRole.OWNER);

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
        return teamRepository.findByMemberUserId(userId, pageable)
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

        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new BadRequestException("User is already a member of this team");
        }

        TeamMember member = TeamMember.builder()
                .team(team)
                .user(user)
                .role(TeamMemberRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();

        teamMemberRepository.save(member);
    }

    @Override
    public void removeTeamMember(Long teamId, Long userId) {
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Team member not found"));

        if (member.getRole() == TeamMemberRole.OWNER) {
            throw new BadRequestException("Team owner cannot be removed from the team");
        }

        teamMemberRepository.delete(member);
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

        List<TeamMember> memberships = team.getId() == null
                ? team.getMembers()
                : teamMemberRepository.findByTeamId(team.getId());

        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .imageUrl(team.getImageUrl())
                .owner(userService.toUserDto(team.getOwner()))
                .isPublic(team.getIsPublic())
                .archived(team.getArchived())
                .members(memberships.stream()
                        .map(TeamMember::getUser)
                        .map(userService::toUserDto)
                        .collect(Collectors.toSet()))
                .teamMembers(memberships.stream()
                        .map(this::toTeamMemberDto)
                        .collect(Collectors.toSet()))
                .memberCount(memberships.size())
                .channelCount(team.getChannels().size())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    private TeamMemberDto toTeamMemberDto(TeamMember teamMember) {
        return TeamMemberDto.builder()
                .id(teamMember.getId())
                .user(userService.toUserDto(teamMember.getUser()))
                .role(teamMember.getRole())
                .joinedAt(teamMember.getJoinedAt())
                .build();
    }
}
