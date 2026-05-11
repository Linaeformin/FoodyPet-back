package org.example.foodypet.domain.community.entity;

import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityPost extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "daily_diet_id", nullable = false)
    private PetDailyDiet dailyDiet;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    private String content;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "bookmark_count", nullable = false)
    private Integer bookmarkCount = 0;
}
