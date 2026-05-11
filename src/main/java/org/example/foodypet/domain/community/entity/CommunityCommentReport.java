package org.example.foodypet.domain.community.entity;

import org.example.foodypet.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_comment_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"comment_id", "reporter_user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityCommentReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "comment_id", nullable = false)
    private CommunityComment comment;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporterUser;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
