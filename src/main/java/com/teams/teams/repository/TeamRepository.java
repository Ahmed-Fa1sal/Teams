package com.teams.teams.repository;

import com.teams.teams.domain.Team;
import com.teams.teams.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByOwnerId(Long ownerId);
    Page<Team> findByIsPublic(Boolean isPublic, Pageable pageable);
    Page<Team> findByArchivedFalse(Pageable pageable);

    // Find teams where the given user is a member or the owner id matches
    @Query("SELECT t FROM Team t WHERE (:user MEMBER OF t.members OR t.owner.id = :ownerId)")
    Page<Team> findByMembersContainingOrOwnerId(@Param("user") User user, @Param("ownerId") Long ownerId, Pageable pageable);

    @Query("SELECT t FROM Team t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Team> searchByNameOrDescription(@Param("query") String query, Pageable pageable);
}

