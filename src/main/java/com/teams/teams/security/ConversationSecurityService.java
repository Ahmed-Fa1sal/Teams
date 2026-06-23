package com.teams.teams.security;

import com.teams.teams.domain.Channel;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.ChannelRepository;
import com.teams.teams.repository.ConversationRepository;
import com.teams.teams.repository.TeamMemberRepository;
import com.teams.teams.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("conversationSecurity")
@RequiredArgsConstructor
public class ConversationSecurityService {

    private final ConversationRepository conversationRepository;
    private final ChannelRepository channelRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CurrentUserService currentUserService;

    public boolean isConversationMember(Long conversationId) {
        Long userId = currentUserService.getCurrentUserId();
        return conversationRepository.isMember(conversationId, userId);
    }

    /**
     * A user can access a channel's conversation if they are:
     * - a direct channel member, OR
     * - a team member on a public channel
     */
    public boolean canAccessChannelConversation(Long channelId) {
        Long userId = currentUserService.getCurrentUserId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));

        boolean isChannelMember = channel.getMembers().stream()
                .anyMatch(u -> u.getId().equals(userId));
        if (isChannelMember) return true;

        return channel.getIsPublic() &&
               teamMemberRepository.existsByTeamIdAndUserId(channel.getTeam().getId(), userId);
    }
}
