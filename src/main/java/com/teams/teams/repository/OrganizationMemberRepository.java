package com.teams.teams.repository;

import com.teams.teams.domain.OrganizationMember;
import com.teams.teams.domain.OrganizationMember.OrganizationMemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {

    Optional<OrganizationMember> findByOrganizationIdAndUserId(Long organizationId, Long userId);

    @Query("SELECT om FROM OrganizationMember om WHERE om.organization.id = :organizationId AND om.deleted = false")
    Page<OrganizationMember> findByOrganizationId(@Param("organizationId") Long organizationId, Pageable pageable);

    @Query("SELECT om FROM OrganizationMember om WHERE om.organization.id = :organizationId AND om.role = :role AND om.deleted = false")
    Page<OrganizationMember> findByOrganizationIdAndRole(@Param("organizationId") Long organizationId,
                                                         @Param("role") OrganizationMemberRole role,
                                                         Pageable pageable);

    @Query("SELECT om FROM OrganizationMember om WHERE om.user.id = :userId AND om.deleted = false")
    Page<OrganizationMember> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(om) FROM OrganizationMember om WHERE om.organization.id = :organizationId AND om.deleted = false")
    long countMembersByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("SELECT om FROM OrganizationMember om WHERE om.organization.id = :organizationId AND om.role = :role AND om.deleted = false")
    java.util.List<OrganizationMember> findAdminsByOrganizationAndRole(@Param("organizationId") Long organizationId,
                                                                       @Param("role") OrganizationMemberRole role);
}

