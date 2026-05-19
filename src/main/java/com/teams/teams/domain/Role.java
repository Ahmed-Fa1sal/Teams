package com.teams.teams.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    public enum RoleType {
        SYSTEM_ADMIN(1L, "ROLE_SYSTEM_ADMIN"),
        ORG_ADMIN(2L, "ROLE_ORG_ADMIN"),
        TEAM_OWNER(3L, "ROLE_TEAM_OWNER"),
        STANDARD_USER(4L, "ROLE_STANDARD_USER"),
        GUEST(5L, "ROLE_GUEST");

        public final Long id;
        public final String value;

        RoleType(Long id, String value) {
            this.id = id;
            this.value = value;
        }
    }
}

