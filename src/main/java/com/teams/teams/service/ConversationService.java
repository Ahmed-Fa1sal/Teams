package com.teams.teams.service;

import com.teams.teams.domain.Conversation;
import com.teams.teams.dto.ConversationDto;
import com.teams.teams.dto.MessageDto;
import com.teams.teams.dto.SendChatMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConversationService {

    ConversationDto createDirectConversation(Long requesterId, Long targetUserId);

    ConversationDto getOrCreateTeamConversation(Long teamId, Long requesterId);

    ConversationDto getOrCreateChannelConversation(Long channelId, Long requesterId);

    ConversationDto getConversationById(Long conversationId, Long userId);

    Page<MessageDto> getConversationMessages(Long conversationId, Long userId, Pageable pageable);

    MessageDto sendMessage(Long conversationId, SendChatMessageRequest request, Long senderId);

    ConversationDto toConversationDto(Conversation conversation);
}
