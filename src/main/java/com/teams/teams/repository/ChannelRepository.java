package com.teams.teams.repository;

import com.teams.teams.domain.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findByTeamId(Long teamId);
    Page<Channel> findByTeamIdAndArchivedFalse(Long teamId, Pageable pageable);
    Page<Channel> findByIsPublicAndArchivedFalse(Boolean isPublic, Pageable pageable);
}

