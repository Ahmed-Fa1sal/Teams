package com.teams.teams.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.teams.teams.domain.NotificationType;
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
public class NotificationDto {
    private Long id;
    private UserDto recipient;
    private NotificationType type;
    private String title;
    private String message;
    private String relatedEntityId;
    private String relatedEntityType;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}


