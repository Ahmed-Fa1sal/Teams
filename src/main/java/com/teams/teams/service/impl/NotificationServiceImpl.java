package com.teams.teams.service.impl;

import com.teams.teams.domain.*;
import com.teams.teams.dto.NotificationDto;
import com.teams.teams.exception.BadRequestException;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.*;
import com.teams.teams.service.NotificationService;
import com.teams.teams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TeamMemberRepository teamMemberRepository;
    private final ChannelRepository channelRepository;
    private final ConversationRepository conversationRepository;

    @Override
    public NotificationDto createNotification(Long recipientId, NotificationType type, String title, String message) {
        return createNotificationInternal(recipientId, null, type, title, message, null, null, false);
    }

    @Override
    public NotificationDto createNotificationWithEntity(Long recipientId, NotificationType type, String title, String message, String entityId, String entityType) {
        return createNotificationInternal(recipientId, null, type, title, message, entityId, entityType, false);
    }

    @Override
    public NotificationDto notifyUser(
            Long recipientId,
            Long actorId,
            NotificationType type,
            String title,
            String message,
            String entityId,
            String entityType
    ) {
        return createNotificationInternal(recipientId, actorId, type, title, message, entityId, entityType, true);
    }

    @Override
    public void notifyUsers(
            Collection<Long> recipientIds,
            Long actorId,
            NotificationType type,
            String title,
            String message,
            String entityId,
            String entityType
    ) {
        if (recipientIds == null || recipientIds.isEmpty()) {
            return;
        }

        for (Long recipientId : toUniqueIds(recipientIds)) {
            notifyUser(recipientId, actorId, type, title, message, entityId, entityType);
        }
    }

    @Override
    public void notifyTeamMembers(
            Long teamId,
            Long actorId,
            NotificationType type,
            String title,
            String message,
            String entityId,
            String entityType
    ) {
        Set<Long> recipientIds = teamMemberRepository.findByTeamId(teamId).stream()
                .map(TeamMember::getUser)
                .map(User::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        notifyUsers(recipientIds, actorId, type, title, message, entityId, entityType);
    }

    @Override
    public void notifyChannelMembers(
            Long channelId,
            Long actorId,
            NotificationType type,
            String title,
            String message,
            String entityId,
            String entityType
    ) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + channelId));

        Set<Long> recipientIds = channel.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        notifyUsers(recipientIds, actorId, type, title, message, entityId, entityType);
    }

    @Override
    public void notifyConversationMembers(
            Long conversationId,
            Long actorId,
            NotificationType type,
            String title,
            String message,
            String entityId,
            String entityType
    ) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        Set<Long> recipientIds = conversation.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        notifyUsers(recipientIds, actorId, type, title, message, entityId, entityType);
    }

    @Override
    public void notifyMessageRecipients(Message message, Long actorId) {
        notifyMessageRecipients(message, actorId, Set.of());
    }

    @Override
    public void notifyMessageRecipients(Message message, Long actorId, Collection<Long> excludedRecipientIds) {
        validateMessageForNotifications(message);

        Set<Long> recipientIds = resolveMessageRecipientIds(message);
        recipientIds.removeAll(toUniqueIds(excludedRecipientIds));

        String senderName = message.getSender().getFullName();
        String entityId = String.valueOf(message.getId());

        if (message.getChannel() != null) {
            String channelName = message.getChannel().getName();
            notifyUsers(
                    recipientIds,
                    actorId,
                    NotificationType.CHANNEL_MESSAGE,
                    "New channel message",
                    senderName + " sent a message in " + channelName,
                    entityId,
                    "MESSAGE"
            );
            return;
        }

        notifyUsers(
                recipientIds,
                actorId,
                NotificationType.DIRECT_MESSAGE,
                "New message",
                senderName + " sent you a message",
                entityId,
                "MESSAGE"
        );
    }

    @Override
    public void notifyMentionedUsers(Collection<Long> mentionedUserIds, Message message, Long actorId) {
        if (mentionedUserIds == null || mentionedUserIds.isEmpty()) {
            return;
        }

        validateMessageForNotifications(message);

        Set<Long> mentionedIds = toUniqueIds(mentionedUserIds);
        Set<Long> allowedRecipientIds = resolveMessageRecipientIds(message);

        for (Long mentionedUserId : mentionedIds) {
            userRepository.findById(mentionedUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + mentionedUserId));

            if (!allowedRecipientIds.contains(mentionedUserId)) {
                throw new BadRequestException("Mentioned user is not a member of this conversation or channel");
            }
        }

        notifyUsers(
                mentionedIds,
                actorId,
                NotificationType.USER_MENTIONED,
                "You were mentioned",
                message.getSender().getFullName() + " mentioned you in a message",
                String.valueOf(message.getId()),
                "MESSAGE"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return toNotificationDto(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto getNotificationById(Long id, Long userId) {
        Notification notification = getOwnedNotification(id, userId);
        return toNotificationDto(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toNotificationDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUnreadNotifications(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable)
                .map(this::toNotificationDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = getOwnedNotification(notificationId, userId);
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        notificationRepository.markAllAsReadByRecipientId(userId);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notificationRepository.delete(notification);
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = getOwnedNotification(notificationId, userId);
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto toNotificationDto(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationDto.builder()
                .id(notification.getId())
                .recipient(userService.toUserDto(notification.getRecipient()))
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .relatedEntityId(notification.getRelatedEntityId())
                .relatedEntityType(notification.getRelatedEntityType())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private NotificationDto createNotificationInternal(
            Long recipientId,
            Long actorId,
            NotificationType type,
            String title,
            String message,
            String entityId,
            String entityType,
            boolean deduplicate
    ) {
        if (recipientId == null) {
            throw new BadRequestException("Notification recipient is required");
        }

        if (actorId != null && actorId.equals(recipientId)) {
            return null;
        }

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + recipientId));

        if (deduplicate && shouldCheckExistingNotification(type, entityId, entityType)
                && notificationRepository.existsByRecipientIdAndTypeAndRelatedEntityIdAndRelatedEntityType(
                        recipientId,
                        type,
                        entityId,
                        entityType
                )) {
            return null;
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .message(message)
                .relatedEntityId(entityId)
                .relatedEntityType(entityType)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return toNotificationDto(saved);
    }

    private Notification getOwnedNotification(Long notificationId, Long userId) {
        return notificationRepository.findByIdAndRecipientId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
    }

    private Set<Long> resolveMessageRecipientIds(Message message) {
        if (message.getChannel() != null) {
            Channel channel = channelRepository.findById(message.getChannel().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Channel not found with id: " + message.getChannel().getId()));
            return channel.getMembers().stream()
                    .map(User::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        if (message.getConversation() != null) {
            Conversation conversation = conversationRepository.findById(message.getConversation().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + message.getConversation().getId()));
            return conversation.getMembers().stream()
                    .map(User::getId)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        return new LinkedHashSet<>();
    }

    private void validateMessageForNotifications(Message message) {
        if (message == null || message.getId() == null) {
            throw new BadRequestException("Saved message is required for notifications");
        }
    }

    private Set<Long> toUniqueIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new LinkedHashSet<>();
        }

        return ids.stream()
                .filter(id -> id != null)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean shouldCheckExistingNotification(NotificationType type, String entityId, String entityType) {
        if (entityId == null || entityType == null) {
            return false;
        }

        return "MESSAGE".equals(entityType) || "CALL".equals(entityType) || "VIDEO_CALL".equals(entityType);
    }
}

