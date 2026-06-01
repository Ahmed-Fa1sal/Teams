package com.teams.teams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateChannelRequest {

    @NotBlank(message = "Channel name is required")
    @Size(min = 1, max = 255, message = "Channel name must be between 1 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @Builder.Default
    private Boolean isPublic = true;
}

