package com.teams.teams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {

    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private Boolean active;
    private String tier;
    private long memberCount;
    private long teamCount;

    // BaseEntity audit fields
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}