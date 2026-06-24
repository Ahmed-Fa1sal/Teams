package com.teams.teams.repository;

import com.teams.teams.domain.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByTeamId(Long teamId);

    Optional<Conversation> findByChannelId(Long channelId);

    @Query("""
            SELECT c FROM Conversation c
            WHERE c.type = com.teams.teams.domain.ConversationType.DIRECT
            AND EXISTS (SELECT m FROM ConversationMember m WHERE m.conversation = c AND m.user.id = :userId1)
            AND EXISTS (SELECT m FROM ConversationMember m WHERE m.conversation = c AND m.user.id = :userId2)
            """)
    Optional<Conversation> findDirectConversationBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    @Query("""
            SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END
            FROM ConversationMember m
            WHERE m.conversation.id = :conversationId AND m.user.id = :userId
            """)
    boolean isMember(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query(value = """
            SELECT c FROM Conversation c
            JOIN c.members m
            WHERE m.user.id = :userId
            ORDER BY COALESCE(
                (SELECT MAX(msg.createdAt) FROM Message msg WHERE msg.conversation = c AND msg.deleted = false),
                c.updatedAt
            ) DESC
            """,
            countQuery = """
            SELECT COUNT(c) FROM Conversation c
            JOIN c.members m
            WHERE m.user.id = :userId
            """)
    Page<Conversation> findAllByMemberId(@Param("userId") Long userId, Pageable pageable);
}
