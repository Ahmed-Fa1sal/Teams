package com.teams.teams.service;

import com.teams.teams.domain.Notification;
import com.teams.teams.domain.NotificationType;
import com.teams.teams.dto.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationDto createNotification(Long recipientId, NotificationType type, String title, String message);
    NotificationDto createNotificationWithEntity(Long recipientId, NotificationType type, String title, String message, String entityId, String entityType);
    NotificationDto getNotificationById(Long id);
    Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable);
    Page<NotificationDto> getUnreadNotifications(Long userId, Pageable pageable);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId);
    NotificationDto toNotificationDto(Notification notification);
}

