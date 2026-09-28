package org.example.foodypet.domain.food.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetFoodStockListResDto {

    private List<PetFoodStockResDto> availableStocks;

    private List<PetFoodStockResDto> expiredStocks;
}