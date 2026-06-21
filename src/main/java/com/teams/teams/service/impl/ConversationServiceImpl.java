package com.teams.teams.service.impl;

import com.teams.teams.domain.*;
import com.teams.teams.dto.ConversationDto;
import com.teams.teams.dto.MessageDto;
import com.teams.teams.dto.SendChatMessageRequest;
import com.teams.teams.dto.UserDto;
import com.teams.teams.exception.BadRequestException;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.exception.UnauthorizedException;
import com.teams.teams.repository.*;
import com.teams.teams.service.ConversationService;
import com.teams.teams.service.MessageService;
import com.teams.teams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final MessageService messageService;

    @Override
    public ConversationDto createDirectConversation(Long requesterId, Long targetUserId) {
        if (requesterId.equals(targetUserId)) {
            throw new BadRequestException("Cannot create a direct conversation with yourself");
        }

        User requester = loadUser(requesterId);
        User target = loadUser(targetUserId);

        return conversationRepository
                .findDirectConversationBetweenUsers(requesterId, targetUserId)
                .map(this::toConversationDto)
                .orElseGet(() -> {
                    Conversation conversation = Conversation.builder()
                            .name(requester.getUsername() + " & " + target.getUsername())
                            .type(ConversationType.DIRECT)
                            .isGroup(false)
                            .build();
                    Conversation saved = conversationRepository.save(conversation);
                    addMember(saved, requester);
                    addMember(saved, target);
                    return toConversationDto(saved);
                });
    }

    @Override
    public ConversationDto getOrCreateTeamConversation(Long teamId, Long requesterId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, requesterId)) {
            throw new UnauthorizedException("You are not a member of this team");
        }

        return conversationRepository.findByTeamId(teamId)
                .map(this::toConversationDto)
                .orElseGet(() -> {
                    Conversation conversation = Conversation.builder()
                            .name(team.getName())
                            .type(ConversationType.TEAM)
                            .team(team)
                            .isGroup(true)
                            .build();
                    Conversation saved = conversationRepository.save(conversation);
                    teamMemberRepository.findByTeamId(teamId).forEach(tm -> addMember(saved, tm.getUser()));
                    return toConversationDto(saved);
                });
    }

    @Override
    public ConversationDto getOrCreateChannelConversation(Long channelId, Long requesterId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));

        boolean isChannelMember = channel.getMembers().stream()
                .anyMatch(u -> u.getId().equals(requesterId));
        boolean isTeamMember = teamMemberRepository.existsByTeamIdAndUserId(
                channel.getTeam().getId(), requesterId);

        if (!isChannelMember && !(channel.getIsPublic() && isTeamMember)) {
            throw new UnauthorizedException("You do not have access to this channel");
        }

        return conversationRepository.findByChannelId(channelId)
                .map(this::toConversationDto)
                .orElseGet(() -> {
                    Conversation conversation = Conversation.builder()
                            .name(channel.getName())
                            .type(ConversationType.CHANNEL)
                            .channel(channel)
                            .isGroup(true)
                            .build();
                    Conversation saved = conversationRepository.save(conversation);
                    channel.getMembers().forEach(u -> addMember(saved, u));
                    return toConversationDto(saved);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDto getConversationById(Long conversationId, Long userId) {
        Conversation conversation = loadConversation(conversationId);
        requireMember(conversationId, userId);
        return toConversationDto(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDto> getConversationMessages(Long conversationId, Long userId, Pageable pageable) {
        loadConversation(conversationId);
        requireMember(conversationId, userId);
        return messageRepository.findByConversationIdAndDeletedFalse(conversationId, pageable)
                .map(messageService::toMessageDto);
    }

    @Override
    public MessageDto sendMessage(Long conversationId, SendChatMessageRequest request, Long senderId) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BadRequestException("Message content must not be blank");
        }
        Conversation conversation = loadConversation(conversationId);
        requireMember(conversationId, senderId);

        User sender = loadUser(senderId);

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .conversation(conversation)
                .edited(false)
                .build();

        if (request.getReplyToId() != null) {
            Message replyTo = messageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Message not found with id: " + request.getReplyToId()));
            message.setReplyTo(replyTo);
        }

        Message saved = messageRepository.save(message);
        return messageService.toMessageDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDto toConversationDto(Conversation conversation) {
        List<UserDto> memberDtos = conversation.getMembers().stream()
                .map(cm -> userService.toUserDto(cm.getUser()))
                .toList();

        return ConversationDto.builder()
                .id(conversation.getId())
                .name(conversation.getName())
                .imageUrl(conversation.getImageUrl())
                .type(conversation.getType() != null ? conversation.getType().name() : null)
                .teamId(conversation.getTeam() != null ? conversation.getTeam().getId() : null)
                .channelId(conversation.getChannel() != null ? conversation.getChannel().getId() : null)
                .isGroup(conversation.getIsGroup())
                .members(memberDtos)
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    private Conversation loadConversation(Long conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conversation not found with id: " + conversationId));
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private void requireMember(Long conversationId, Long userId) {
        if (!conversationRepository.isMember(conversationId, userId)) {
            throw new UnauthorizedException("You are not a member of this conversation");
        }
    }

    private void addMember(Conversation conversation, User user) {
        if (!conversationMemberRepository.existsByConversationIdAndUserId(conversation.getId(), user.getId())) {
            ConversationMember member = ConversationMember.builder()
                    .conversation(conversation)
                    .user(user)
                    .joinedAt(LocalDateTime.now())
                    .build();
            conversationMemberRepository.save(member);
        }
    }
}
