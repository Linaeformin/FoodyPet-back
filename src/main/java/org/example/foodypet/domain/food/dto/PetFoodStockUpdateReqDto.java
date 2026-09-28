package org.example.foodypet.domain.food.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class PetFoodStockUpdateReqDto {

    private List<StockQuantityDto> stocks;

    @Getter
    @NoArgsConstructor
    public static class StockQuantityDto {
        private Long stockId;
        private BigDecimal quantity;
    }
}