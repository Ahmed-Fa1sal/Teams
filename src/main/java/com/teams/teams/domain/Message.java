package com.teams.teams.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "reply_to_id")
    private Message replyTo;

    @OneToMany(mappedBy = "replyTo", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    private Set<Message> replies = new HashSet<>();

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    private Set<MessageReaction> reactions = new HashSet<>();

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    private Set<MessageRead> readReceipts = new HashSet<>();

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Builder.Default
    private Set<Attachment> attachments = new HashSet<>();

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column
    private Boolean edited = false;

    @Column
    private Boolean deleted = false;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

