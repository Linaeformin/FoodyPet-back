package org.example.foodypet.domain.food.init;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.food.repository.PetFoodRepository;
import org.example.foodypet.domain.food.service.PetFoodAutocompleteRedisService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetFoodAutocompleteInitializer implements ApplicationRunner {

    private final PetFoodRepository petFoodRepository;
    private final PetFoodAutocompleteRedisService petFoodAutocompleteRedisService;

    @Override
    public void run(ApplicationArguments args) {
        petFoodAutocompleteRedisService.clear();

        petFoodRepository.findAll()
                .forEach(petFoodAutocompleteRedisService::savePetFood);
    }
}