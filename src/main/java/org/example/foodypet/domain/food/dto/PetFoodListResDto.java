package org.example.foodypet.domain.food.dto;

import lombok.*;
import org.example.foodypet.domain.food.entity.FoodSource;
import org.example.foodypet.domain.food.entity.FoodType;
import org.example.foodypet.domain.food.entity.Unit;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetFoodListResDto {

    private List<PetFoodDto> foods;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PetFoodDto {

        // 사료 ID
        private Long foodId;

        // 사료명
        private String foodName;

        // 사료 이미지 URL
        private String imageUrl;

        // 영양성분표 이미지 URL
        private String nutritionImageUrl;

        // 사료 출처: SYSTEM, USER
        private FoodSource foodSource;

        // 사료 타입: COOKED, WET, RAW, DRY
        private FoodType foodType;

        // 단위: BAG, COUNT, ML, GRAM
        private Unit unit;
    }
}