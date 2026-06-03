package org.example.foodypet.domain.diary.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MealDiaryWriteFormResponse {

    private Long dailyDietId;
    private Long petId;
    private String petName;
    private LocalDate dietDate;

    private List<MealDto> meals;
    private List<CapsuleDto> capsules;

    @Getter
    @Builder
    public static class MealDto {
        private Long petMealScheduleId;
        private Integer mealOrder;
        private String mealTime;
        private String description;
        private List<FoodDto> foods;
    }

    @Getter
    @Builder
    public static class FoodDto {
        private Long foodId;
        private String foodName;
        private String amount;
        private String unit;
        private String displayText;
    }

    @Getter
    @Builder
    public static class CapsuleDto {
        private Long petCapsuleId;
        private String capsuleName;
        private Integer dailyCount;
    }
}