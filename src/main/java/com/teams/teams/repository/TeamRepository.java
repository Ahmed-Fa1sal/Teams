package com.teams.teams.repository;

import com.teams.teams.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByOwnerId(Long ownerId);
    Page<Team> findByIsPublic(Boolean isPublic, Pageable pageable);
    Page<Team> findByArchivedFalse(Pageable pageable);
}

