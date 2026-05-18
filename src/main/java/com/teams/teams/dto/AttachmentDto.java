package com.teams.teams.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class AttachmentDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private Long messageId;
    private UserDto uploadedBy;
    private LocalDateTime createdAt;
}

