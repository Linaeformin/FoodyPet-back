package org.example.foodypet.domain.food.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.food.dto.PetFoodListResDto;
import org.example.foodypet.domain.food.dto.PetFoodSystemFormDto;
import org.example.foodypet.domain.food.entity.PetFood;
import org.example.foodypet.domain.food.entity.PetFoodStock;
import org.example.foodypet.domain.food.repository.PetFoodRepository;
import org.example.foodypet.domain.food.repository.PetFoodStockRepository;
import org.example.foodypet.domain.user.entity.User;
import org.example.foodypet.domain.user.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetFoodService {

    private final PetFoodRepository petFoodRepository;
    private final PetFoodStockRepository petFoodStockRepository;
    private final UsersRepository usersRepository;

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

    // 시스템에 등록된 식품을 내 재고로 등록
    @Transactional
    public void assignPetFood(CustomUserDetails me, PetFoodSystemFormDto dto) {

        User user = usersRepository.findById(me.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        PetFood petFood = petFoodRepository.findById(dto.getFoodId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식품입니다."));

        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }

        if (dto.getUnit() == null) {
            throw new IllegalArgumentException("단위는 필수입니다.");
        }

        PetFoodStock petFoodStock = PetFoodStock.create(
                user,
                petFood,
                BigDecimal.valueOf(dto.getQuantity()),
                dto.getUnit(),
                dto.getExpiredAt(),
                dto.getIsTreat()
        );

        petFoodStockRepository.save(petFoodStock);
    }
}