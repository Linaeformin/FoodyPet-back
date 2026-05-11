package org.example.foodypet.domain.community.entity;

import org.example.foodypet.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiverUser;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "actor_user_id", nullable = false)
    private User actorUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityNotificationType type;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id")
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "comment_id")
    private CommunityComment comment;

    @Column(length = 255, nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
