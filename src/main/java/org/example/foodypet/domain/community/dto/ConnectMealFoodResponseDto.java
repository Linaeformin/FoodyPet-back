package org.example.foodypet.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.example.foodypet.domain.food.entity.Unit;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ConnectMealFoodResponseDto {

    private Long dietItemId;

    private Long foodId;

    private String foodName;

    private Integer mealOrder;

    private BigDecimal amount;

    private Unit unit;

    private BigDecimal calorie;

    public static ConnectMealFoodResponseDto from(PetDailyDietItem item) {
        return new ConnectMealFoodResponseDto(
                item.getId(),
                item.getPetFood().getId(),
                item.getPetFood().getName(),
                item.getMealOrder(),
                item.getAmount(),
                item.getUnit(),
                item.getCalorie()
        );
    }
}