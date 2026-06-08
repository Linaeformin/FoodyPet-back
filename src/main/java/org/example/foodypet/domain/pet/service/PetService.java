package org.example.foodypet.domain.pet.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.pet.dto.PetAssignFormDto;
import org.example.foodypet.domain.pet.dto.PetCapsuleIntakeListResponseDto;
import org.example.foodypet.domain.pet.dto.PetCapsuleIntakeRequestDto;
import org.example.foodypet.domain.pet.entity.*;
import org.example.foodypet.domain.pet.repository.*;
import org.example.foodypet.domain.user.entity.User;
import org.example.foodypet.domain.user.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.foodypet.common.S3Uploader;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {

    private final UsersRepository userRepository;
    private final PetRepository petRepository;
    private final PetMealScheduleRepository petMealScheduleRepository;
    private final PetCapsuleRepository petCapsuleRepository;
    private final PetNutritionStandardRepository petNutritionStandardRepository;
    private final PetCapsuleIntakeRepository petCapsuleIntakeRepository;
    private final S3Uploader s3Uploader;

    public void assignPet(Long userId, PetAssignFormDto petAssignFormDto, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String imageUrl = s3Uploader.uploadPetImage(image);

        PetAssignFormDto.PetInfoDto petInfo = petAssignFormDto.getPetInfo();

        validateBreedByPetType(petInfo);
        validateMealInfo(petAssignFormDto.getMealInfo());

        boolean isFirstPet = !petRepository.existsByUserId(userId);

        Pet pet = new Pet();
        pet.setPetName(petInfo.getName());
        pet.setBirth(petInfo.getBirthDate());
        pet.setWeightKg(petInfo.getWeightKg());
        pet.setPetImg(imageUrl);
        pet.setPetType(petInfo.getPetType());
        pet.setDogBreed(petInfo.getDogBreed());
        pet.setCatBreed(petInfo.getCatBreed());
        pet.setPetGender(petInfo.getGender());
        pet.setNeuteredStatus(petInfo.getNeuteredStatus());
        pet.setUser(user);

        petRepository.save(pet);

        if (isFirstPet) {
            user.updateUserImg(imageUrl);
        }

        saveMealSchedules(pet, petAssignFormDto.getMealInfo());
        saveSupplements(pet, petAssignFormDto.getSupplements());
        saveNutritionStandard(pet);
    }

    private void validateBreedByPetType(PetAssignFormDto.PetInfoDto petInfo) {
        if (petInfo.getPetType() == PetType.DOG) {
            if (petInfo.getDogBreed() == null) {
                throw new IllegalArgumentException("강아지는 강아지 품종이 필수입니다.");
            }

            if (petInfo.getCatBreed() != null) {
                throw new IllegalArgumentException("강아지 등록 시 고양이 품종은 입력할 수 없습니다.");
            }
        }

        if (petInfo.getPetType() == PetType.CAT) {
            if (petInfo.getCatBreed() == null) {
                throw new IllegalArgumentException("고양이는 고양이 품종이 필수입니다.");
            }

            if (petInfo.getDogBreed() != null) {
                throw new IllegalArgumentException("고양이 등록 시 강아지 품종은 입력할 수 없습니다.");
            }
        }
    }

    private void validateMealInfo(PetAssignFormDto.MealInfoDto mealInfo) {
        int mealCount = mealInfo.getMealCount();
        int mealTimeSize = mealInfo.getMealTimes().size();

        if (mealCount != mealTimeSize) {
            throw new IllegalArgumentException("급여 횟수와 급여 시각 개수가 일치하지 않습니다.");
        }

        boolean hasDuplicateOrder = mealInfo.getMealTimes().stream()
                .map(PetAssignFormDto.MealTimeDto::getSequence)
                .distinct()
                .count() != mealTimeSize;

        if (hasDuplicateOrder) {
            throw new IllegalArgumentException("급여 순서가 중복되었습니다.");
        }

        boolean hasDuplicateTime = mealInfo.getMealTimes().stream()
                .map(PetAssignFormDto.MealTimeDto::getTime)
                .distinct()
                .count() != mealTimeSize;

        if (hasDuplicateTime) {
            throw new IllegalArgumentException("급여 시각이 중복되었습니다.");
        }
    }

    private void saveMealSchedules(Pet pet, PetAssignFormDto.MealInfoDto mealInfo) {
        List<PetMealSchedule> mealSchedules = mealInfo.getMealTimes().stream()
                .map(mealTimeDto -> {
                    PetMealSchedule schedule = new PetMealSchedule();
                    schedule.setPet(pet);
                    schedule.setMealOrder(mealTimeDto.getSequence());
                    schedule.setMealTime(mealTimeDto.getTime());
                    return schedule;
                })
                .toList();

        petMealScheduleRepository.saveAll(mealSchedules);
    }

    private void saveSupplements(Pet pet, List<PetAssignFormDto.SupplementInfoDto> supplements) {
        if (supplements == null || supplements.isEmpty()) {
            return;
        }

        List<PetCapsule> capsules = supplements.stream()
                .map(supplement -> {
                    PetCapsule capsule = new PetCapsule();
                    capsule.setPet(pet);
                    capsule.setCapsuleName(supplement.getName());
                    capsule.setCapsuleCount(supplement.getCountPerDay());
                    return capsule;
                })
                .toList();

        petCapsuleRepository.saveAll(capsules);
    }

    private void saveNutritionStandard(Pet pet) {
        NutritionResult nutritionResult = calculateNutritionStandard(pet);

        PetNutritionStandard standard = new PetNutritionStandard();
        standard.setPet(pet);
        standard.setRecommendedCalorie(nutritionResult.recommendedCalorie());
        standard.setRecommendedProtein(nutritionResult.recommendedProtein());
        standard.setRecommendedFat(nutritionResult.recommendedFat());
        standard.setRecommendedAsh(nutritionResult.recommendedAsh());
        standard.setRecommendedFiber(nutritionResult.recommendedFiber());

        petNutritionStandardRepository.save(standard);
    }

    private NutritionResult calculateNutritionStandard(Pet pet) {
        BigDecimal calorie = calculateRecommendedCalorie(
                pet.getPetType(),
                pet.getNeuteredStatus(),
                pet.getWeightKg()
        );

        /*
         * 단위:
         * recommendedCalorie = kcal/day
         * recommendedProtein = g/day
         * recommendedFat = g/day
         * recommendedAsh = g/day
         * recommendedFiber = g/day
         *
         * 기준:
         * - FEDIAF Nutritional Guidelines:
         *   반려견/반려묘 영양소 기준을 1000kcal ME 단위로 제시함.
         *
         * - MSD Veterinary Manual / AAFCO:
         *   성견 단백질 최소 기준: 45g / 1000kcal ME
         *   성묘 단백질 최소 기준: 65g / 1000kcal ME
         *
         * 주의:
         * 이 값은 등록 초기 기본 권장량임.
         * 실제 급여량은 나이, 활동량, 건강 상태, 비만도, 임신 여부 등에 따라 달라질 수 있음.
         */

        BigDecimal proteinPer1000Kcal;
        BigDecimal fatPer1000Kcal;
        BigDecimal ashPer1000Kcal;
        BigDecimal fiberPer1000Kcal;

        if (pet.getPetType() == PetType.DOG) {
            proteinPer1000Kcal = BigDecimal.valueOf(45.0);
            fatPer1000Kcal = BigDecimal.valueOf(13.75);
            ashPer1000Kcal = BigDecimal.valueOf(20.0);
            fiberPer1000Kcal = BigDecimal.valueOf(25.0);
        } else {
            proteinPer1000Kcal = BigDecimal.valueOf(65.0);
            fatPer1000Kcal = BigDecimal.valueOf(22.5);
            ashPer1000Kcal = BigDecimal.valueOf(20.0);
            fiberPer1000Kcal = BigDecimal.valueOf(25.0);
        }

        BigDecimal calorieUnit = calorie.divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);

        BigDecimal protein = proteinPer1000Kcal.multiply(calorieUnit)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal fat = fatPer1000Kcal.multiply(calorieUnit)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal ash = ashPer1000Kcal.multiply(calorieUnit)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal fiber = fiberPer1000Kcal.multiply(calorieUnit)
                .setScale(2, RoundingMode.HALF_UP);

        return new NutritionResult(
                calorie.setScale(2, RoundingMode.HALF_UP),
                protein,
                fat,
                ash,
                fiber
        );
    }

    private BigDecimal calculateRecommendedCalorie(
            PetType petType,
            NeuteredStatus neuteredStatus,
            BigDecimal weightKg
    ) {
        double weight = weightKg.doubleValue();

        if (petType == PetType.DOG) {
            double factor = neuteredStatus == NeuteredStatus.NEUTERED ? 95.0 : 110.0;
            double calorie = factor * Math.pow(weight, 0.75);
            return BigDecimal.valueOf(calorie);
        }

        double factor = neuteredStatus == NeuteredStatus.NEUTERED ? 75.0 : 100.0;
        double calorie = factor * Math.pow(weight, 0.67);
        return BigDecimal.valueOf(calorie);
    }

    private record NutritionResult(
            BigDecimal recommendedCalorie,
            BigDecimal recommendedProtein,
            BigDecimal recommendedFat,
            BigDecimal recommendedAsh,
            BigDecimal recommendedFiber
    ) {
    }

    public void updateTodayCapsuleIntakes(Long userId, Long petId, PetCapsuleIntakeRequestDto requestDto) {
        getMyPet(userId, petId);

        LocalDate today = LocalDate.now();

        for (PetCapsuleIntakeRequestDto.CapsuleIntakeItem item : requestDto.getCapsuleIntakes()) {
            PetCapsule petCapsule = petCapsuleRepository.findByIdAndPetId(
                            item.getPetCapsuleId(),
                            petId
                    )
                    .orElseThrow(() -> new IllegalArgumentException("해당 영양제를 찾을 수 없습니다."));

            petCapsuleIntakeRepository
                    .findByPetCapsuleIdAndIntakeDate(item.getPetCapsuleId(), today)
                    .ifPresentOrElse(
                            intake -> intake.updateGivenCount(item.getGivenCount()),
                            () -> petCapsuleIntakeRepository.save(
                                    PetCapsuleIntake.create(
                                            petCapsule,
                                            today,
                                            item.getGivenCount()
                                    )
                            )
                    );
        }
    }

    @Transactional(readOnly = true)
    public PetCapsuleIntakeListResponseDto getTodayCapsuleIntakes(Long userId, Long petId) {
        Pet pet = getMyPet(userId, petId);

        LocalDate today = LocalDate.now();

        List<PetCapsule> petCapsules = petCapsuleRepository.findAllByPetIdOrderByIdAsc(pet.getId());

        List<PetCapsuleIntake> todayIntakes =
                petCapsuleIntakeRepository.findAllByPetCapsulePetIdAndIntakeDate(pet.getId(), today);

        Map<Long, PetCapsuleIntake> intakeMap = todayIntakes.stream()
                .collect(Collectors.toMap(
                        intake -> intake.getPetCapsule().getId(),
                        intake -> intake
                ));

        return PetCapsuleIntakeListResponseDto.of(
                pet.getId(),
                petCapsules,
                intakeMap
        );
    }

    private Pet getMyPet(Long userId, Long petId) {
        return petRepository.findByIdAndUserId(petId, userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려동물을 찾을 수 없습니다."));
    }
}