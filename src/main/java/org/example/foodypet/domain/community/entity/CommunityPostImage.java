package org.example.foodypet.domain.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_post_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @Column(name = "image_url", length = 500, nullable = false)
    private String imageUrl;

    @Column(name = "image_order", nullable = false)
    private Integer imageOrder = 1;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public static CommunityPostImage create(
            CommunityPost post,
            String imageUrl,
            Integer imageOrder
    ) {
        CommunityPostImage postImage = new CommunityPostImage();
        postImage.post = post;
        postImage.imageUrl = imageUrl;
        postImage.imageOrder = imageOrder;
        postImage.createdAt = LocalDateTime.now();
        return postImage;
    }
}