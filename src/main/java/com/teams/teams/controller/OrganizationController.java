package com.teams.teams.controller;

import com.teams.teams.domain.User;
import com.teams.teams.dto.CreateOrganizationRequest;
import com.teams.teams.dto.OrganizationResponse;
import com.teams.teams.dto.PagedResponse;
import com.teams.teams.dto.UpdateOrganizationRequest;
import com.teams.teams.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    // ── POST /api/v1/organizations ──────────────────────────────────────────
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request,
            @AuthenticationPrincipal User currentUser) {

        OrganizationResponse response = organizationService.createOrganization(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/v1/organizations/{id} ──────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrganizationById(id));
    }

    // ── GET /api/v1/organizations ───────────────────────────────────────────
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<OrganizationResponse>> getAllOrganizations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return ResponseEntity.ok(
                organizationService.getAllOrganizations(PageRequest.of(page, size, sort)));
    }

    // ── GET /api/v1/organizations/search ────────────────────────────────────
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<OrganizationResponse>> searchOrganizations(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                organizationService.searchOrganizations(query, PageRequest.of(page, size)));
    }

    // ── GET /api/v1/organizations/my ────────────────────────────────────────
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponse<OrganizationResponse>> getMyOrganizations(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                organizationService.getUserOrganizations(currentUser.getId(), PageRequest.of(page, size)));
    }

    // ── PUT /api/v1/organizations/{id} ──────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("@orgSecurity.isOrgAdmin(#id)")
    public ResponseEntity<OrganizationResponse> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrganizationRequest request) {

        return ResponseEntity.ok(organizationService.updateOrganization(id, request));
    }

    // ── DELETE /api/v1/organizations/{id} ───────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("@orgSecurity.isOrgAdmin(#id)")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }

    // ── PATCH /api/v1/organizations/{id}/activate ───────────────────────────
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @orgSecurity.isOrgAdmin(#id)")
    public ResponseEntity<OrganizationResponse> activateOrganization(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.activateOrganization(id));
    }

    // ── PATCH /api/v1/organizations/{id}/deactivate ─────────────────────────
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @orgSecurity.isOrgAdmin(#id)")
    public ResponseEntity<OrganizationResponse> deactivateOrganization(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.deactivateOrganization(id));
    }
}
