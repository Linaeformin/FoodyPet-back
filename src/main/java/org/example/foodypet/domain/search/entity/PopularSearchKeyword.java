package org.example.foodypet.domain.search.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "popular_search_keywords", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"keyword", "based_at"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularSearchKeyword extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String keyword;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @Column(name = "previous_rank")
    private Integer previousRank;

    @Column(name = "search_count", nullable = false)
    private Integer searchCount = 0;

    @Column(name = "based_at", nullable = false)
    private LocalDateTime basedAt;
}
