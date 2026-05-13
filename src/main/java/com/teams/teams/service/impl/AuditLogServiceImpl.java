package com.teams.teams.service.impl;

import com.teams.teams.domain.AuditLog;
import com.teams.teams.domain.User;
import com.teams.teams.dto.AuditLogDto;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.repository.AuditLogRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.AuditLogService;
import com.teams.teams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public void logAction(Long userId, String action, String entityType, String entityId, String details, String ipAddress) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress)
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    public void logAction(Long userId, String action, String entityType, String entityId, String details) {
        logAction(userId, action, entityType, entityId, details, null);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogDto getAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found with id: " + id));
        return toAuditLogDto(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getUserAuditLogs(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toAuditLogDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getEntityAuditLogs(String entityType, String entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId, pageable)
                .map(this::toAuditLogDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toAuditLogDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogDto toAuditLogDto(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }

        return AuditLogDto.builder()
                .id(auditLog.getId())
                .user(auditLog.getUser() != null ? userService.toUserDto(auditLog.getUser()) : null)
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .details(auditLog.getDetails())
                .ipAddress(auditLog.getIpAddress())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}

