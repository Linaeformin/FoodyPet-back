package org.example.foodypet.domain.diary.dto;

import org.example.foodypet.domain.food.entity.PetFoodStock;
import org.example.foodypet.domain.food.entity.Unit;

public record TreatStockAutocompleteResponse(
        Long stockId,
        String foodName,
        Unit unit,
        String unitLabel
) {
    public static TreatStockAutocompleteResponse from(PetFoodStock stock) {
        return new TreatStockAutocompleteResponse(
                stock.getId(),
                stock.getPetFood().getName(),
                stock.getUnit(),
                toUnitLabel(stock.getUnit())
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