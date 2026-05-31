package com.teams.teams.dto;

import com.teams.teams.domain.OrganizationMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMemberResponse {

    private Long id;
    private Long organizationId;
    private String organizationName;
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String profileImageUrl;
    private OrganizationMember.OrganizationMemberRole role;
    private LocalDateTime joinedAt;

    // BaseEntity audit fields
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
