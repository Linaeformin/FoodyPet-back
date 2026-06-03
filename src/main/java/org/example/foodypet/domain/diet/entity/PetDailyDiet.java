package org.example.foodypet.domain.diet.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.food.entity.FoodSource;
import org.example.foodypet.domain.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pet_daily_diets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetDailyDiet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "diet_date", nullable = false)
    private LocalDate dietDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodSource source;

    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed;

    @Column(name = "total_calorie", precision = 8, scale = 2)
    private BigDecimal totalCalorie;

    @Column(name = "total_protein", precision = 8, scale = 2)
    private BigDecimal totalProtein;

    @Column(name = "total_fat", precision = 8, scale = 2)
    private BigDecimal totalFat;

    @Column(name = "total_ash", precision = 8, scale = 2)
    private BigDecimal totalAsh;

    @Column(name = "total_fiber", precision = 8, scale = 2)
    private BigDecimal totalFiber;

    @Column(name = "total_calcium", precision = 8, scale = 2)
    private BigDecimal totalCalcium;

    @Column(name = "total_phosphorus", precision = 8, scale = 2)
    private BigDecimal totalPhosphorus;

    @Column(name = "total_taurine", precision = 8, scale = 2)
    private BigDecimal totalTaurine;

    @Column(name = "calcium_phosphorus_ratio", precision = 5, scale = 2)
    private BigDecimal calciumPhosphorusRatio;

    public static PetDailyDiet createRecommendedDiet(
            Pet pet,
            LocalDate dietDate,
            FoodSource source,
            BigDecimal totalCalorie,
            BigDecimal totalProtein,
            BigDecimal totalFat,
            BigDecimal totalAsh,
            BigDecimal totalFiber,
            BigDecimal totalCalcium,
            BigDecimal totalPhosphorus,
            BigDecimal totalTaurine,
            BigDecimal calciumPhosphorusRatio
    ) {
        PetDailyDiet diet = new PetDailyDiet();
        diet.pet = pet;
        diet.dietDate = dietDate;
        diet.source = source;
        diet.isConfirmed = false;
        diet.totalCalorie = totalCalorie;
        diet.totalProtein = totalProtein;
        diet.totalFat = totalFat;
        diet.totalAsh = totalAsh;
        diet.totalFiber = totalFiber;
        diet.totalCalcium = totalCalcium;
        diet.totalPhosphorus = totalPhosphorus;
        diet.totalTaurine = totalTaurine;
        diet.calciumPhosphorusRatio = calciumPhosphorusRatio;
        return diet;
    }


    public void updateRecommendedDiet(
            FoodSource source,
            BigDecimal totalCalorie,
            BigDecimal totalProtein,
            BigDecimal totalFat,
            BigDecimal totalAsh,
            BigDecimal totalFiber,
            BigDecimal totalCalcium,
            BigDecimal totalPhosphorus,
            BigDecimal totalTaurine,
            BigDecimal calciumPhosphorusRatio
    ) {
        if (Boolean.TRUE.equals(this.isConfirmed)) {
            throw new IllegalStateException("이미 최종 등록된 식단은 수정할 수 없어.");
        }

        this.source = source;
        this.totalCalorie = totalCalorie;
        this.totalProtein = totalProtein;
        this.totalFat = totalFat;
        this.totalAsh = totalAsh;
        this.totalFiber = totalFiber;
        this.totalCalcium = totalCalcium;
        this.totalPhosphorus = totalPhosphorus;
        this.totalTaurine = totalTaurine;
        this.calciumPhosphorusRatio = calciumPhosphorusRatio;
    }

    public void confirm() {
        this.isConfirmed = true;
    }
}