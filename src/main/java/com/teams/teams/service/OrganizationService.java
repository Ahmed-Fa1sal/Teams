package com.teams.teams.service;

import com.teams.teams.dto.CreateOrganizationRequest;
import com.teams.teams.dto.OrganizationResponse;
import com.teams.teams.dto.PagedResponse;
import com.teams.teams.dto.UpdateOrganizationRequest;
import org.springframework.data.domain.Pageable;

public interface OrganizationService {

    OrganizationResponse createOrganization(CreateOrganizationRequest request, Long creatorUserId);

    OrganizationResponse getOrganizationById(Long id);

    OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest request);

    void deleteOrganization(Long id);

    PagedResponse<OrganizationResponse> getAllOrganizations(Pageable pageable);

    PagedResponse<OrganizationResponse> searchOrganizations(String query, Pageable pageable);

    PagedResponse<OrganizationResponse> getUserOrganizations(Long userId, Pageable pageable);

    OrganizationResponse activateOrganization(Long id);

    OrganizationResponse deactivateOrganization(Long id);
}