package org.example.foodypet.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ConnectMealPreviewResponseDto {

    private Long mealDiaryId;

    private Long dailyDietId;

    private String imageUrl;

    private List<ConnectMealFoodResponseDto> foods;
}