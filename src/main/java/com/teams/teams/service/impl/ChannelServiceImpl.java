package com.teams.teams.service.impl;

import com.teams.teams.domain.Channel;
import com.teams.teams.domain.NotificationType;
import com.teams.teams.domain.Team;
import com.teams.teams.domain.User;
import com.teams.teams.dto.ChannelDto;
import com.teams.teams.dto.CreateChannelRequest;
import com.teams.teams.dto.UpdateChannelRequest;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.ChannelRepository;
import com.teams.teams.repository.ConversationRepository;
import com.teams.teams.repository.TeamRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.ChannelService;
import com.teams.teams.service.ConversationService;
import com.teams.teams.service.NotificationService;
import com.teams.teams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;

    @Override
    public ChannelDto createChannel(
            Long teamId,
            CreateChannelRequest request,
            Long ownerId
    ) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Team not found with id: " + teamId
                        ));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + ownerId
                        ));

        Channel channel = Channel.builder()
                .team(team)
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(
                        request.getIsPublic() != null
                                ? request.getIsPublic()
                                : true
                )
                .owner(owner)
                .build();

        channel.addMember(owner);

        Channel savedChannel = channelRepository.save(channel);
        conversationService.createChannelConversation(savedChannel);

        return toChannelDto(savedChannel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto getChannelById(Long id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + id));
        return toChannelDto(channel);
    }

    @Override
    public ChannelDto updateChannel(Long id, UpdateChannelRequest request) {
        return updateChannel(id, request, null);
    }

    @Override
    public ChannelDto updateChannel(Long id, UpdateChannelRequest request, Long actorId) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            channel.setName(request.getName());
        }
        if (request.getDescription() != null) {
            channel.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            channel.setIsPublic(request.getIsPublic());
        }
        if (request.getArchived() != null) {
            channel.setArchived(request.getArchived());
        }

        Channel updatedChannel = channelRepository.save(channel);

        notificationService.notifyChannelMembers(
                updatedChannel.getId(),
                actorId,
                NotificationType.CHANNEL_UPDATED,
                "Channel updated",
                updatedChannel.getName() + " was updated",
                String.valueOf(updatedChannel.getId()),
                "CHANNEL"
        );

        return toChannelDto(updatedChannel);
    }

    @Override
    public void deleteChannel(Long id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + id));
        // Soft delete the channel by setting deleted flag and timestamp
        channel.setDeleted(true);
        channel.setDeletedAt(java.time.LocalDateTime.now());
        channelRepository.save(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChannelDto> getTeamChannels(Long teamId, Pageable pageable) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        return channelRepository.findByTeamIdAndArchivedFalse(teamId, pageable)
                .map(this::toChannelDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChannelDto> getUserChannels(Long userId, Pageable pageable) {
        return channelRepository.findAccessibleByUserId(userId, pageable)
                .map(this::toChannelDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChannelDto> searchChannels(String query, Pageable pageable) {
        return channelRepository.searchByNameOrDescription(query, pageable)
                .map(this::toChannelDto);
    }

    @Override
    public void addChannelMember(Long channelId, Long userId) {
        addChannelMember(channelId, userId, null);
    }

    @Override
    public void addChannelMember(Long channelId, Long userId, Long actorId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        boolean alreadyMember = channel.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));

        channel.addMember(user);
        channelRepository.save(channel);

        if (!alreadyMember) {
            conversationService.addMemberToChannelConversation(channelId, user);
            notificationService.notifyUser(
                    userId,
                    actorId,
                    NotificationType.CHANNEL_ADDED,
                    "Added to channel",
                    "You were added to " + channel.getName(),
                    String.valueOf(channel.getId()),
                    "CHANNEL"
            );
        }
    }

    @Override
    public void removeChannelMember(Long channelId, Long userId) {
        removeChannelMember(channelId, userId, null);
    }

    @Override
    public void removeChannelMember(Long channelId, Long userId, Long actorId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String channelName = channel.getName();
        boolean wasMember = channel.getMembers().stream()
                .anyMatch(member -> member.getId().equals(userId));

        channel.removeMember(user);
        channelRepository.save(channel);

        if (wasMember) {
            conversationService.removeMemberFromChannelConversation(channelId, user);
            notificationService.notifyUser(
                    userId,
                    actorId,
                    NotificationType.CHANNEL_REMOVED,
                    "Removed from channel",
                    "You were removed from " + channelName,
                    String.valueOf(channelId),
                    "CHANNEL"
            );
        }
    }

    @Override
    public void archiveChannel(Long id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + id));
        channel.setArchived(true);
        channelRepository.save(channel);
    }

    @Override
    public void unarchiveChannel(Long id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + id));
        channel.setArchived(false);
        channelRepository.save(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto toChannelDto(Channel channel) {
        if (channel == null) {
            return null;
        }

        Long conversationId = channel.getId() == null ? null :
                conversationRepository.findByChannelId(channel.getId())
                        .map(c -> c.getId())
                        .orElse(null);

        return ChannelDto.builder()
                .id(channel.getId())
                .conversationId(conversationId)
                .teamId(channel.getTeam().getId())
                .teamName(channel.getTeam().getName())
                .name(channel.getName())
                .description(channel.getDescription())
                .owner(userService.toUserDto(channel.getOwner()))
                .isPublic(channel.getIsPublic())
                .archived(channel.getArchived())
                .members(channel.getMembers().stream()
                        .map(userService::toUserDto)
                        .collect(java.util.stream.Collectors.toSet()))
                .memberCount(channel.getMembers().size())
                .messageCount(channel.getMessages().size())
                .createdAt(channel.getCreatedAt())
                .updatedAt(channel.getUpdatedAt())
                .build();
    }
}



