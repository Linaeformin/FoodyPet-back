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

    private static final BigDecimal DEFAULT_FOOD_LIKE = new BigDecimal("50.00");

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

    @Column(name = "is_treat", nullable = false)
    private Boolean isTreat = false;

    @Column(name = "food_like", precision = 5, scale = 2, nullable = false)
    private BigDecimal foodLike;

    public static PetFoodStock create(
            User user,
            PetFood petFood,
            BigDecimal quantity,
            Unit unit,
            LocalDate expiredAt,
            Boolean isTreat
    ) {
        PetFoodStock stock = new PetFoodStock();
        stock.user = user;
        stock.petFood = petFood;
        stock.quantity = quantity;
        stock.unit = unit;
        stock.expiredAt = expiredAt;
        stock.isTreat = isTreat != null ? isTreat : false;
        stock.foodLike = DEFAULT_FOOD_LIKE;
        return stock;
    }

    public void updateQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void update(
            BigDecimal quantity,
            Unit unit,
            LocalDate expiredAt,
            Boolean isTreat
    ) {
        this.quantity = quantity;
        this.unit = unit;
        this.expiredAt = expiredAt;
        this.isTreat = isTreat != null ? isTreat : false;
    }

    public void decreaseFoodLike(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        if (this.foodLike == null) {
            this.foodLike = DEFAULT_FOOD_LIKE;
        }

        this.foodLike = this.foodLike.subtract(value);

        if (this.foodLike.compareTo(BigDecimal.ZERO) < 0) {
            this.foodLike = BigDecimal.ZERO;
        }
    }
}