package org.example.foodypet.domain.food.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.food.dto.PetFoodListResDto;
import org.example.foodypet.domain.food.entity.PetFood;
import org.example.foodypet.domain.food.repository.PetFoodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetFoodService {

    private final PetFoodRepository petFoodRepository;

    public PetFoodListResDto getPetFoodList() {
        List<PetFood> petFoods = petFoodRepository.findAll();

        List<PetFoodListResDto.PetFoodDto> foodDtos = petFoods.stream()
                .map(petFood -> PetFoodListResDto.PetFoodDto.builder()
                        .foodId(petFood.getId())
                        .foodName(petFood.getName())
                        .imageUrl(petFood.getFoodImg())
                        .nutritionImageUrl(petFood.getNutritionImg())
                        .foodSource(petFood.getSource())
                        .foodType(petFood.getFoodType())
                        .unit(petFood.getUnit())
                        .build())
                .toList();

        return PetFoodListResDto.builder()
                .foods(foodDtos)
                .build();
    }
}