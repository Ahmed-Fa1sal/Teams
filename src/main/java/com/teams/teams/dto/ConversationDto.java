package com.teams.teams.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String type;
    private Long teamId;
    private Long channelId;
    private Boolean isGroup;
    private List<UserDto> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
