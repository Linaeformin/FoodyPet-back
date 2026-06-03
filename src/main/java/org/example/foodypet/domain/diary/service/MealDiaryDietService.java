package org.example.foodypet.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.diet.dto.DietRecommendSimpleResponse;
import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.example.foodypet.domain.diet.repository.PetDailyDietItemRepository;
import org.example.foodypet.domain.diet.repository.PetDailyDietRepository;
import org.example.foodypet.domain.food.entity.Unit;
import org.example.foodypet.domain.pet.entity.PetMealSchedule;
import org.example.foodypet.domain.pet.repository.PetMealScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.foodypet.domain.food.entity.Unit.GRAM;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealDiaryDietService {

    private final PetDailyDietRepository petDailyDietRepository;
    private final PetDailyDietItemRepository petDailyDietItemRepository;
    private final PetMealScheduleRepository petMealScheduleRepository;

    public DietRecommendSimpleResponse getConfirmedDiet(Long petId, LocalDate date) {

        PetDailyDiet dailyDiet = petDailyDietRepository
                .findAvailableConfirmedDiet(petId, date)
                .orElseThrow(() -> new IllegalArgumentException("불러올 수 있는 최종 등록 식단이 없습니다."));

        List<PetDailyDietItem> items =
                petDailyDietItemRepository.findByDailyDietIdOrderByMealOrderAscIdAsc(dailyDiet.getId());

        List<PetMealSchedule> schedules =
                petMealScheduleRepository.findByPetIdOrderByMealOrderAsc(petId);

        Map<Integer, String> mealTimeMap = schedules.stream()
                .collect(Collectors.toMap(
                        PetMealSchedule::getMealOrder,
                        schedule -> schedule.getMealTime()
                                .format(DateTimeFormatter.ofPattern("HH:mm"))
                ));

        Map<Integer, List<PetDailyDietItem>> groupedByMealOrder = items.stream()
                .collect(Collectors.groupingBy(PetDailyDietItem::getMealOrder));

        List<DietRecommendSimpleResponse.MealDto> meals = groupedByMealOrder.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Integer mealOrder = entry.getKey();
                    List<PetDailyDietItem> mealItems = entry.getValue();

                    String mealTime = mealTimeMap.getOrDefault(mealOrder, "");

                    List<DietRecommendSimpleResponse.FoodDto> foods = mealItems.stream()
                            .map(this::toFoodDto)
                            .toList();

                    String description = foods.stream()
                            .map(DietRecommendSimpleResponse.FoodDto::getDisplayText)
                            .collect(Collectors.joining(", "));

                    return DietRecommendSimpleResponse.MealDto.builder()
                            .mealOrder(mealOrder)
                            .mealTime(mealTime)
                            .description(description)
                            .foods(foods)
                            .build();
                })
                .toList();

        return DietRecommendSimpleResponse.builder()
                .dailyDietId(dailyDiet.getId())
                .petId(dailyDiet.getPet().getId())
                .petName(dailyDiet.getPet().getPetName())
                .dietDate(dailyDiet.getDietDate())
                .meals(meals)
                .build();
    }

    private DietRecommendSimpleResponse.FoodDto toFoodDto(PetDailyDietItem item) {
        String amount = formatAmount(item.getAmount());
        String unit = convertUnit(item.getUnit());

        String foodName = item.getPetFood().getName();
        String displayText = foodName + " " + amount + unit;

        return DietRecommendSimpleResponse.FoodDto.builder()
                .foodId(item.getPetFood().getId())
                .foodName(foodName)
                .amount(amount)
                .unit(unit)
                .displayText(displayText)
                .build();
    }

    private String convertUnit(Unit unit) {
        if (unit == null) {
            return "";
        }

        return switch (unit) {
            case GRAM -> "g";
            default -> unit.name().toLowerCase();
        };
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }

        return amount.stripTrailingZeros().toPlainString();
    }
}