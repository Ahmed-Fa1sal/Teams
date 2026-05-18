package com.teams.teams.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTeamRequest {

    @Size(min = 1, max = 255, message = "Team name must be between 1 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    private String imageUrl;

    private Boolean isPublic;

    private Boolean archived;
}

