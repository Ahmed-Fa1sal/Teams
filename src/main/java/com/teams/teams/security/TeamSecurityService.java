package com.teams.teams.security;

import com.teams.teams.domain.TeamMemberRole;
import com.teams.teams.repository.TeamMemberRepository;
import com.teams.teams.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamSecurityService {

    private final TeamMemberRepository teamMemberRepository;
    private final CurrentUserService currentUserService;

    public boolean isTeamMember(Long teamId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return teamMemberRepository.existsByTeamIdAndUserId(teamId, currentUserId);
    }

    public boolean isTeamOwner(Long teamId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return teamMemberRepository.existsByTeamIdAndUserIdAndRole(
                teamId,
                currentUserId,
                TeamMemberRole.OWNER
        );
    }

    public boolean canManageTeam(Long teamId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return teamMemberRepository.existsByTeamIdAndUserIdAndRoleIn(
                teamId,
                currentUserId,
                Set.of(TeamMemberRole.OWNER, TeamMemberRole.ADMIN)
        );
    }
}
