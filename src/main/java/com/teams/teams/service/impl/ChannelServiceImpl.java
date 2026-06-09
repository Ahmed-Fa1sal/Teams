package com.teams.teams.service.impl;

import com.teams.teams.domain.Channel;
import com.teams.teams.domain.Team;
import com.teams.teams.domain.User;
import com.teams.teams.dto.ChannelDto;
import com.teams.teams.dto.CreateChannelRequest;
import com.teams.teams.dto.UpdateChannelRequest;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.ChannelRepository;
import com.teams.teams.repository.TeamRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.ChannelService;
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
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        channel.addMember(user);
        channelRepository.save(channel);
    }

    @Override
    public void removeChannelMember(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        channel.removeMember(user);
        channelRepository.save(channel);
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

        return ChannelDto.builder()
                .id(channel.getId())
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



