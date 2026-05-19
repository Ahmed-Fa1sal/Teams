package com.teams.teams.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teams.teams.domain.TeamMemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamMemberDto {
    private Long id;
    private UserDto user;
    private TeamMemberRole role;
    private LocalDateTime joinedAt;
}
