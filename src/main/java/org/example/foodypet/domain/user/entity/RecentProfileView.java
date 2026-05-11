package org.example.foodypet.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recent_profile_views", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "viewed_user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentProfileView {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "viewed_user_id", nullable = false)
    private User viewedUser;

    @Column(name = "viewed_at", updatable = false)
    private LocalDateTime viewedAt;
}
