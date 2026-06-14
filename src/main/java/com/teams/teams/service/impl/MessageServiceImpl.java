package com.teams.teams.service.impl;

import com.teams.teams.domain.*;
import com.teams.teams.dto.CreateMessageRequest;
import com.teams.teams.dto.MessageDto;
import com.teams.teams.dto.UpdateMessageRequest;
import com.teams.teams.exception.BadRequestException;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.exception.UnauthorizedException;
import com.teams.teams.repository.ChannelRepository;
import com.teams.teams.repository.ConversationRepository;
import com.teams.teams.repository.MessageRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.MessageService;
import com.teams.teams.service.NotificationService;
import com.teams.teams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public MessageDto createMessage(CreateMessageRequest request, Long senderId) {
        if (request.getChannelId() == null && request.getConversationId() == null) {
            throw new BadRequestException("Either channelId or conversationId must be provided");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + senderId));

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .edited(false)
                .build();

        if (request.getChannelId() != null) {
            Channel channel = channelRepository.findById(request.getChannelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + request.getChannelId()));
            message.setChannel(channel);
        }

        if (request.getConversationId() != null) {
            Conversation conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + request.getConversationId()));
            message.setConversation(conversation);
        }

        Message replyTo = null;
        if (request.getReplyToId() != null) {
            replyTo = messageRepository.findById(request.getReplyToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + request.getReplyToId()));
            message.setReplyTo(replyTo);
        }

        Message savedMessage = messageRepository.save(message);

        Set<Long> mentionedUserIds = request.getMentionedUserIds() == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(request.getMentionedUserIds());

        Set<Long> excludedFromMessageNotifications = new LinkedHashSet<>(mentionedUserIds);
        Long replyRecipientId = replyTo != null ? replyTo.getSender().getId() : null;
        if (replyRecipientId != null && !replyRecipientId.equals(senderId)) {
            excludedFromMessageNotifications.add(replyRecipientId);
        }

        notificationService.notifyMentionedUsers(mentionedUserIds, savedMessage, senderId);
        notificationService.notifyMessageRecipients(savedMessage, senderId, excludedFromMessageNotifications);

        if (replyRecipientId != null && !replyRecipientId.equals(senderId) && !mentionedUserIds.contains(replyRecipientId)) {
            notificationService.notifyUser(
                    replyRecipientId,
                    senderId,
                    NotificationType.MESSAGE_REPLY,
                    "New reply",
                    sender.getFullName() + " replied to your message",
                    String.valueOf(savedMessage.getId()),
                    "MESSAGE"
            );
        }

        return toMessageDto(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return toMessageDto(message);
    }

    @Override
    public MessageDto updateMessage(Long id, UpdateMessageRequest request, Long userId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        if (!message.getSender().getId().equals(userId)) {
            throw new UnauthorizedException("You can only edit your own messages");
        }

        message.setContent(request.getContent());
        message.setEdited(true);

        Message updatedMessage = messageRepository.save(message);
        return toMessageDto(updatedMessage);
    }

    @Override
    public void deleteMessage(Long id, Long userId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));

        if (!message.getSender().getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own messages");
        }

        message.setDeleted(true);
        message.setDeletedAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDto> getChannelMessages(Long channelId, Pageable pageable) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));
        return messageRepository.findByChannelIdAndDeletedFalse(channelId, pageable)
                .map(this::toMessageDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDto> getConversationMessages(Long conversationId, Pageable pageable) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));
        return messageRepository.findByConversationIdAndDeletedFalse(conversationId, pageable)
                .map(this::toMessageDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDto> getMessageReplies(Long messageId, Pageable pageable) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        return messageRepository.findByReplyToIdAndDeletedFalse(messageId, pageable)
                .map(this::toMessageDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDto> searchMessages(String query, Pageable pageable) {
        return messageRepository.searchByContent(query, pageable)
                .map(this::toMessageDto);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDto toMessageDto(Message message) {
        if (message == null || message.isDeleted()) {
            return null;
        }

        return MessageDto.builder()
                .id(message.getId())
                .content(message.getContent())
                .sender(userService.toUserDto(message.getSender()))
                .channelId(message.getChannel() != null ? message.getChannel().getId() : null)
                .conversationId(message.getConversation() != null ? message.getConversation().getId() : null)
                .replyToId(message.getReplyTo() != null ? message.getReplyTo().getId() : null)
                .replyToContent(message.getReplyTo() != null ? message.getReplyTo().getContent() : null)
                .edited(message.getEdited())
                .deleted(message.isDeleted())
                .reactionCount(message.getReactions().size())
                .replyCount(message.getReplies().size())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}


