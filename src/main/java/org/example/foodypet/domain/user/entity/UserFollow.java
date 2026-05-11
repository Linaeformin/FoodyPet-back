package org.example.foodypet.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_user_id", "following_user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFollow {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "follower_user_id", nullable = false)
    private User followerUser;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "following_user_id", nullable = false)
    private User followingUser;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
