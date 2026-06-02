package com.teams.teams.service.impl;


import com.teams.teams.domain.Organization;
import com.teams.teams.domain.OrganizationMember;
import com.teams.teams.domain.User;
import com.teams.teams.dto.AddOrganizationMemberRequest;
import com.teams.teams.dto.OrganizationMemberResponse;
import com.teams.teams.dto.PagedResponse;
import com.teams.teams.dto.UpdateMemberRoleRequest;
import com.teams.teams.exception.BadRequestException;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.OrganizationMemberRepository;
import com.teams.teams.repository.OrganizationRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.OrganizationMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationMemberServiceImpl implements OrganizationMemberService {

    private final OrganizationMemberRepository memberRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrganizationMemberResponse addMember(Long organizationId, AddOrganizationMemberRequest request) {
        log.info("Adding user id={} to organization id={}", request.getUserId(), organizationId);

        Organization organization = findOrganizationById(organizationId);
        User user = findUserById(request.getUserId());

        // Check for an existing record — active or previously soft-deleted
        OrganizationMember member = memberRepository
                .findByOrganizationIdAndUserId(organizationId, request.getUserId())
                .map(existing -> {
                    if (!existing.isDeleted()) {
                        throw new BadRequestException("User is already a member of this organization");
                    }
                    // Re-activate a previously removed member instead of creating a duplicate row
                    existing.setDeleted(false);
                    existing.setDeletedAt(null);
                    existing.setRole(request.getRole());
                    return existing;
                })
                .orElseGet(() -> OrganizationMember.builder()
                        .organization(organization)
                        .user(user)
                        .role(request.getRole())
                        .build());

        member = memberRepository.save(member);
        organization.addMember(member);

        log.info("User id={} added to organization id={} with role={}", user.getId(), organizationId, request.getRole());
        return toResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationMemberResponse getMember(Long organizationId, Long userId) {
        OrganizationMember member = findMember(organizationId, userId);
        return toResponse(member);
    }

    @Override
    @Transactional
    public OrganizationMemberResponse updateMemberRole(Long organizationId, Long userId, UpdateMemberRoleRequest request) {
        log.info("Updating role for user id={} in organization id={} to {}", userId, organizationId, request.getRole());

        OrganizationMember member = findMember(organizationId, userId);

        // Prevent removing the last ORG_ADMIN
        if (member.getRole() == OrganizationMember.OrganizationMemberRole.ORG_ADMIN
                && request.getRole() != OrganizationMember.OrganizationMemberRole.ORG_ADMIN) {
            long adminCount = memberRepository
                    .findAdminsByOrganizationAndRole(organizationId, OrganizationMember.OrganizationMemberRole.ORG_ADMIN)
                    .stream().filter(m -> !m.isDeleted()).count();
            if (adminCount <= 1) {
                throw new BadRequestException("Cannot change role: organization must have at least one ORG_ADMIN");
            }
        }

        member.setRole(request.getRole());
        return toResponse(memberRepository.save(member));
    }

    @Override
    @Transactional
    public void removeMember(Long organizationId, Long userId) {
        log.info("Removing user id={} from organization id={}", userId, organizationId);

        OrganizationMember member = findMember(organizationId, userId);

        // Prevent removing the last ORG_ADMIN
        if (member.getRole() == OrganizationMember.OrganizationMemberRole.ORG_ADMIN) {
            List<OrganizationMember> admins = memberRepository
                    .findAdminsByOrganizationAndRole(organizationId, OrganizationMember.OrganizationMemberRole.ORG_ADMIN);
            if (admins.size() <= 1) {
                throw new BadRequestException("Cannot remove the last ORG_ADMIN from the organization");
            }
        }

        Organization organization = member.getOrganization();
        organization.removeMember(member);
        member.setDeleted(true);
        member.setDeletedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrganizationMemberResponse> getMembers(Long organizationId, Pageable pageable) {
        ensureOrganizationExists(organizationId);
        Page<OrganizationMember> page = memberRepository.findByOrganizationId(organizationId, pageable);
        return PagedResponse.of(page.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrganizationMemberResponse> getMembersByRole(Long organizationId,
                                                                      OrganizationMember.OrganizationMemberRole role,
                                                                      Pageable pageable) {
        ensureOrganizationExists(organizationId);
        Page<OrganizationMember> page = memberRepository.findByOrganizationIdAndRole(organizationId, role, pageable);
        return PagedResponse.of(page.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMember(Long organizationId, Long userId) {
        return memberRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .map(m -> !m.isDeleted())
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long organizationId, Long userId, OrganizationMember.OrganizationMemberRole role) {
        return memberRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .map(m -> !m.isDeleted() && m.getRole() == role)
                .orElse(false);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private OrganizationMember findMember(Long organizationId, Long userId) {
        return memberRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .filter(m -> !m.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found in organization id=" + organizationId + " for user id=" + userId));
    }

    private Organization findOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private void ensureOrganizationExists(Long organizationId) {
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("Organization not found with id: " + organizationId);
        }
    }

    private OrganizationMemberResponse toResponse(OrganizationMember member) {
        User user = member.getUser();
        Organization org = member.getOrganization();
        return OrganizationMemberResponse.builder()
                .id(member.getId())
                .organizationId(org.getId())
                .organizationName(org.getName())
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .createdBy(member.getCreatedBy())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}