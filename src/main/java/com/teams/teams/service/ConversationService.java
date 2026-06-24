package com.teams.teams.service;

import com.teams.teams.domain.Channel;
import com.teams.teams.domain.Conversation;
import com.teams.teams.domain.Team;
import com.teams.teams.domain.User;
import com.teams.teams.dto.ConversationDto;
import com.teams.teams.dto.MessageDto;
import com.teams.teams.dto.SendChatMessageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConversationService {

    // DIRECT
    ConversationDto createDirectConversation(Long requesterId, Long targetUserId);

    // Called internally by TeamService / ChannelService on entity creation
    ConversationDto createTeamConversation(Team team);
    ConversationDto createChannelConversation(Channel channel);

    // Member sync — called by TeamService / ChannelService on add/remove
    void addMemberToTeamConversation(Long teamId, User user);
    void removeMemberFromTeamConversation(Long teamId, User user);
    void addMemberToChannelConversation(Long channelId, User user);
    void removeMemberFromChannelConversation(Long channelId, User user);

    // READ — auth enforced by @PreAuthorize at controller level
    Page<ConversationDto> getMyConversations(Long userId, Pageable pageable);
    ConversationDto getTeamConversation(Long teamId);
    ConversationDto getChannelConversation(Long channelId);
    Page<MessageDto> getConversationMessages(Long conversationId, Pageable pageable);

    // SEND — service-level membership check for WebSocket path
    MessageDto sendMessage(Long conversationId, SendChatMessageRequest request, Long senderId);

    ConversationDto toConversationDto(Conversation conversation);
}
