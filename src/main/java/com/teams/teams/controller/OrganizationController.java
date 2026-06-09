package com.teams.teams.controller;

import com.teams.teams.domain.OrganizationMember;
import com.teams.teams.dto.*;
import com.teams.teams.service.CurrentUserService;
import com.teams.teams.service.OrganizationMemberService;
import com.teams.teams.service.OrganizationService;
import com.teams.teams.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizations")
@Tag(name = "Organizations", description = "Organization management and membership operations")
@SecurityRequirement(name = "Bearer Authentication")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMemberService memberService;
    private final CurrentUserService currentUserService;

    public OrganizationController(
            OrganizationService organizationService,
            OrganizationMemberService memberService,
            CurrentUserService currentUserService
    ) {
        this.organizationService = organizationService;
        this.memberService = memberService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Create a new organization")
    public ResponseEntity<?> createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        Long userId = currentUserService.getCurrentUserId();

        OrganizationResponse organization =
                organizationService.createOrganization(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(organization));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get all organizations with pagination")
    public ResponseEntity<?> getAllOrganizations(Pageable pageable) {
        PagedResponse<OrganizationResponse> organizations =
                organizationService.getAllOrganizations(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Organizations retrieved successfully", organizations)
        );
    }

    @GetMapping("/search")
    @Operation(summary = "Search organizations")
    public ResponseEntity<?> searchOrganizations(@RequestParam String query, Pageable pageable) {
        PagedResponse<OrganizationResponse> organizations =
                organizationService.searchOrganizations(query, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Search completed", organizations)
        );
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user's organizations")
    public ResponseEntity<?> getMyOrganizations(Pageable pageable) {
        Long userId = currentUserService.getCurrentUserId();

        PagedResponse<OrganizationResponse> organizations =
                organizationService.getUserOrganizations(userId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("User organizations retrieved successfully", organizations)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@orgSecurity.isMember(#id) or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get organization by ID")
    public ResponseEntity<?> getOrganization(@PathVariable Long id) {
        OrganizationResponse organization = organizationService.getOrganizationById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Organization retrieved successfully", organization)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("@orgSecurity.isOrgAdmin(#id) or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Update organization")
    public ResponseEntity<?> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        OrganizationResponse organization =
                organizationService.updateOrganization(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Organization updated successfully", organization)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@orgSecurity.isOrgAdmin(#id) or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Delete organization")
    public ResponseEntity<?> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);

        return ResponseEntity.ok(
                ApiResponse.success("Organization deleted successfully")
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or @orgSecurity.isOrgAdmin(#id)")
    @Operation(summary = "Activate organization")
    public ResponseEntity<?> activateOrganization(@PathVariable Long id) {
        OrganizationResponse organization =
                organizationService.activateOrganization(id);

        return ResponseEntity.ok(
                ApiResponse.success("Organization activated successfully", organization)
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN') or @orgSecurity.isOrgAdmin(#id)")
    @Operation(summary = "Deactivate organization")
    public ResponseEntity<?> deactivateOrganization(@PathVariable Long id) {
        OrganizationResponse organization =
                organizationService.deactivateOrganization(id);

        return ResponseEntity.ok(
                ApiResponse.success("Organization deactivated successfully", organization)
        );
    }

    @PostMapping("/{organizationId}/members")
    @PreAuthorize("@orgSecurity.isOrgAdmin(#organizationId) or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Add organization member")
    public ResponseEntity<?> addMember(
            @PathVariable Long organizationId,
            @Valid @RequestBody AddOrganizationMemberRequest request
    ) {
        OrganizationMemberResponse member =
                memberService.addMember(organizationId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(member));
    }

    @GetMapping("/{organizationId}/members")
    @PreAuthorize("@orgSecurity.isMember(#organizationId) or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get organization members")
    public ResponseEntity<?> getMembers(
            @PathVariable Long organizationId,
            @RequestParam(required = false) OrganizationMember.OrganizationMemberRole role,
            Pageable pageable
    ) {
        PagedResponse<OrganizationMemberResponse> members =
                role != null
                        ? memberService.getMembersByRole(organizationId, role, pageable)
                        : memberService.getMembers(organizationId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Organization members retrieved successfully", members)
        );
    }

    @GetMapping("/{organizationId}/members/{userId}")
    @PreAuthorize("@orgSecurity.isMember(#organizationId) or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Get organization member")
    public ResponseEntity<?> getMember(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        OrganizationMemberResponse member =
                memberService.getMember(organizationId, userId);

        return ResponseEntity.ok(
                ApiResponse.success("Organization member retrieved successfully", member)
        );
    }

    @PatchMapping("/{organizationId}/members/{userId}/role")
    @PreAuthorize("@orgSecurity.isOrgAdmin(#organizationId) or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Update organization member role")
    public ResponseEntity<?> updateMemberRole(
            @PathVariable Long organizationId,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateMemberRoleRequest request
    ) {
        OrganizationMemberResponse member =
                memberService.updateMemberRole(organizationId, userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Organization member role updated successfully", member)
        );
    }

    @DeleteMapping("/{organizationId}/members/{userId}")
    @PreAuthorize("@orgSecurity.isOrgAdminOrSelf(#organizationId, #userId) or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @Operation(summary = "Remove organization member")
    public ResponseEntity<?> removeMember(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        memberService.removeMember(organizationId, userId);

        return ResponseEntity.ok(
                ApiResponse.success("Organization member removed successfully")
        );
    }

    @DeleteMapping("/{organizationId}/members/me/leave")
    @PreAuthorize("@orgSecurity.isMember(#organizationId)")
    @Operation(summary = "Leave organization")
    public ResponseEntity<?> leaveOrganization(@PathVariable Long organizationId) {
        Long userId = currentUserService.getCurrentUserId();

        memberService.removeMember(organizationId, userId);

        return ResponseEntity.ok(
                ApiResponse.success("Left organization successfully")
        );
    }
}