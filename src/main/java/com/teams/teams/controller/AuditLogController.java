package com.teams.teams.controller;

import com.teams.teams.dto.ApiResponse;
import com.teams.teams.dto.AuditLogDto;
import com.teams.teams.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-logs")
@Tag(name = "Audit Logs", description = "System audit logging endpoints (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') or hasRole('ROLE_ORG_ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log by ID (Admin only)")
    public ResponseEntity<?> getAuditLogById(@PathVariable Long id) {
        AuditLogDto auditLog = auditLogService.getAuditLogById(id);
        return ResponseEntity.ok(ApiResponse.success("Audit log retrieved successfully", auditLog));
    }

    @GetMapping
    @Operation(summary = "Get all audit logs (Admin only)")
    public ResponseEntity<?> getAllAuditLogs(Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditLogService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", auditLogs));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get audit logs for a user (Admin only)")
    public ResponseEntity<?> getUserAuditLogs(@PathVariable Long userId, Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditLogService.getUserAuditLogs(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("User audit logs retrieved successfully", auditLogs));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get audit logs for an entity (Admin only)")
    public ResponseEntity<?> getEntityAuditLogs(@PathVariable String entityType, @PathVariable String entityId, Pageable pageable) {
        Page<AuditLogDto> auditLogs = auditLogService.getEntityAuditLogs(entityType, entityId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Entity audit logs retrieved successfully", auditLogs));
    }
}

