package com.teams.teams.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDirectConversationRequest {

    @NotNull(message = "Target user ID is required")
    private Long targetUserId;
}
