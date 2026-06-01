package com.teams.teams.security;

import com.teams.teams.domain.OrganizationMember.OrganizationMemberRole;
import com.teams.teams.repository.OrganizationMemberRepository;
import com.teams.teams.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("orgSecurity")
@RequiredArgsConstructor
public class OrganizationSecurityService {

    private final OrganizationMemberRepository organizationMemberRepository;
    private final CurrentUserService currentUserService;

    public boolean isMember(Long organizationId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return organizationMemberRepository.existsByOrganizationIdAndUserId(
                organizationId,
                currentUserId
        );
    }

    public boolean isOrgAdmin(Long organizationId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return organizationMemberRepository.existsByOrganizationIdAndUserIdAndRole(
                organizationId,
                currentUserId,
                OrganizationMemberRole.ORG_ADMIN
        );
    }

    public boolean isTeamAdmin(Long organizationId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return organizationMemberRepository.existsByOrganizationIdAndUserIdAndRole(
                organizationId,
                currentUserId,
                OrganizationMemberRole.TEAM_ADMIN
        );
    }

    public boolean isOrgAdminOrTeamAdmin(Long organizationId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return organizationMemberRepository.existsByOrganizationIdAndUserIdAndRoleIn(
                organizationId,
                currentUserId,
                Set.of(
                        OrganizationMemberRole.ORG_ADMIN,
                        OrganizationMemberRole.TEAM_ADMIN
                )
        );
    }

    public boolean isOrgMemberOrHigher(Long organizationId) {
        return isMember(organizationId);
    }

    public boolean isSelf(Long userId) {
        Long currentUserId = currentUserService.getCurrentUserId();

        return currentUserId.equals(userId);
    }

    public boolean isOrgAdminOrSelf(Long organizationId, Long userId) {
        return isOrgAdmin(organizationId) || isSelf(userId);
    }
}