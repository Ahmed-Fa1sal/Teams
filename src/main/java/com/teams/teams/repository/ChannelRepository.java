package com.teams.teams.repository;

import com.teams.teams.domain.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findByTeamId(Long teamId);
    Page<Channel> findByTeamIdAndArchivedFalse(Long teamId, Pageable pageable);
    Page<Channel> findByIsPublicAndArchivedFalse(Boolean isPublic, Pageable pageable);

    @Query("SELECT c FROM Channel c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Channel> searchByNameOrDescription(@Param("query") String query, Pageable pageable);

    @Query("""
            SELECT DISTINCT c FROM Channel c
            WHERE c.archived = false AND c.deleted = false
              AND (
                (c.isPublic = true AND EXISTS (
                    SELECT tm FROM TeamMember tm
                    WHERE tm.team = c.team AND tm.user.id = :userId
                ))
                OR EXISTS (
                    SELECT u FROM c.members u WHERE u.id = :userId
                )
              )
            """)
    Page<Channel> findAccessibleByUserId(@Param("userId") Long userId, Pageable pageable);
}

