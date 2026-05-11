package org.example.foodypet.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_blocks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"blocker_user_id", "blocked_user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBlock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "blocker_user_id", nullable = false)
    private User blockerUser;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "blocked_user_id", nullable = false)
    private User blockedUser;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
