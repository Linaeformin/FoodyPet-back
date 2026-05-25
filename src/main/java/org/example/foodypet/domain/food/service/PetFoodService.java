package org.example.foodypet.domain.food.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.food.dto.*;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Transactional
    public void updatePetFoodStockQuantities(
            CustomUserDetails me,
            PetFoodStockUpdateReqDto dto
    ) {
        if (dto.getStocks() == null || dto.getStocks().isEmpty()) {
            throw new IllegalArgumentException("수정할 재고 목록이 비어 있습니다.");
        }

        for (PetFoodStockUpdateReqDto.StockQuantityDto stockDto : dto.getStocks()) {
            if (stockDto.getStockId() == null) {
                throw new IllegalArgumentException("재고 ID는 필수입니다.");
            }

            if (stockDto.getQuantity() == null) {
                throw new IllegalArgumentException("수량은 필수입니다.");
            }

            if (stockDto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
            }
        }

        List<Long> stockIds = dto.getStocks().stream()
                .map(PetFoodStockUpdateReqDto.StockQuantityDto::getStockId)
                .distinct()
                .toList();

        List<PetFoodStock> stocks = petFoodStockRepository.findByIdInAndUserId(
                stockIds,
                me.getId()
        );

        if (stocks.size() != stockIds.size()) {
            throw new IllegalArgumentException("존재하지 않거나 수정 권한이 없는 재고가 포함되어 있습니다.");
        }

        Map<Long, PetFoodStock> stockMap = stocks.stream()
                .collect(Collectors.toMap(PetFoodStock::getId, Function.identity()));

        for (PetFoodStockUpdateReqDto.StockQuantityDto stockDto : dto.getStocks()) {
            PetFoodStock stock = stockMap.get(stockDto.getStockId());
            stock.updateQuantity(stockDto.getQuantity());
        }
    }

    @Transactional
    public void updatePetFoodStock(
            CustomUserDetails me,
            Long stockId,
            PetFoodStockUpdateFormDto dto
    ) {
        if (stockId == null) {
            throw new IllegalArgumentException("재고 ID는 필수입니다.");
        }

        if (dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }

        if (dto.getUnit() == null) {
            throw new IllegalArgumentException("단위는 필수입니다.");
        }

        PetFoodStock stock = petFoodStockRepository.findByIdAndUserId(
                stockId,
                me.getId()
        ).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않거나 수정 권한이 없는 재고입니다.")
        );

        stock.update(
                BigDecimal.valueOf(dto.getQuantity()),
                dto.getUnit(),
                dto.getExpiredAt(),
                dto.getIsTreat()
        );
    }

    @Transactional
    public void deletePetFoodStocks(
            CustomUserDetails me,
            PetFoodStockDeleteReqDto dto
    ) {
        if (dto.getStockIds() == null || dto.getStockIds().isEmpty()) {
            throw new IllegalArgumentException("삭제할 재고 목록이 비어 있습니다.");
        }

        List<Long> stockIds = dto.getStockIds().stream()
                .distinct()
                .toList();

        List<PetFoodStock> stocks = petFoodStockRepository.findByIdInAndUserId(
                stockIds,
                me.getId()
        );

        if (stocks.size() != stockIds.size()) {
            throw new IllegalArgumentException("존재하지 않거나 삭제 권한이 없는 재고가 포함되어 있습니다.");
        }

        petFoodStockRepository.deleteAll(stocks);
    }
}