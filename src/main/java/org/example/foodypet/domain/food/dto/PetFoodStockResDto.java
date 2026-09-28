package org.example.foodypet.domain.food.dto;

import lombok.*;
import org.example.foodypet.domain.food.entity.FoodSource;
import org.example.foodypet.domain.food.entity.FoodType;
import org.example.foodypet.domain.food.entity.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetFoodStockResDto {

    private Long stockId;

    private Long petFoodId;

    private String foodName;

    // 음식 대표 이미지
    private String foodImg;

    // 영양성분표 이미지
    private String nutritionImg;

    private FoodType foodType;

    private FoodSource source;

    private BigDecimal quantity;

    private Unit unit;

    private LocalDate expiredAt;

    private Boolean isTreat;

    private Boolean expired;
}