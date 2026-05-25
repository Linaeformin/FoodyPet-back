package org.example.foodypet.domain.food.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.food.dto.PetFoodListResDto;
import org.example.foodypet.domain.food.dto.PetFoodStockListResDto;
import org.example.foodypet.domain.food.dto.PetFoodStockResDto;
import org.example.foodypet.domain.food.dto.PetFoodSystemFormDto;
import org.example.foodypet.domain.food.entity.FoodType;
import org.example.foodypet.domain.food.entity.PetFood;
import org.example.foodypet.domain.food.entity.PetFoodStock;
import org.example.foodypet.domain.food.entity.PetFoodStockSortType;
import org.example.foodypet.domain.food.repository.PetFoodRepository;
import org.example.foodypet.domain.food.repository.PetFoodStockRepository;
import org.example.foodypet.domain.user.entity.User;
import org.example.foodypet.domain.user.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
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

    // 재고 관리 화면 조회
    public PetFoodStockListResDto getPetFoodStocks(
            CustomUserDetails me,
            FoodType foodType,
            Boolean treat,
            PetFoodStockSortType sort
    ) {
        List<PetFoodStock> stocks = petFoodStockRepository.findByUserId(me.getId());

        List<PetFoodStockResDto> filteredStocks = stocks.stream()
                .filter(stock -> filterByTab(stock, foodType, treat))
                .map(this::toPetFoodStockResDto)
                .sorted(getStockComparator(sort))
                .toList();

        List<PetFoodStockResDto> availableStocks = filteredStocks.stream()
                .filter(stock -> !stock.getExpired())
                .toList();

        List<PetFoodStockResDto> expiredStocks = filteredStocks.stream()
                .filter(PetFoodStockResDto::getExpired)
                .toList();

        return PetFoodStockListResDto.builder()
                .availableStocks(availableStocks)
                .expiredStocks(expiredStocks)
                .build();
    }

    private boolean filterByTab(
            PetFoodStock stock,
            FoodType foodType,
            Boolean treat
    ) {
        PetFood petFood = stock.getPetFood();

        // 간식 탭: 화식/습식/건식/생식 구별 없이 isTreat=true인 것만 보여줌
        if (Boolean.TRUE.equals(treat)) {
            return Boolean.TRUE.equals(stock.getIsTreat());
        }

        // foodType 없으면 전체 일반 음식
        if (foodType == null) {
            return true;
        }

        return petFood.getFoodType() == foodType;
    }

    private PetFoodStockResDto toPetFoodStockResDto(PetFoodStock stock) {
        PetFood petFood = stock.getPetFood();

        LocalDate today = LocalDate.now();
        LocalDate expiredAt = stock.getExpiredAt();

        boolean expired = expiredAt != null && expiredAt.isBefore(today);

        return PetFoodStockResDto.builder()
                .stockId(stock.getId())
                .petFoodId(petFood.getId())
                .foodName(petFood.getName())
                .foodImg(petFood.getFoodImg())
                .nutritionImg(petFood.getNutritionImg())
                .foodType(petFood.getFoodType())
                .source(petFood.getSource())
                .quantity(stock.getQuantity())
                .unit(stock.getUnit())
                .expiredAt(expiredAt)
                .isTreat(stock.getIsTreat())
                .expired(expired)
                .build();
    }

    private Comparator<PetFoodStockResDto> getStockComparator(PetFoodStockSortType sort) {
        if (sort == null) {
            sort = PetFoodStockSortType.CREATED;
        }

        return switch (sort) {
            case NAME -> Comparator.comparing(
                    PetFoodStockResDto::getFoodName,
                    Comparator.nullsLast(String::compareTo)
            );

            case EXPIRED -> Comparator.comparing(
                    PetFoodStockResDto::getExpiredAt,
                    Comparator.nullsLast(LocalDate::compareTo)
            );

            case CREATED -> Comparator.comparing(
                    PetFoodStockResDto::getStockId,
                    Comparator.nullsLast(Long::compareTo)
            );
        };
    }
}