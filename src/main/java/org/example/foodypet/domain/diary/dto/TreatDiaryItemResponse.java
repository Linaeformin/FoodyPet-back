package org.example.foodypet.domain.diary.dto;

import org.example.foodypet.domain.diary.entity.PetTreatDiaryItem;
import org.example.foodypet.domain.food.entity.Unit;

import java.math.BigDecimal;

public record TreatDiaryItemResponse(
        String foodName,
        BigDecimal amount,
        Unit unit,
        String unitLabel
) {
    public static TreatDiaryItemResponse from(PetTreatDiaryItem item) {
        return new TreatDiaryItemResponse(
                item.getPetFood().getName(),
                item.getAmount(),
                item.getUnit(),
                toUnitLabel(item.getUnit())
        );
    }

    private static String toUnitLabel(Unit unit) {
        return switch (unit) {
            case GRAM -> "g";
            case ML -> "ml";
            case COUNT -> "개";
            case BAG -> "봉";
        };
    }
}