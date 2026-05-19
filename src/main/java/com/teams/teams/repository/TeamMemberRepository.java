package com.teams.teams.repository;

import com.teams.teams.domain.TeamMember;
import com.teams.teams.domain.TeamMemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamId(Long teamId);

    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    boolean existsByTeamIdAndUserIdAndRole(Long teamId, Long userId, TeamMemberRole role);

    boolean existsByTeamIdAndUserIdAndRoleIn(Long teamId, Long userId, Collection<TeamMemberRole> roles);
}
