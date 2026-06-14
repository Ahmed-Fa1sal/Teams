package com.teams.teams.service;

import com.teams.teams.domain.Message;
import com.teams.teams.domain.Notification;
import com.teams.teams.domain.NotificationType;
import com.teams.teams.dto.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface NotificationService {
    NotificationDto createNotification(Long recipientId, NotificationType type, String title, String message);
    NotificationDto createNotificationWithEntity(Long recipientId, NotificationType type, String title, String message, String entityId, String entityType);
    NotificationDto notifyUser(Long recipientId, Long actorId, NotificationType type, String title, String message, String entityId, String entityType);
    void notifyUsers(Collection<Long> recipientIds, Long actorId, NotificationType type, String title, String message, String entityId, String entityType);
    void notifyTeamMembers(Long teamId, Long actorId, NotificationType type, String title, String message, String entityId, String entityType);
    void notifyChannelMembers(Long channelId, Long actorId, NotificationType type, String title, String message, String entityId, String entityType);
    void notifyConversationMembers(Long conversationId, Long actorId, NotificationType type, String title, String message, String entityId, String entityType);
    void notifyMessageRecipients(Message message, Long actorId);
    void notifyMessageRecipients(Message message, Long actorId, Collection<Long> excludedRecipientIds);
    void notifyMentionedUsers(Collection<Long> mentionedUserIds, Message message, Long actorId);
    NotificationDto getNotificationById(Long id);
    NotificationDto getNotificationById(Long id, Long userId);
    Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable);
    Page<NotificationDto> getUnreadNotifications(Long userId, Pageable pageable);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId);
    void deleteNotification(Long notificationId, Long userId);
    NotificationDto toNotificationDto(Notification notification);
}

