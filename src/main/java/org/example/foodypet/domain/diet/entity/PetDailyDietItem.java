package org.example.foodypet.domain.diet.entity;

import org.example.foodypet.domain.food.entity.PetFood;
import org.example.foodypet.domain.food.entity.Unit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_daily_diet_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetDailyDietItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_diet_id", nullable = false)
    private PetDailyDiet dailyDiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_food_id", nullable = false)
    private PetFood petFood;

    @Column(name = "meal_order", nullable = false)
    private Integer mealOrder;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @Column(precision = 8, scale = 2)
    private BigDecimal calorie;
    @Column(precision = 8, scale = 2)
    private BigDecimal protein;
    @Column(precision = 8, scale = 2)
    private BigDecimal fat;
    @Column(precision = 8, scale = 2)
    private BigDecimal ash;
    @Column(precision = 8, scale = 2)
    private BigDecimal fiber;
    @Column(precision = 8, scale = 2)
    private BigDecimal calcium;
    @Column(precision = 8, scale = 2)
    private BigDecimal phosphorus;
    @Column(precision = 8, scale = 2)
    private BigDecimal taurine;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
