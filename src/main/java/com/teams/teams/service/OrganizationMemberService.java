package com.teams.teams.service;

import com.teams.teams.domain.OrganizationMember;
import com.teams.teams.dto.AddOrganizationMemberRequest;
import com.teams.teams.dto.OrganizationMemberResponse;
import com.teams.teams.dto.PagedResponse;
import com.teams.teams.dto.UpdateMemberRoleRequest;
import org.springframework.data.domain.Pageable;


public interface OrganizationMemberService {

    OrganizationMemberResponse addMember(Long organizationId, AddOrganizationMemberRequest request);

    OrganizationMemberResponse getMember(Long organizationId, Long userId);

    OrganizationMemberResponse updateMemberRole(Long organizationId, Long userId, UpdateMemberRoleRequest request);

    void removeMember(Long organizationId, Long userId);

    PagedResponse<OrganizationMemberResponse> getMembers(Long organizationId, Pageable pageable);

    PagedResponse<OrganizationMemberResponse> getMembersByRole(Long organizationId,
                                                               OrganizationMember.OrganizationMemberRole role,
                                                               Pageable pageable);

    boolean isMember(Long organizationId, Long userId);

    boolean hasRole(Long organizationId, Long userId, OrganizationMember.OrganizationMemberRole role);
}