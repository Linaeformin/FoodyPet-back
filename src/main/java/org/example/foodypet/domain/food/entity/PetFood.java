package org.example.foodypet.domain.food.entity;

import org.example.foodypet.common.entity.BaseTimeEntity;
import org.example.foodypet.domain.pet.entity.PetType;
import org.example.foodypet.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pet_foods")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetFood extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Lob
    @Column(name = "food_img")
    private String foodImg;

    @Lob
    @Column(name = "nutrition_img")
    private String nutritionImg;

    @Enumerated(EnumType.STRING)
    @Column(name = "food_type", nullable = false)
    private FoodType foodType;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private Unit unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_type", nullable = false)
    private PetType petType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodSource source = FoodSource.SYSTEM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "metabolizable_energy_kcal_per_100g", precision = 8, scale = 2, nullable = false)
    private BigDecimal metabolizableEnergyKcalPer100g;

    @Column(name = "crude_protein_percent", precision = 5, scale = 2, nullable = false)
    private BigDecimal crudeProteinPercent;

    @Column(name = "crude_fat_percent", precision = 5, scale = 2, nullable = false)
    private BigDecimal crudeFatPercent;

    @Column(name = "crude_ash_percent", precision = 5, scale = 2)
    private BigDecimal crudeAshPercent;

    @Column(name = "crude_fiber_percent", precision = 5, scale = 2)
    private BigDecimal crudeFiberPercent;

    @Column(name = "calcium_percent", precision = 5, scale = 2)
    private BigDecimal calciumPercent;

    @Column(name = "phosphorus_percent", precision = 5, scale = 2)
    private BigDecimal phosphorusPercent;

    @Column(name = "taurine_mg_per_100g", precision = 8, scale = 2)
    private BigDecimal taurineMgPer100g;

    @Column(name = "food_like", precision = 5, scale = 2, nullable = false)
    private BigDecimal foodLike = new BigDecimal("50.00");
}