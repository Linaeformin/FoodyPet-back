package org.example.foodypet.domain.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.user.entity.User;

@Entity
@Table(name = "community_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityComment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_comment_id")
    private CommunityComment parentComment;

    @Column(length = 500, nullable = false)
    private String content;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
