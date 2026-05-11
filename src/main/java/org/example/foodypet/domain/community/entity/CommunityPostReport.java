package org.example.foodypet.domain.community.entity;

import org.example.foodypet.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_post_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "reporter_user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityPostReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporterUser;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
