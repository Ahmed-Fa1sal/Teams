package com.teams.teams.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    @ToString.Exclude
    private Organization organization;

    @Column(nullable = false)
    @ToString.Include
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean archived = false;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<TeamMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    @ToString.Exclude
    private Set<Channel> channels = new HashSet<>();


    public void addMember(User user, TeamMemberRole role) {
        TeamMember member = TeamMember.builder()
                .team(this)
                .user(user)
                .role(role)
                .joinedAt(LocalDateTime.now())
                .build();

        members.add(member);
    }
}
