package org.example.foodypet.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.foodypet.domain.food.entity.Unit;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CommunityPostMealResponseDto {

    private String foodName;

    private BigDecimal amount;

    private Unit unit;
}