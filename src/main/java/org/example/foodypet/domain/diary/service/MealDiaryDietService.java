package org.example.foodypet.domain.diary.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.S3Uploader;
import org.example.foodypet.domain.diary.dto.MealDiaryWriteFormResponse;
import org.example.foodypet.domain.diary.dto.PetMealDiaryCreateRequest;
import org.example.foodypet.domain.diary.entity.PetMealDiary;
import org.example.foodypet.domain.diary.entity.PetMealDiaryCapsule;
import org.example.foodypet.domain.diary.entity.PetMealDiarySymptom;
import org.example.foodypet.domain.diary.repository.PetMealDiaryCapsuleRepository;
import org.example.foodypet.domain.diary.repository.PetMealDiaryRepository;
import org.example.foodypet.domain.diary.repository.PetMealDiarySymptomRepository;
import org.example.foodypet.domain.diet.dto.DietRecommendSimpleResponse;
import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.example.foodypet.domain.diet.repository.PetDailyDietItemRepository;
import org.example.foodypet.domain.diet.repository.PetDailyDietRepository;
import org.example.foodypet.domain.food.entity.Unit;
import org.example.foodypet.domain.pet.entity.Pet;
import org.example.foodypet.domain.pet.entity.PetCapsule;
import org.example.foodypet.domain.pet.entity.PetMealSchedule;
import org.example.foodypet.domain.pet.repository.PetCapsuleRepository;
import org.example.foodypet.domain.pet.repository.PetMealScheduleRepository;
import org.example.foodypet.domain.pet.repository.PetRepository;
import org.example.foodypet.domain.water.entity.PetWaterIntake;
import org.example.foodypet.domain.water.entity.PetWaterIntakeItem;
import org.example.foodypet.domain.water.repository.PetWaterIntakeItemRepository;
import org.example.foodypet.domain.water.repository.PetWaterIntakeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealDiaryDietService {

    private final S3Uploader s3Uploader;

    private final PetRepository petRepository;

    private final PetDailyDietRepository petDailyDietRepository;
    private final PetDailyDietItemRepository petDailyDietItemRepository;

    private final PetMealScheduleRepository petMealScheduleRepository;
    private final PetCapsuleRepository petCapsuleRepository;

    private final PetMealDiaryRepository petMealDiaryRepository;
    private final PetMealDiarySymptomRepository petMealDiarySymptomRepository;
    private final PetMealDiaryCapsuleRepository petMealDiaryCapsuleRepository;

    private final PetWaterIntakeRepository petWaterIntakeRepository;
    private final PetWaterIntakeItemRepository petWaterIntakeItemRepository;

    public DietRecommendSimpleResponse getConfirmedDiet(Long petId, LocalDate date) {
        PetDailyDiet dailyDiet = petDailyDietRepository
                .findFirstByPetIdAndDietDateAndIsConfirmedTrueOrderByIdDesc(petId, date)
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

    public MealDiaryWriteFormResponse getMealDiaryWriteForm(Long petId, LocalDate date) {
        PetDailyDiet dailyDiet = petDailyDietRepository
                .findFirstByPetIdAndDietDateAndIsConfirmedTrueOrderByIdDesc(petId, date)
                .orElseThrow(() -> new IllegalArgumentException("불러올 수 있는 최종 등록 식단이 없습니다."));

        List<PetDailyDietItem> items =
                petDailyDietItemRepository.findByDailyDietIdOrderByMealOrderAscIdAsc(dailyDiet.getId());

        List<PetMealSchedule> schedules =
                petMealScheduleRepository.findByPetIdOrderByMealOrderAsc(petId);

        Map<Integer, PetMealSchedule> scheduleMap = schedules.stream()
                .collect(Collectors.toMap(
                        PetMealSchedule::getMealOrder,
                        schedule -> schedule
                ));

        Map<Integer, List<PetDailyDietItem>> groupedByMealOrder = items.stream()
                .collect(Collectors.groupingBy(PetDailyDietItem::getMealOrder));

        List<MealDiaryWriteFormResponse.MealDto> meals = groupedByMealOrder.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Integer mealOrder = entry.getKey();
                    List<PetDailyDietItem> mealItems = entry.getValue();

                    PetMealSchedule schedule = scheduleMap.get(mealOrder);

                    Long petMealScheduleId = schedule == null ? null : schedule.getId();

                    String mealTime = schedule == null
                            ? ""
                            : schedule.getMealTime().format(DateTimeFormatter.ofPattern("HH:mm"));

                    List<MealDiaryWriteFormResponse.FoodDto> foods = mealItems.stream()
                            .map(this::toWriteFormFoodDto)
                            .toList();

                    String description = foods.stream()
                            .map(MealDiaryWriteFormResponse.FoodDto::getDisplayText)
                            .collect(Collectors.joining(", "));

                    return MealDiaryWriteFormResponse.MealDto.builder()
                            .petMealScheduleId(petMealScheduleId)
                            .mealOrder(mealOrder)
                            .mealTime(mealTime)
                            .description(description)
                            .foods(foods)
                            .build();
                })
                .toList();

        List<MealDiaryWriteFormResponse.CapsuleDto> capsules = petCapsuleRepository.findByPetId(petId)
                .stream()
                .map(capsule -> MealDiaryWriteFormResponse.CapsuleDto.builder()
                        .petCapsuleId(capsule.getId())
                        .capsuleName(capsule.getCapsuleName())
                        .dailyCount(capsule.getCapsuleCount())
                        .build()
                )
                .toList();

        return MealDiaryWriteFormResponse.builder()
                .dailyDietId(dailyDiet.getId())
                .petId(dailyDiet.getPet().getId())
                .petName(dailyDiet.getPet().getPetName())
                .dietDate(dailyDiet.getDietDate())
                .meals(meals)
                .capsules(capsules)
                .build();
    }

    @Transactional
    public void createMealDiary(PetMealDiaryCreateRequest request, MultipartFile image) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다."));

        PetDailyDiet dailyDiet = petDailyDietRepository.findById(request.dailyDietId())
                .orElseThrow(() -> new IllegalArgumentException("식단 정보를 찾을 수 없습니다."));

        PetMealSchedule petMealSchedule = petMealScheduleRepository.findById(request.petMealScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("급여 일정을 찾을 수 없습니다."));

        validateDiaryTarget(pet, dailyDiet, petMealSchedule);

        validateDuplicateMealDiary(
                pet.getId(),
                dailyDiet.getId(),
                petMealSchedule.getId(),
                request.diaryDate()
        );

        String imageUrl = s3Uploader.uploadMealDiaryImage(image);

        PetMealDiary mealDiary = PetMealDiary.create(
                pet,
                dailyDiet,
                petMealSchedule,
                request.diaryDate(),
                imageUrl,
                request.satisfaction(),
                request.mealStatus(),
                request.waterIntakeMl(),
                request.memo()
        );

        PetMealDiary savedMealDiary = petMealDiaryRepository.save(mealDiary);

        saveSymptoms(request, savedMealDiary);
        saveCapsules(request, savedMealDiary, pet);
        saveWaterIntake(pet, request.diaryDate(), request.waterIntakeMl());
    }

    private void validateDuplicateMealDiary(
            Long petId,
            Long dailyDietId,
            Long petMealScheduleId,
            LocalDate diaryDate
    ) {
        boolean exists = petMealDiaryRepository
                .existsByPetIdAndDailyDietIdAndPetMealScheduleIdAndDiaryDate(
                        petId,
                        dailyDietId,
                        petMealScheduleId,
                        diaryDate
                );

        if (exists) {
            throw new IllegalArgumentException("이미 등록된 식단 급여 기록입니다.");
        }
    }

    private void validateDiaryTarget(
            Pet pet,
            PetDailyDiet dailyDiet,
            PetMealSchedule petMealSchedule
    ) {
        if (!dailyDiet.getPet().getId().equals(pet.getId())) {
            throw new IllegalArgumentException("해당 반려동물의 식단이 아닙니다.");
        }

        if (!petMealSchedule.getPet().getId().equals(pet.getId())) {
            throw new IllegalArgumentException("해당 반려동물의 급여 일정이 아닙니다.");
        }
    }

    private void saveSymptoms(
            PetMealDiaryCreateRequest request,
            PetMealDiary savedMealDiary
    ) {
        if (request.symptoms() == null || request.symptoms().isEmpty()) {
            return;
        }

        request.symptoms().forEach(symptom -> {
            PetMealDiarySymptom diarySymptom = PetMealDiarySymptom.create(
                    savedMealDiary,
                    symptom
            );

            petMealDiarySymptomRepository.save(diarySymptom);
        });
    }

    private void saveCapsules(
            PetMealDiaryCreateRequest request,
            PetMealDiary savedMealDiary,
            Pet pet
    ) {
        if (request.capsules() == null || request.capsules().isEmpty()) {
            return;
        }

        request.capsules().forEach(capsuleRequest -> {
            PetCapsule petCapsule = petCapsuleRepository.findById(capsuleRequest.petCapsuleId())
                    .orElseThrow(() -> new IllegalArgumentException("영양제 정보를 찾을 수 없습니다."));

            if (!petCapsule.getPet().getId().equals(pet.getId())) {
                throw new IllegalArgumentException("해당 반려동물의 영양제가 아닙니다.");
            }

            PetMealDiaryCapsule diaryCapsule = PetMealDiaryCapsule.create(
                    savedMealDiary,
                    petCapsule,
                    capsuleRequest.givenCount()
            );

            petMealDiaryCapsuleRepository.save(diaryCapsule);
        });
    }

    private void saveWaterIntake(
            Pet pet,
            LocalDate diaryDate,
            BigDecimal waterIntakeMl
    ) {
        if (waterIntakeMl == null || waterIntakeMl.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        PetWaterIntake waterIntake = petWaterIntakeRepository
                .findByPetAndIntakeDate(pet, diaryDate)
                .orElseGet(() -> petWaterIntakeRepository.save(
                        PetWaterIntake.create(pet, diaryDate)
                ));

        waterIntake.addAmount(waterIntakeMl);

        PetWaterIntakeItem waterIntakeItem = PetWaterIntakeItem.create(
                waterIntake,
                waterIntakeMl
        );

        petWaterIntakeItemRepository.save(waterIntakeItem);
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

    private MealDiaryWriteFormResponse.FoodDto toWriteFormFoodDto(PetDailyDietItem item) {
        String amount = formatAmount(item.getAmount());
        String unit = convertUnit(item.getUnit());

        String foodName = item.getPetFood().getName();
        String displayText = foodName + " " + amount + unit;

        return MealDiaryWriteFormResponse.FoodDto.builder()
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