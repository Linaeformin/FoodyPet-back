package org.example.foodypet.domain.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PetTodayDiaryHomeResponse {

    private List<PetHome> pets;

    @Getter
    @AllArgsConstructor
    public static class PetHome {
        private Long petId;
        private String petName;
        private String petImg;
        private TodayMeal todayMeal;
        private TodayDiary diary;
    }

    @Getter
    @AllArgsConstructor
    public static class TodayMeal {
        private Long dailyDietId;
        private Long mealScheduleId;
        private LocalTime mealTime;
        private String mealText;
        private List<TodayMealFood> foods;
        private boolean exists;
    }

    @Getter
    @AllArgsConstructor
    public static class TodayMealFood {
        private Long foodId;
        private String foodName;
        private BigDecimal amount;
        private String unit;
        private String displayText;
    }

    @Getter
    @AllArgsConstructor
    public static class TodayDiary {
        private MealDiary mealDiary;
        private WaterDiary waterDiary;
        private TreatDiary treatDiary;
        private CapsuleDiary capsuleDiary;
    }

    @Getter
    @AllArgsConstructor
    public static class MealDiary {
        private Integer givenCount;
        private Integer targetCount;
        private String displayText;
        private boolean exists;
    }

    @Getter
    @AllArgsConstructor
    public static class WaterDiary {
        private Long waterIntakeId;
        private BigDecimal totalAmountMl;
        private String displayText;
        private boolean exists;
    }

    @Getter
    @AllArgsConstructor
    public static class TreatDiary {
        private Integer givenCount;
        private String displayText;
        private boolean exists;
    }

    @Getter
    @AllArgsConstructor
    public static class CapsuleDiary {
        private Integer givenCount;
        private Integer targetCount;
        private String displayText;
        private boolean exists;
    }
}