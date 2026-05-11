package org.example.foodypet.domain.search.entity;

import org.example.foodypet.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recent_search_keywords", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "keyword"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecentSearchKeyword {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String keyword;

    @Column(name = "searched_at", updatable = false)
    private LocalDateTime searchedAt;
}
