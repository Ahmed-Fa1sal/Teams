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

    @Query("SELECT t FROM Team t WHERE (t.id IN (SELECT tm FROM Team tm WHERE :user MEMBER OF tm.members) OR t.owner = :user)")
    Page<Team> findByMembersContainingOrOwnerId(@Param("user") User user, @Param("ownerId") Long ownerId, Pageable pageable);

    @Query("SELECT t FROM Team t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Team> searchByNameOrDescription(@Param("query") String query, Pageable pageable);
}

