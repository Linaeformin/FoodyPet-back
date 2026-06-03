package org.example.foodypet.domain.diet.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DietRecommendSimpleResponse {

    private Long dailyDietId;

    private Long petId;
    private String petName;

    private LocalDate dietDate;

    private List<MealDto> meals;

    @Getter
    @Builder
    public static class MealDto {

        private Integer mealOrder;

        // "13:00" 형태로 내려줌
        private String mealTime;

        // 화면에 바로 뿌릴 문자열
        // 예: "조공 주식캔 내추럴 연어 21.89g, 더리얼 밀 캣 연어 18.06g"
        private String description;

        // 프론트에서 줄바꿈/스타일링 필요하면 이것도 사용 가능
        private List<FoodDto> foods;
    }

    @Getter
    @Builder
    public static class FoodDto {

        private Long foodId;
        private String foodName;

        // 예: 21.89
        private String amount;

        // 예: "g"
        private String unit;

        // 예: "조공 주식캔 내추럴 연어 21.89g"
        private String displayText;
    }
}