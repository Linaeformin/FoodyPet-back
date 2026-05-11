package org.example.foodypet.domain.user.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inquiries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "reply_email", length = 100, nullable = false)
    private String replyEmail;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
}
