package com.teams.teams.service;

import com.teams.teams.domain.Channel;
import com.teams.teams.dto.ChannelDto;
import com.teams.teams.dto.CreateChannelRequest;
import com.teams.teams.dto.UpdateChannelRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChannelService {
    ChannelDto createChannel(Long teamId,CreateChannelRequest request, Long ownerId);
    ChannelDto getChannelById(Long id);
    ChannelDto updateChannel(Long id, UpdateChannelRequest request);
    ChannelDto updateChannel(Long id, UpdateChannelRequest request, Long actorId);
    void deleteChannel(Long id);
    Page<ChannelDto> getTeamChannels(Long teamId, Pageable pageable);
    Page<ChannelDto> getUserChannels(Long userId, Pageable pageable);
    Page<ChannelDto> searchChannels(String query, Pageable pageable);
    void addChannelMember(Long channelId, Long userId);
    void addChannelMember(Long channelId, Long userId, Long actorId);
    void removeChannelMember(Long channelId, Long userId);
    void removeChannelMember(Long channelId, Long userId, Long actorId);
    void archiveChannel(Long id);
    void unarchiveChannel(Long id);
    ChannelDto toChannelDto(Channel channel);
}

