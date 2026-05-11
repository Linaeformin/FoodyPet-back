package org.example.foodypet.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.foodypet.domain.food.entity.PetFoodStock;
import org.example.foodypet.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pet_food_stock_id")
    private PetFoodStock petFoodStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(length = 255, nullable = false)
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "notified_at", nullable = false)
    private LocalDateTime notifiedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
