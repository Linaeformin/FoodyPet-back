package org.example.foodypet.domain.food.dto;

import lombok.*;
import org.example.foodypet.domain.food.entity.Unit;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetFoodStockUpdateFormDto {

    private LocalDate expiredAt;
    private Boolean isTreat;
    private Unit unit;
    private int quantity;
}