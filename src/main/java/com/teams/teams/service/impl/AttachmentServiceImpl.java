package com.teams.teams.service.impl;

import com.teams.teams.domain.Attachment;
import com.teams.teams.domain.Message;
import com.teams.teams.domain.User;
import com.teams.teams.dto.AttachmentDto;
import com.teams.teams.exception.BadRequestException;
import com.teams.teams.exception.ResourceNotFoundException;
import com.teams.teams.exception.UnauthorizedException;
import com.teams.teams.repository.AttachmentRepository;
import com.teams.teams.repository.MessageRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.service.AttachmentService;
import com.teams.teams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${app.file.upload-dir:./storage/attachments}")
    private String uploadDir;

    @Value("${app.file.max-size:52428800}")
    private long maxFileSize; // Default 50MB

    @Override
    public AttachmentDto uploadAttachment(Long messageId, MultipartFile file, Long userId) {
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new BadRequestException("File size exceeds maximum allowed size of " + (maxFileSize / 1024 / 1024) + "MB");
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, messageId.toString());
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID() + fileExtension;

            // Save file to disk
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.write(filePath, file.getBytes());

            // Create attachment entity
            Attachment attachment = Attachment.builder()
                    .message(message)
                    .originalFilename(originalFilename)
                    .storedFilename(uniqueFilename)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .filePath(filePath.toString())
                    .uploadedBy(uploader)
                    .build();

            Attachment saved = attachmentRepository.save(attachment);
            return toAttachmentDto(saved);

        } catch (IOException e) {
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentDto getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + id));
        return toAttachmentDto(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttachmentDto> getMessageAttachments(Long messageId, Pageable pageable) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
        return attachmentRepository.findByMessageId(messageId, pageable)
                .map(this::toAttachmentDto);
    }

    @Override
    public void deleteAttachment(Long id, Long userId) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + id));

        if (!attachment.getUploadedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own attachments");
        }

        try {
            // Delete file from disk
            Path filePath = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log but don't fail - file may already be deleted
            System.err.println("Failed to delete attachment file: " + e.getMessage());
        }

        attachmentRepository.delete(attachment);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + id));

        try {
            Path filePath = Paths.get(attachment.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new BadRequestException("Failed to download attachment: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentDto toAttachmentDto(Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        return AttachmentDto.builder()
                .id(attachment.getId())
                .fileName(attachment.getOriginalFilename())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .fileUrl("/api/v1/attachments/" + attachment.getId() + "/download")
                .messageId(attachment.getMessage().getId())
                .uploadedBy(userService.toUserDto(attachment.getUploadedBy()))
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}

