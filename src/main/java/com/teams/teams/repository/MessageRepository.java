package com.teams.teams.repository;

import com.teams.teams.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByChannelIdAndDeletedFalse(Long channelId, Pageable pageable);
    Page<Message> findByConversationIdAndDeletedFalse(Long conversationId, Pageable pageable);
}

