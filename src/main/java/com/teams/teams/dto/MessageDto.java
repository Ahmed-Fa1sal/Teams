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
public class MessageDto {
    private Long id;
    private String content;
    private UserDto sender;
    private Long channelId;
    private Long conversationId;
    private Long replyToId;
    private String replyToContent;
    private Boolean edited;
    private Boolean deleted;
    private Integer reactionCount;
    private Integer replyCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

