package org.example.foodypet.domain.food.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pet_food_stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetFoodStock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_food_id", nullable = false)
    private PetFood petFood;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @Column(name = "expired_at")
    private LocalDate expiredAt;
}
