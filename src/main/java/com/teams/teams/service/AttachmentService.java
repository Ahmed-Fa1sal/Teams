package com.teams.teams.service;

import com.teams.teams.domain.Attachment;
import com.teams.teams.dto.AttachmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentDto uploadAttachment(Long messageId, MultipartFile file, Long userId);
    AttachmentDto getAttachmentById(Long id);
    Page<AttachmentDto> getMessageAttachments(Long messageId, Pageable pageable);
    void deleteAttachment(Long id, Long userId);
    byte[] downloadAttachment(Long id);
    AttachmentDto toAttachmentDto(Attachment attachment);
}

