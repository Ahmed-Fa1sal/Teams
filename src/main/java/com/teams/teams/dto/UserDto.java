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
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String profileImageUrl;
    private String bio;
    private Boolean active;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lastActivityAt;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

