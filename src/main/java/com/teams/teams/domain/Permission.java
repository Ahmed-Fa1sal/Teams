package com.teams.teams.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    public enum PermissionType {
        // User Permissions
        USER_CREATE("user:create"),
        USER_READ("user:read"),
        USER_UPDATE("user:update"),
        USER_DELETE("user:delete"),

        // Team Permissions
        TEAM_CREATE("team:create"),
        TEAM_READ("team:read"),
        TEAM_UPDATE("team:update"),
        TEAM_DELETE("team:delete"),

        // Channel Permissions
        CHANNEL_CREATE("channel:create"),
        CHANNEL_READ("channel:read"),
        CHANNEL_UPDATE("channel:update"),
        CHANNEL_DELETE("channel:delete"),

        // Message Permissions
        MESSAGE_CREATE("message:create"),
        MESSAGE_READ("message:read"),
        MESSAGE_UPDATE("message:update"),
        MESSAGE_DELETE("message:delete"),

        // File Permissions
        FILE_UPLOAD("file:upload"),
        FILE_DOWNLOAD("file:download"),
        FILE_DELETE("file:delete"),

        // Admin Permissions
        ADMIN_ACCESS("admin:access"),
        AUDIT_READ("audit:read");

        public final String value;

        PermissionType(String value) {
            this.value = value;
        }
    }
}

