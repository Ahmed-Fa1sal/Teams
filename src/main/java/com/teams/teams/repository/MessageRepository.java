package com.teams.teams.repository;

import com.teams.teams.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByChannelIdAndDeletedFalse(Long channelId, Pageable pageable);
    Page<Message> findByConversationIdAndDeletedFalse(Long conversationId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.replyTo.id = :replyToId AND m.deleted = false")
    Page<Message> findByReplyToIdAndDeletedFalse(@Param("replyToId") Long messageId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')) AND m.deleted = false")
    Page<Message> searchByContent(@Param("query") String query, Pageable pageable);
}

