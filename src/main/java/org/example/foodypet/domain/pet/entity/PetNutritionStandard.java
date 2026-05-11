package org.example.foodypet.domain.pet.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pet_nutrition_standards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetNutritionStandard extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, unique = true)
    private Pet pet;

    @Column(name = "recommended_calorie", precision = 8, scale = 2, nullable = false)
    private BigDecimal recommendedCalorie;

    @Column(name = "recommended_protein", precision = 8, scale = 2, nullable = false)
    private BigDecimal recommendedProtein;

    @Column(name = "recommended_fat", precision = 8, scale = 2, nullable = false)
    private BigDecimal recommendedFat;

    @Column(name = "recommended_ash", precision = 8, scale = 2)
    private BigDecimal recommendedAsh;

    @Column(name = "recommended_fiber", precision = 8, scale = 2)
    private BigDecimal recommendedFiber;
}
