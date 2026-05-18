package com.teams.teams.repository;

import com.teams.teams.domain.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Page<Attachment> findByMessageId(Long messageId, Pageable pageable);
    Page<Attachment> findByUploadedById(Long userId, Pageable pageable);

    @Query("SELECT a FROM Attachment a WHERE a.message.id = :messageId AND a.deletedAt IS NULL")
    Page<Attachment> findActiveByMessageId(@Param("messageId") Long messageId, Pageable pageable);

    @Query("SELECT a FROM Attachment a WHERE a.uploadedBy.id = :userId AND a.deletedAt IS NULL")
    Page<Attachment> findActiveByUploadedById(@Param("userId") Long userId, Pageable pageable);

    void deleteByMessageId(Long messageId);
}
