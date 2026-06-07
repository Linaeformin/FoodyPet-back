package org.example.foodypet.domain.diary.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.example.foodypet.domain.food.entity.Unit;

import java.math.BigDecimal;

@Getter
@Builder
public class MealDiaryFoodResponse {

    private Long foodId;
    private String foodName;
    private BigDecimal amount;
    private Unit unit;
    private String displayText;

    public static MealDiaryFoodResponse from(PetDailyDietItem item) {
        String displayText = item.getPetFood().getName()
                + " "
                + formatAmount(item.getAmount())
                + item.getUnit().name();

        return MealDiaryFoodResponse.builder()
                .foodId(item.getPetFood().getId())
                .foodName(item.getPetFood().getName())
                .amount(item.getAmount())
                .unit(item.getUnit())
                .displayText(displayText)
                .build();
    }

    private static String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }

        return amount.stripTrailingZeros().toPlainString();
    }
}