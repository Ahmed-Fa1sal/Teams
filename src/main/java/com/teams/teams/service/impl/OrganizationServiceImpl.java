package com.teams.teams.service.impl;


import com.teams.teams.domain.Organization;
import com.teams.teams.domain.OrganizationMember;
import com.teams.teams.domain.User;
import com.teams.teams.dto.CreateOrganizationRequest;
import com.teams.teams.dto.OrganizationResponse;
import com.teams.teams.dto.PagedResponse;
import com.teams.teams.dto.UpdateOrganizationRequest;
import com.teams.teams.exception.BadRequestException;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.OrganizationMemberRepository;
import com.teams.teams.repository.OrganizationRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request, Long creatorUserId) {
        log.info("Creating organization '{}' for user id={}", request.getName(), creatorUserId);

        if (organizationRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new BadRequestException("Organization with name '" + request.getName() + "' already exists");
        }

        User creator = userRepository.findById(creatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + creatorUserId));

        Organization organization = Organization.builder()
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .tier(request.getTier() != null ? request.getTier() : "FREE")
                .active(true)
                .build();

        organization = organizationRepository.save(organization);

        log.info("Organization created with id={}", organization.getId());
        return toResponse(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization = findOrganizationById(id);
        return toResponse(organization);
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest request) {
        log.info("Updating organization id={}", id);
        Organization organization = findOrganizationById(id);

        if (request.getName() != null && !request.getName().equalsIgnoreCase(organization.getName())) {
            organizationRepository.findByNameIgnoreCase(request.getName()).ifPresent(existing -> {
                throw new BadRequestException("Organization with name '" + request.getName() + "' already exists");
            });
            organization.setName(request.getName());
        }
        if (request.getDescription() != null) organization.setDescription(request.getDescription());
        if (request.getLogoUrl() != null) organization.setLogoUrl(request.getLogoUrl());
        if (request.getTier() != null) organization.setTier(request.getTier());
        if (request.getActive() != null) organization.setActive(request.getActive());

        return toResponse(organizationRepository.save(organization));
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        log.info("Soft-deleting organization id={}", id);
        Organization organization = findOrganizationById(id);
        organization.setDeleted(true);
        organization.setDeletedAt(LocalDateTime.now());
        organizationRepository.save(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrganizationResponse> getAllOrganizations(Pageable pageable) {
        Page<Organization> page = organizationRepository.findAllActiveAndNotDeleted(pageable);
        return PagedResponse.of(page.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrganizationResponse> searchOrganizations(String query, Pageable pageable) {
        Page<Organization> page = organizationRepository.searchByNameOrDescription(query, pageable);
        return PagedResponse.of(page.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrganizationResponse> getUserOrganizations(Long userId, Pageable pageable) {
        Page<OrganizationMember> memberships = organizationMemberRepository.findByUserId(userId, pageable);
        Page<OrganizationResponse> responses = memberships.map(m -> toResponse(m.getOrganization()));
        return PagedResponse.of(responses);
    }

    @Override
    @Transactional
    public OrganizationResponse activateOrganization(Long id) {
        Organization organization = findOrganizationById(id);
        organization.setActive(true);
        return toResponse(organizationRepository.save(organization));
    }

    @Override
    @Transactional
    public OrganizationResponse deactivateOrganization(Long id) {
        Organization organization = findOrganizationById(id);
        organization.setActive(false);
        return toResponse(organizationRepository.save(organization));
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Organization findOrganizationById(Long id) {
        return organizationRepository.findById(id)
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
    }

    private OrganizationResponse toResponse(Organization org) {
        long memberCount = organizationMemberRepository.countMembersByOrganizationId(org.getId());
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .description(org.getDescription())
                .logoUrl(org.getLogoUrl())
                .active(org.getActive())
                .tier(org.getTier())
                .memberCount(memberCount)
                .teamCount(org.getTeams() != null ? org.getTeams().size() : 0)
                .createdAt(org.getCreatedAt())
                .updatedAt(org.getUpdatedAt())
                .createdBy(org.getCreatedBy())
                .build();
    }
}

