package com.teams.teams.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private UserDto owner;
    private Boolean isPublic;
    private Boolean archived;
    private Set<UserDto> members;
    private Integer memberCount;
    private Integer channelCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

