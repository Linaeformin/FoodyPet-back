package org.example.foodypet.domain.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.diary.entity.PetMealDiary;
import org.example.foodypet.domain.user.entity.User;

@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_diary_id", nullable = false)
    private PetMealDiary mealDiary;

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

    public static CommunityPost create(
            User user,
            PetMealDiary mealDiary,
            String title,
            String content
    ) {
        CommunityPost post = new CommunityPost();
        post.user = user;
        post.mealDiary = mealDiary;
        post.title = title;
        post.content = content;
        return post;
    }
}