package org.example.foodypet.domain.diary.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.diary.dto.PetTodayDiaryHomeResponse;
import org.example.foodypet.domain.diary.entity.PetMealDiary;
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
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PetDiaryHomeService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

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
        LocalDate today = LocalDate.now(KOREA_ZONE);
        Long userId = me.getId();

        log.info("[홈 조회 시작] userId={}, today={}", userId, today);

        List<Pet> pets = petRepository.findByUser_IdOrderByIdAsc(userId);

        log.info("[홈 조회 반려동물] userId={}, petCount={}", userId, pets.size());

        List<PetTodayDiaryHomeResponse.PetHome> petHomes = pets.stream()
                .map(pet -> createPetHome(pet, today))
                .toList();

        log.info("[홈 조회 완료] userId={}, today={}", userId, today);

        return new PetTodayDiaryHomeResponse(petHomes);
    }

    private PetTodayDiaryHomeResponse.PetHome createPetHome(
            Pet pet,
            LocalDate today
    ) {
        Long petId = pet.getId();

        log.info(
                "[홈 펫 처리] petId={}, petName={}, today={}",
                petId,
                pet.getPetName(),
                today
        );

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
            log.info(
                    "[홈 오늘 식단] 오늘 확정 식단 없음 petId={}, today={}",
                    petId,
                    today
            );

            return new PetTodayDiaryHomeResponse.TodayMeal(
                    null,
                    null,
                    null,
                    "",
                    List.of(),
                    false
            );
        }

        log.info(
                "[홈 오늘 식단] dailyDietId={}, petId={}, dietDate={}",
                dailyDiet.getId(),
                petId,
                dailyDiet.getDietDate()
        );

        List<PetMealSchedule> schedules =
                petMealScheduleRepository.findOldestNotWrittenSchedule(
                        petId,
                        dailyDiet.getId(),
                        today,
                        PageRequest.of(0, 1)
                );

        if (schedules.isEmpty()) {
            log.info(
                    "[홈 오늘 식단] 미작성 급여 스케줄 없음 petId={}, dailyDietId={}, today={}",
                    petId,
                    dailyDiet.getId(),
                    today
            );

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

        log.info(
                "[홈 오늘 식단] 다음 식단 petId={}, dailyDietId={}, petMealScheduleId={}, mealOrder={}, mealTime={}",
                petId,
                dailyDiet.getId(),
                schedule.getId(),
                schedule.getMealOrder(),
                schedule.getMealTime()
        );

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

        log.info(
                "[홈 오늘 식단 음식] dailyDietId={}, mealOrder={}, foodCount={}",
                dailyDietId,
                mealOrder,
                items.size()
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
     *
     * count 메서드 따로 안 쓰고, 오늘 밥 일기 목록을 직접 조회해서 size로 계산함.
     * 이러면 실제 잡힌 row id, schedule id까지 로그로 확인 가능함.
     */
    private PetTodayDiaryHomeResponse.MealDiary getMealDiarySummary(
            Long petId,
            LocalDate today
    ) {
        Integer targetCount = petMealScheduleRepository.countByPet_Id(petId);

        List<PetMealDiary> todayMealDiaries =
                petMealDiaryRepository.findByPet_IdAndDiaryDateOrderByCreatedAtDesc(
                        petId,
                        today
                );

        Integer givenCount = todayMealDiaries.size();

        String diaryDebugText = todayMealDiaries.stream()
                .map(diary -> {
                    Long diaryId = diary.getId();
                    Long dailyDietId = diary.getDailyDiet() != null
                            ? diary.getDailyDiet().getId()
                            : null;
                    Long scheduleId = diary.getPetMealSchedule() != null
                            ? diary.getPetMealSchedule().getId()
                            : null;

                    return "{diaryId=" + diaryId
                            + ", dailyDietId=" + dailyDietId
                            + ", petMealScheduleId=" + scheduleId
                            + ", diaryDate=" + diary.getDiaryDate()
                            + "}";
                })
                .collect(Collectors.joining(", "));

        log.info(
                "[홈 밥일기 카운트] petId={}, today={}, givenCount={}, targetCount={}, diaries=[{}]",
                petId,
                today,
                givenCount,
                targetCount,
                diaryDebugText
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
                .map(water -> {
                    log.info(
                            "[홈 물 기록] petId={}, today={}, waterId={}, amount={}",
                            petId,
                            today,
                            water.getId(),
                            water.getTotalAmountMl()
                    );

                    return new PetTodayDiaryHomeResponse.WaterDiary(
                            water.getId(),
                            water.getTotalAmountMl(),
                            formatAmount(water.getTotalAmountMl()) + "ml",
                            true
                    );
                })
                .orElseGet(() -> {
                    log.info(
                            "[홈 물 기록] 기록 없음 petId={}, today={}",
                            petId,
                            today
                    );

                    return new PetTodayDiaryHomeResponse.WaterDiary(
                            null,
                            BigDecimal.ZERO,
                            "0ml",
                            false
                    );
                });
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

        log.info(
                "[홈 간식 카운트] petId={}, today={}, givenCount={}",
                petId,
                today,
                givenCount
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
     * displayText는 given / target 순서로 맞춤.
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

        String intakeDebugText = intakes.stream()
                .map(intake -> {
                    Long intakeId = intake.getId();
                    Long capsuleId = intake.getPetCapsule() != null
                            ? intake.getPetCapsule().getId()
                            : null;

                    return "{intakeId=" + intakeId
                            + ", petCapsuleId=" + capsuleId
                            + ", givenCount=" + intake.getGivenCount()
                            + ", intakeDate=" + intake.getIntakeDate()
                            + "}";
                })
                .collect(Collectors.joining(", "));

        log.info(
                "[홈 영양제 카운트] petId={}, today={}, givenCount={}, targetCount={}, intakes=[{}]",
                petId,
                today,
                givenCount,
                targetCount,
                intakeDebugText
        );

        return new PetTodayDiaryHomeResponse.CapsuleDiary(
                givenCount,
                targetCount,
                givenCount + "회 / " + targetCount + "회 급여",
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