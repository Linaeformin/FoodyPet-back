package org.example.foodypet.domain.food.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PetFoodAutocompleteResDto {

    private List<String> foodNames;
}