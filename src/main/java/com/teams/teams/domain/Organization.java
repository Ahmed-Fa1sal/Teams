package com.teams.teams.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Organization extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String logoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<OrganizationMember> members = new ArrayList<>();

    @Column
    private String tier; // e.g., "FREE", "PROFESSIONAL", "ENTERPRISE"

    public void addMember(OrganizationMember member) {
        members.add(member);
    }

    public void removeMember(OrganizationMember member) {
        members.remove(member);
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
    }
}

