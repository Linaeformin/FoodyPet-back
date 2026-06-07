package org.example.foodypet.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.diary.dto.PetTodayDiaryHomeResponse;
import org.example.foodypet.domain.diary.repository.PetMealDiaryRepository;
import org.example.foodypet.domain.diary.repository.PetTreatDiaryRepository;
import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.example.foodypet.domain.diet.repository.PetDailyDietItemRepository;
import org.example.foodypet.domain.diet.repository.PetDailyDietRepository;
import org.example.foodypet.domain.food.entity.Unit;
import org.example.foodypet.domain.pet.entity.Pet;
import org.example.foodypet.domain.pet.entity.PetCapsule;
import org.example.foodypet.domain.pet.entity.PetCapsuleIntake;
import org.example.foodypet.domain.pet.entity.PetMealSchedule;
import org.example.foodypet.domain.pet.repository.PetCapsuleIntakeRepository;
import org.example.foodypet.domain.pet.repository.PetCapsuleRepository;
import org.example.foodypet.domain.pet.repository.PetMealScheduleRepository;
import org.example.foodypet.domain.pet.repository.PetRepository;
import org.example.foodypet.domain.water.repository.PetWaterIntakeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetDiaryHomeService {

    private final PetRepository petRepository;

    private final PetDailyDietRepository petDailyDietRepository;
    private final PetDailyDietItemRepository petDailyDietItemRepository;
    private final PetMealScheduleRepository petMealScheduleRepository;

    private final PetMealDiaryRepository petMealDiaryRepository;
    private final PetWaterIntakeRepository petWaterIntakeRepository;
    private final PetTreatDiaryRepository petTreatDiaryRepository;

    private final PetCapsuleRepository petCapsuleRepository;
    private final PetCapsuleIntakeRepository petCapsuleIntakeRepository;

    public PetTodayDiaryHomeResponse getTodayDiaryHome(CustomUserDetails me) {
        LocalDate today = LocalDate.now();
        Long userId = me.getId();

        List<Pet> pets = petRepository.findByUser_IdOrderByIdAsc(userId);

        List<PetTodayDiaryHomeResponse.PetHome> petHomes = pets.stream()
                .map(pet -> createPetHome(pet, today))
                .toList();

        return new PetTodayDiaryHomeResponse(petHomes);
    }

    private PetTodayDiaryHomeResponse.PetHome createPetHome(
            Pet pet,
            LocalDate today
    ) {
        Long petId = pet.getId();

        return new PetTodayDiaryHomeResponse.PetHome(
                pet.getId(),
                pet.getPetName(),
                pet.getPetImg(),
                getTodayMeal(petId, today),
                getTodayDiary(petId, today)
        );
    }

    /**
     * 해당 펫의 오늘 남은 바로 식단
     *
     * 기준:
     * 1. 오늘 확정 식단 조회
     * 2. 그 식단에서 아직 식단 일기가 작성되지 않은 가장 빠른 mealSchedule 조회
     * 3. 해당 schedule의 mealOrder로 dailyDietItem 조회
     */
    private PetTodayDiaryHomeResponse.TodayMeal getTodayMeal(
            Long petId,
            LocalDate today
    ) {
        PetDailyDiet dailyDiet = petDailyDietRepository
                .findFirstByPet_IdAndDietDateAndIsConfirmedTrueOrderByIdDesc(
                        petId,
                        today
                )
                .orElse(null);

        if (dailyDiet == null) {
            return new PetTodayDiaryHomeResponse.TodayMeal(
                    null,
                    null,
                    null,
                    "",
                    List.of(),
                    false
            );
        }

        List<PetMealSchedule> schedules =
                petMealScheduleRepository.findOldestNotWrittenSchedule(
                        petId,
                        dailyDiet.getId(),
                        today,
                        PageRequest.of(0, 1)
                );

        if (schedules.isEmpty()) {
            return new PetTodayDiaryHomeResponse.TodayMeal(
                    dailyDiet.getId(),
                    null,
                    null,
                    "",
                    List.of(),
                    false
            );
        }

        PetMealSchedule schedule = schedules.get(0);

        List<PetTodayDiaryHomeResponse.TodayMealFood> foods =
                getTodayMealFoods(
                        dailyDiet.getId(),
                        schedule.getMealOrder()
                );

        String mealText = foods.stream()
                .map(PetTodayDiaryHomeResponse.TodayMealFood::getDisplayText)
                .collect(Collectors.joining(", "));

        return new PetTodayDiaryHomeResponse.TodayMeal(
                dailyDiet.getId(),
                schedule.getId(),
                schedule.getMealTime(),
                mealText,
                foods,
                true
        );
    }

    private List<PetTodayDiaryHomeResponse.TodayMealFood> getTodayMealFoods(
            Long dailyDietId,
            Integer mealOrder
    ) {
        List<PetDailyDietItem> items =
                petDailyDietItemRepository.findByDailyDiet_IdAndMealOrderOrderByIdAsc(
                        dailyDietId,
                        mealOrder
                );

        return items.stream()
                .map(item -> {
                    Long foodId = item.getPetFood().getId();
                    String foodName = item.getPetFood().getName();
                    BigDecimal amount = item.getAmount();
                    String unit = convertUnit(item.getUnit());

                    String displayText = foodName + " " + formatAmount(amount) + unit;

                    return new PetTodayDiaryHomeResponse.TodayMealFood(
                            foodId,
                            foodName,
                            amount,
                            unit,
                            displayText
                    );
                })
                .toList();
    }

    private PetTodayDiaryHomeResponse.TodayDiary getTodayDiary(
            Long petId,
            LocalDate today
    ) {
        return new PetTodayDiaryHomeResponse.TodayDiary(
                getMealDiarySummary(petId, today),
                getWaterDiarySummary(petId, today),
                getTreatDiarySummary(petId, today),
                getCapsuleDiarySummary(petId, today)
        );
    }

    /**
     * 밥 일기 카드
     *
     * targetCount = 해당 펫의 하루 급여 횟수
     * givenCount = 오늘 해당 펫의 식단 일기 작성 횟수
     */
    private PetTodayDiaryHomeResponse.MealDiary getMealDiarySummary(
            Long petId,
            LocalDate today
    ) {
        Integer targetCount = petMealScheduleRepository.countByPet_Id(petId);
        Integer givenCount = petMealDiaryRepository.countByPet_IdAndDiaryDate(
                petId,
                today
        );

        return new PetTodayDiaryHomeResponse.MealDiary(
                givenCount,
                targetCount,
                givenCount + "회 / " + targetCount + "회 급여",
                targetCount > 0 || givenCount > 0
        );
    }

    /**
     * 물 카드
     */
    private PetTodayDiaryHomeResponse.WaterDiary getWaterDiarySummary(
            Long petId,
            LocalDate today
    ) {
        return petWaterIntakeRepository.findByPet_IdAndIntakeDate(petId, today)
                .map(water -> new PetTodayDiaryHomeResponse.WaterDiary(
                        water.getId(),
                        water.getTotalAmountMl(),
                        formatAmount(water.getTotalAmountMl()) + "ml",
                        true
                ))
                .orElseGet(() -> new PetTodayDiaryHomeResponse.WaterDiary(
                        null,
                        BigDecimal.ZERO,
                        "0ml",
                        false
                ));
    }

    /**
     * 간식 카드
     *
     * givenCount = 오늘 해당 펫의 간식 기록 개수
     */
    private PetTodayDiaryHomeResponse.TreatDiary getTreatDiarySummary(
            Long petId,
            LocalDate today
    ) {
        Integer givenCount = petTreatDiaryRepository.countByPet_IdAndDiaryDate(
                petId,
                today
        );

        return new PetTodayDiaryHomeResponse.TreatDiary(
                givenCount,
                givenCount + "회",
                givenCount > 0
        );
    }

    /**
     * 영양제 카드
     *
     * targetCount = 해당 펫 영양제 목표 횟수 총합
     * givenCount = 오늘 해당 펫 영양제 급여 횟수 총합
     *
     * displayText는 target / given 순서
     */
    private PetTodayDiaryHomeResponse.CapsuleDiary getCapsuleDiarySummary(
            Long petId,
            LocalDate today
    ) {
        List<PetCapsule> capsules = petCapsuleRepository.findByPet_Id(petId);

        int targetCount = capsules.stream()
                .map(PetCapsule::getCapsuleCount)
                .filter(count -> count != null)
                .mapToInt(Integer::intValue)
                .sum();

        List<PetCapsuleIntake> intakes =
                petCapsuleIntakeRepository.findByPetCapsule_Pet_IdAndIntakeDate(
                        petId,
                        today
                );

        int givenCount = intakes.stream()
                .map(PetCapsuleIntake::getGivenCount)
                .filter(count -> count != null)
                .mapToInt(Integer::intValue)
                .sum();

        return new PetTodayDiaryHomeResponse.CapsuleDiary(
                givenCount,
                targetCount,
                targetCount + "회 / " + givenCount + "회 급여",
                targetCount > 0 || givenCount > 0
        );
    }

    private String convertUnit(Unit unit) {
        if (unit == null) {
            return "";
        }

        if (unit == Unit.GRAM) {
            return "g";
        }

        return unit.name();
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(amount);
    }
}