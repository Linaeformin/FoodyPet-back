package org.example.foodypet.domain.diet.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class DietRecommendResponse {

    private Long dailyDietId;

    private Long petId;
    private String petName;
    private LocalDate dietDate;

    private Integer mealCount;

    private AnalysisStatus calorieStatus;
    private AnalysisStatus proteinStatus;
    private AnalysisStatus fatStatus;
    private AnalysisStatus ashStatus;
    private AnalysisStatus fiberStatus;
    private AnalysisStatus calciumPhosphorusRatioStatus;
    private AnalysisStatus taurineStatus;

    private BigDecimal recommendedCalorie;
    private BigDecimal recommendedProtein;
    private BigDecimal recommendedFat;
    private BigDecimal recommendedAsh;
    private BigDecimal recommendedFiber;
    private BigDecimal recommendedCalcium;
    private BigDecimal recommendedPhosphorus;
    private BigDecimal recommendedTaurine;

    private BigDecimal totalCalorie;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalAsh;
    private BigDecimal totalFiber;
    private BigDecimal totalCalcium;
    private BigDecimal totalPhosphorus;
    private BigDecimal totalTaurine;
    private BigDecimal calciumPhosphorusRatio;

    private List<MealRecommendDto> meals;

    @Getter
    @Builder
    public static class MealRecommendDto {

        private Integer mealOrder;
        private LocalTime mealTime;

        private BigDecimal actualCalorie;
        private BigDecimal actualProtein;
        private BigDecimal actualFat;
        private BigDecimal actualAsh;
        private BigDecimal actualFiber;
        private BigDecimal actualCalcium;
        private BigDecimal actualPhosphorus;
        private BigDecimal actualTaurine;

        private List<MealFoodDto> foods;
    }

    @Getter
    @Builder
    public static class MealFoodDto {

        private Long foodId;
        private String foodName;
        private String foodImg;

        private BigDecimal amount;
        private String unit;

        private BigDecimal calorie;
        private BigDecimal protein;
        private BigDecimal fat;
        private BigDecimal ash;
        private BigDecimal fiber;
        private BigDecimal calcium;
        private BigDecimal phosphorus;
        private BigDecimal taurine;
        private BigDecimal foodLike;
    }
}