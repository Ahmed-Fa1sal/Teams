package com.teams.teams.repository;

import com.teams.teams.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByNameIgnoreCase(String name);

    @Query("SELECT o FROM Organization o WHERE o.deleted = false")
    Page<Organization> findAllActive(Pageable pageable);

    @Query("SELECT o FROM Organization o WHERE o.active = true AND o.deleted = false")
    Page<Organization> findAllActiveAndNotDeleted(Pageable pageable);

    @Query("""
            SELECT o FROM Organization o
            WHERE (LOWER(o.name) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(o.description) LIKE LOWER(CONCAT('%', :query, '%')))
            AND o.deleted = false
            """)
    Page<Organization> searchByNameOrDescription(@Param("query") String query, Pageable pageable);
}

