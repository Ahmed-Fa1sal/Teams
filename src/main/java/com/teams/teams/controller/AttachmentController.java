package com.teams.teams.controller;

import com.teams.teams.dto.ApiResponse;
import com.teams.teams.dto.AttachmentDto;
import com.teams.teams.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachments")
@Tag(name = "Attachments", description = "File attachment operations")
@SecurityRequirement(name = "Bearer Authentication")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload/{messageId}")
    @Operation(summary = "Upload an attachment to a message")
    public ResponseEntity<?> uploadAttachment(
            @PathVariable Long messageId,
            @RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        AttachmentDto attachment = attachmentService.uploadAttachment(messageId, file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(attachment));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attachment metadata by ID")
    public ResponseEntity<?> getAttachmentById(@PathVariable Long id) {
        AttachmentDto attachment = attachmentService.getAttachmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Attachment retrieved successfully", attachment));
    }

    @GetMapping("/message/{messageId}")
    @Operation(summary = "Get all attachments for a message")
    public ResponseEntity<?> getMessageAttachments(@PathVariable Long messageId, Pageable pageable) {
        Page<AttachmentDto> attachments = attachmentService.getMessageAttachments(messageId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Message attachments retrieved successfully", attachments));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download an attachment file")
    public ResponseEntity<?> downloadAttachment(@PathVariable Long id) {
        try {
            byte[] fileContent = attachmentService.downloadAttachment(id);
            AttachmentDto attachment = attachmentService.getAttachmentById(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("File not found or cannot be downloaded", 404));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an attachment")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(auth.getName());

        attachmentService.deleteAttachment(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Attachment deleted successfully"));
    }
}


