package com.teams.teams.service;

import com.teams.teams.domain.AuditLog;
import com.teams.teams.dto.AuditLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void logAction(Long userId, String action, String entityType, String entityId, String details, String ipAddress);
    void logAction(Long userId, String action, String entityType, String entityId, String details);
    AuditLogDto getAuditLogById(Long id);
    Page<AuditLogDto> getUserAuditLogs(Long userId, Pageable pageable);
    Page<AuditLogDto> getEntityAuditLogs(String entityType, String entityId, Pageable pageable);
    Page<AuditLogDto> getAllAuditLogs(Pageable pageable);
    AuditLogDto toAuditLogDto(AuditLog auditLog);
}

