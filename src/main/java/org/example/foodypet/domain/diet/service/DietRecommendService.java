package org.example.foodypet.domain.diet.service;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.diet.dto.AnalysisStatus;
import org.example.foodypet.domain.diet.dto.DietRecommendRequest;
import org.example.foodypet.domain.diet.dto.DietRecommendResponse;
import org.example.foodypet.domain.diet.dto.DietRecommendSimpleResponse;
import org.example.foodypet.domain.diet.entity.PetDailyDiet;
import org.example.foodypet.domain.diet.entity.PetDailyDietItem;
import org.example.foodypet.domain.diet.repository.PetDailyDietItemRepository;
import org.example.foodypet.domain.diet.repository.PetDailyDietRepository;
import org.example.foodypet.domain.food.entity.FoodSource;
import org.example.foodypet.domain.food.entity.PetFood;
import org.example.foodypet.domain.food.entity.PetFoodStock;
import org.example.foodypet.domain.food.entity.Unit;
import org.example.foodypet.domain.food.repository.PetFoodStockRepository;
import org.example.foodypet.domain.pet.entity.Pet;
import org.example.foodypet.domain.pet.entity.PetMealSchedule;
import org.example.foodypet.domain.pet.entity.PetNutritionStandard;
import org.example.foodypet.domain.pet.repository.PetMealScheduleRepository;
import org.example.foodypet.domain.pet.repository.PetNutritionStandardRepository;
import org.example.foodypet.domain.pet.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DietRecommendService {

    private final PetRepository petRepository;
    private final PetNutritionStandardRepository nutritionStandardRepository;
    private final PetMealScheduleRepository mealScheduleRepository;
    private final PetFoodStockRepository petFoodStockRepository;
    private final PetDailyDietRepository petDailyDietRepository;
    private final PetDailyDietItemRepository petDailyDietItemRepository;

    private static final BigDecimal FINAL_REGISTER_FOOD_LIKE_DECREASE = new BigDecimal("5.0");

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private static final int MAX_FOOD_COUNT_PER_MEAL = 8;
    private static final int CANDIDATE_LIMIT = 30;
    private static final int TOP_MEAL_RESULT_LIMIT = 50;

    private static final BigDecimal CALORIE_WEIGHT = new BigDecimal("5.0");
    private static final BigDecimal PROTEIN_WEIGHT = new BigDecimal("6.0");
    private static final BigDecimal FAT_WEIGHT = new BigDecimal("6.0");
    private static final BigDecimal ASH_WEIGHT = new BigDecimal("3.0");
    private static final BigDecimal FIBER_WEIGHT = new BigDecimal("4.0");

    private static final BigDecimal CALCIUM_PHOSPHORUS_TARGET_RATIO = new BigDecimal("1.20");
    private static final BigDecimal CALCIUM_PHOSPHORUS_RATIO_WEIGHT = new BigDecimal("4.0");

    private static final BigDecimal TAURINE_MISSING_PENALTY = new BigDecimal("1.0");
    private static final BigDecimal FOOD_LIKE_WEIGHT = new BigDecimal("1.5");
    private static final BigDecimal REPEAT_FOOD_PENALTY = new BigDecimal("0.8000");
    private static final BigDecimal SIMILAR_SCORE_RANGE = new BigDecimal("0.0500");

    public DietRecommendSimpleResponse recommendAndSaveDiet(Long userId, DietRecommendRequest request) {
        Pet pet = petRepository.findByIdAndUserId(request.getPetId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("반려동물을 찾을 수 없습니다."));

        LocalDate dietDate = LocalDate.now();

        boolean alreadyConfirmed = petDailyDietRepository
                .existsByPet_IdAndDietDateAndIsConfirmedTrue(pet.getId(), dietDate);

        if (alreadyConfirmed) {
            throw new IllegalArgumentException("이미 최종 등록된 식단이 있어 추천을 다시 받을 수 없습니다.");
        }

        PetNutritionStandard standard = nutritionStandardRepository.findByPetId(pet.getId())
                .orElseThrow(() -> new IllegalArgumentException("반려동물 영양 기준표가 없습니다."));

        List<PetMealSchedule> schedules =
                mealScheduleRepository.findByPetIdOrderByMealOrderAsc(pet.getId());

        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("등록된 급여 시간이 없습니다.");
        }

        int mealCount = schedules.size();
        int maxFoodCountPerMeal = MAX_FOOD_COUNT_PER_MEAL;

        List<PetFoodStock> stocks = petFoodStockRepository
                .findByUserIdAndPetFoodPetType(userId, pet.getPetType())
                .stream()
                .filter(stock -> Boolean.FALSE.equals(stock.getIsTreat()))
                .filter(stock -> stock.getQuantity().compareTo(ZERO) > 0)
                .filter(stock -> stock.getExpiredAt() == null || !stock.getExpiredAt().isBefore(LocalDate.now()))
                .toList();

        if (stocks.isEmpty()) {
            throw new IllegalArgumentException("추천에 사용할 수 있는 재고 식품이 없습니다.");
        }

        MealTarget targetPerMeal = MealTarget.of(standard, mealCount);

        List<RecommendedMeal> recommendedMeals = new ArrayList<>();
        NutritionSum dailyTotal = NutritionSum.empty();

        List<Long> usedFoodIdsToday = new ArrayList<>();

        for (PetMealSchedule schedule : schedules) {
            RecommendedMeal recommendedMeal = findBestMealCombination(
                    stocks,
                    targetPerMeal,
                    maxFoodCountPerMeal,
                    schedule.getMealOrder(),
                    schedule.getMealTime(),
                    usedFoodIdsToday
            );

            recommendedMeals.add(recommendedMeal);
            dailyTotal = dailyTotal.add(recommendedMeal.nutritionSum());

            recommendedMeal.foods().forEach(food ->
                    usedFoodIdsToday.add(food.petFood().getId())
            );
        }

        BigDecimal calciumPhosphorusRatio = calculateCalciumPhosphorusRatio(
                dailyTotal.calcium(),
                dailyTotal.phosphorus()
        );

        PetDailyDiet savedDiet = petDailyDietRepository
                .findByPet_IdAndDietDate(pet.getId(), dietDate)
                .orElse(null);

        if (savedDiet != null) {
            petDailyDietItemRepository.deleteByDailyDiet_Id(savedDiet.getId());

            savedDiet.updateRecommendedDiet(
                    FoodSource.SYSTEM,
                    dailyTotal.calorie(),
                    dailyTotal.protein(),
                    dailyTotal.fat(),
                    dailyTotal.ash(),
                    dailyTotal.fiber(),
                    dailyTotal.calcium(),
                    dailyTotal.phosphorus(),
                    dailyTotal.taurine(),
                    calciumPhosphorusRatio
            );
        } else {
            savedDiet = PetDailyDiet.createRecommendedDiet(
                    pet,
                    dietDate,
                    FoodSource.SYSTEM,
                    dailyTotal.calorie(),
                    dailyTotal.protein(),
                    dailyTotal.fat(),
                    dailyTotal.ash(),
                    dailyTotal.fiber(),
                    dailyTotal.calcium(),
                    dailyTotal.phosphorus(),
                    dailyTotal.taurine(),
                    calciumPhosphorusRatio
            );

            petDailyDietRepository.save(savedDiet);
        }

        for (RecommendedMeal meal : recommendedMeals) {
            for (CalculatedFood food : meal.foods()) {
                PetDailyDietItem item = PetDailyDietItem.create(
                        savedDiet,
                        food.petFood(),
                        meal.mealOrder(),
                        food.amount(),
                        food.unit(),
                        food.calorie(),
                        food.protein(),
                        food.fat(),
                        food.ash(),
                        food.fiber(),
                        food.calcium(),
                        food.phosphorus(),
                        food.taurine()
                );

                petDailyDietItemRepository.save(item);
            }
        }

        return toSimpleResponse(savedDiet, pet, recommendedMeals);
    }

    public void confirmRecommendedDiet(Long userId, Long dietId) {
        PetDailyDiet diet = petDailyDietRepository.findById(dietId)
                .orElseThrow(() -> new IllegalArgumentException("식단을 찾을 수 없습니다."));

        petRepository.findByIdAndUserId(diet.getPet().getId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식단을 등록할 권한이 없습니다."));

        if (Boolean.TRUE.equals(diet.getIsConfirmed())) {
            return;
        }

        boolean alreadyConfirmed = petDailyDietRepository
                .existsByPet_IdAndDietDateAndIsConfirmedTrue(
                        diet.getPet().getId(),
                        diet.getDietDate()
                );

        if (alreadyConfirmed) {
            throw new IllegalArgumentException("해당 날짜에 이미 최종 등록된 식단이 있습니다.");
        }

        List<Long> petFoodIds = petDailyDietItemRepository.findPetFoodIdsByDailyDietId(dietId);

        List<PetFoodStock> stocks = petFoodStockRepository
                .findByUser_IdAndPetFood_IdIn(userId, petFoodIds);

        stocks.forEach(stock ->
                stock.decreaseFoodLike(FINAL_REGISTER_FOOD_LIKE_DECREASE)
        );

        diet.confirm();
    }

    private DietRecommendSimpleResponse toSimpleResponse(
            PetDailyDiet savedDiet,
            Pet pet,
            List<RecommendedMeal> recommendedMeals
    ) {
        return DietRecommendSimpleResponse.builder()
                .dailyDietId(savedDiet.getId())
                .petId(pet.getId())
                .petName(pet.getPetName())
                .dietDate(savedDiet.getDietDate())
                .meals(recommendedMeals.stream()
                        .map(this::toSimpleMealDto)
                        .toList())
                .build();
    }

    private DietRecommendSimpleResponse.MealDto toSimpleMealDto(RecommendedMeal meal) {
        List<DietRecommendSimpleResponse.FoodDto> foods = meal.foods().stream()
                .map(this::toSimpleFoodDto)
                .toList();

        String description = foods.stream()
                .map(DietRecommendSimpleResponse.FoodDto::getDisplayText)
                .collect(Collectors.joining(", "));

        return DietRecommendSimpleResponse.MealDto.builder()
                .mealOrder(meal.mealOrder())
                .mealTime(meal.mealTime().toString().substring(0, 5))
                .description(description)
                .foods(foods)
                .build();
    }

    private DietRecommendSimpleResponse.FoodDto toSimpleFoodDto(CalculatedFood food) {
        String amount = food.amount()
                .stripTrailingZeros()
                .toPlainString();

        String unit = toDisplayUnit(food.unit());
        String displayText = food.petFood().getName() + " " + amount + unit;

        return DietRecommendSimpleResponse.FoodDto.builder()
                .foodId(food.petFood().getId())
                .foodName(food.petFood().getName())
                .amount(amount)
                .unit(unit)
                .displayText(displayText)
                .build();
    }

    private String toDisplayUnit(Unit unit) {
        if (unit == Unit.GRAM) {
            return "g";
        }

        return unit.name();
    }

    private RecommendedMeal findBestMealCombination(
            List<PetFoodStock> stocks,
            MealTarget target,
            int maxFoodCountPerMeal,
            Integer mealOrder,
            java.time.LocalTime mealTime,
            List<Long> usedFoodIdsToday
    ) {
        List<PetFoodStock> candidateStocks = stocks.stream()
                .collect(Collectors.toMap(
                        stock -> stock.getPetFood().getId(),
                        stock -> stock,
                        (stock1, stock2) -> stock1.getQuantity().compareTo(stock2.getQuantity()) >= 0
                                ? stock1
                                : stock2
                ))
                .values()
                .stream()
                .sorted(Comparator
                        .comparing(PetFoodStock::getFoodLike).reversed()
                        .thenComparing(
                                stock -> stock.getPetFood().getMetabolizableEnergyKcalPer100g(),
                                Comparator.reverseOrder()
                        ))
                .limit(CANDIDATE_LIMIT)
                .toList();

        if (candidateStocks.isEmpty()) {
            throw new IllegalArgumentException("계산 가능한 식품 후보가 없습니다.");
        }

        List<RecommendedMeal> topMeals = new ArrayList<>();

        int minCombinationSize = Math.min(2, candidateStocks.size());
        int limit = Math.min(maxFoodCountPerMeal, candidateStocks.size());

        for (int size = minCombinationSize; size <= limit; size++) {
            searchCombinationsAndKeepTop(
                    candidateStocks,
                    0,
                    size,
                    new ArrayList<>(),
                    topMeals,
                    target,
                    mealOrder,
                    mealTime,
                    usedFoodIdsToday
            );
        }

        if (topMeals.isEmpty()) {
            throw new IllegalArgumentException("추천 조합을 만들 수 없습니다.");
        }

        topMeals.sort(Comparator.comparing(RecommendedMeal::score));

        BigDecimal bestScore = topMeals.get(0).score();

        List<RecommendedMeal> similarBestMeals = topMeals.stream()
                .filter(meal -> meal.score()
                        .subtract(bestScore)
                        .abs()
                        .compareTo(SIMILAR_SCORE_RANGE) <= 0)
                .toList();

        return similarBestMeals.get(
                ThreadLocalRandom.current().nextInt(similarBestMeals.size())
        );
    }

    private void searchCombinationsAndKeepTop(
            List<PetFoodStock> candidates,
            int start,
            int targetSize,
            List<PetFoodStock> current,
            List<RecommendedMeal> topMeals,
            MealTarget target,
            Integer mealOrder,
            java.time.LocalTime mealTime,
            List<Long> usedFoodIdsToday
    ) {
        if (current.size() == targetSize) {
            int combinationSize = current.size();

            List<CalculatedFood> calculatedFoods = current.stream()
                    .map(stock -> calculateFoodByCombinationSize(
                            stock,
                            target.calorie(),
                            combinationSize
                    ))
                    .filter(food -> food.amount().compareTo(ZERO) > 0)
                    .toList();

            if (calculatedFoods.isEmpty()) {
                return;
            }

            NutritionSum sum = NutritionSum.of(calculatedFoods);

            BigDecimal score = calculateCombinationScore(sum, target, calculatedFoods)
                    .add(calculateRepeatPenalty(calculatedFoods, usedFoodIdsToday));

            RecommendedMeal meal = new RecommendedMeal(
                    mealOrder,
                    mealTime,
                    calculatedFoods,
                    sum,
                    score
            );

            addTopMeal(topMeals, meal);
            return;
        }

        for (int i = start; i < candidates.size(); i++) {
            current.add(candidates.get(i));

            searchCombinationsAndKeepTop(
                    candidates,
                    i + 1,
                    targetSize,
                    current,
                    topMeals,
                    target,
                    mealOrder,
                    mealTime,
                    usedFoodIdsToday
            );

            current.remove(current.size() - 1);
        }
    }

    private BigDecimal calculateRepeatPenalty(
            List<CalculatedFood> foods,
            List<Long> usedFoodIdsToday
    ) {
        if (usedFoodIdsToday.isEmpty()) {
            return ZERO;
        }

        long repeatCount = foods.stream()
                .map(food -> food.petFood().getId())
                .filter(usedFoodIdsToday::contains)
                .count();

        return REPEAT_FOOD_PENALTY.multiply(BigDecimal.valueOf(repeatCount));
    }

    private void addTopMeal(
            List<RecommendedMeal> topMeals,
            RecommendedMeal newMeal
    ) {
        topMeals.add(newMeal);

        topMeals.sort(Comparator.comparing(RecommendedMeal::score));

        if (topMeals.size() > TOP_MEAL_RESULT_LIMIT) {
            topMeals.remove(topMeals.size() - 1);
        }
    }

    private CalculatedFood calculateFoodByCombinationSize(
            PetFoodStock stock,
            BigDecimal targetCaloriePerMeal,
            int combinationSize
    ) {
        PetFood food = stock.getPetFood();

        BigDecimal kcalPerGram = food.getMetabolizableEnergyKcalPer100g()
                .divide(HUNDRED, 6, RoundingMode.HALF_UP);

        if (kcalPerGram.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("식품 칼로리 정보가 잘못됐습니다.");
        }

        BigDecimal targetCalorieForFood = targetCaloriePerMeal
                .divide(BigDecimal.valueOf(combinationSize), 2, RoundingMode.HALF_UP);

        BigDecimal amount = targetCalorieForFood
                .divide(kcalPerGram, 2, RoundingMode.HALF_UP);

        BigDecimal stockGram = convertToGram(stock.getQuantity(), stock.getUnit());

        if (amount.compareTo(stockGram) > 0) {
            amount = stockGram;
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        BigDecimal calorie = amount.multiply(kcalPerGram).setScale(2, RoundingMode.HALF_UP);
        BigDecimal protein = percentOf(amount, food.getCrudeProteinPercent());
        BigDecimal fat = percentOf(amount, food.getCrudeFatPercent());
        BigDecimal ash = percentOfNullable(amount, food.getCrudeAshPercent());
        BigDecimal fiber = percentOfNullable(amount, food.getCrudeFiberPercent());
        BigDecimal calcium = percentOfNullable(amount, food.getCalciumPercent());
        BigDecimal phosphorus = percentOfNullable(amount, food.getPhosphorusPercent());
        BigDecimal taurine = per100gNullable(amount, food.getTaurineMgPer100g());

        return new CalculatedFood(
                food,
                amount,
                Unit.GRAM,
                calorie,
                protein,
                fat,
                ash,
                fiber,
                calcium,
                phosphorus,
                taurine,
                stock.getFoodLike()
        );
    }

    private BigDecimal calculateCombinationScore(
            NutritionSum sum,
            MealTarget target,
            List<CalculatedFood> foods
    ) {
        BigDecimal score = ZERO;

        score = score.add(diffRatio(sum.calorie(), target.calorie()).multiply(CALORIE_WEIGHT));
        score = score.add(diffRatio(sum.protein(), target.protein()).multiply(PROTEIN_WEIGHT));
        score = score.add(diffRatio(sum.fat(), target.fat()).multiply(FAT_WEIGHT));
        score = score.add(diffRatioNullable(sum.ash(), target.ash()).multiply(ASH_WEIGHT));
        score = score.add(diffRatioNullable(sum.fiber(), target.fiber()).multiply(FIBER_WEIGHT));

        BigDecimal calciumPhosphorusRatio = calculateCalciumPhosphorusRatio(
                sum.calcium(),
                sum.phosphorus()
        );

        if (calciumPhosphorusRatio != null) {
            BigDecimal ratioDiff = calciumPhosphorusRatio
                    .subtract(CALCIUM_PHOSPHORUS_TARGET_RATIO)
                    .abs();

            score = score.add(
                    ratioDiff.multiply(CALCIUM_PHOSPHORUS_RATIO_WEIGHT)
            );
        }

        if (sum.taurine() == null || sum.taurine().compareTo(ZERO) <= 0) {
            score = score.add(TAURINE_MISSING_PENALTY);
        }

        BigDecimal averageFoodLike = foods.stream()
                .map(CalculatedFood::foodLike)
                .reduce(ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(foods.size()), 4, RoundingMode.HALF_UP);

        BigDecimal likeBonus = averageFoodLike
                .divide(HUNDRED, 4, RoundingMode.HALF_UP)
                .multiply(FOOD_LIKE_WEIGHT);

        return score.subtract(likeBonus).setScale(4, RoundingMode.HALF_UP);
    }

    private DietRecommendResponse toResponse(
            PetDailyDiet savedDiet,
            Pet pet,
            PetNutritionStandard standard,
            List<PetMealSchedule> schedules,
            List<RecommendedMeal> recommendedMeals,
            NutritionSum dailyTotal,
            BigDecimal calciumPhosphorusRatio
    ) {
        return DietRecommendResponse.builder()
                .dailyDietId(savedDiet.getId())
                .petId(pet.getId())
                .petName(pet.getPetName())
                .dietDate(savedDiet.getDietDate())
                .mealCount(schedules.size())

                .recommendedCalorie(standard.getRecommendedCalorie())
                .recommendedProtein(standard.getRecommendedProtein())
                .recommendedFat(standard.getRecommendedFat())
                .recommendedAsh(standard.getRecommendedAsh())
                .recommendedFiber(standard.getRecommendedFiber())
                .recommendedCalcium(standard.getRecommendedCalcium())
                .recommendedPhosphorus(standard.getRecommendedPhosphorus())
                .recommendedTaurine(standard.getRecommendedTaurine())

                .totalCalorie(dailyTotal.calorie())
                .totalProtein(dailyTotal.protein())
                .totalFat(dailyTotal.fat())
                .totalAsh(dailyTotal.ash())
                .totalFiber(dailyTotal.fiber())
                .totalCalcium(dailyTotal.calcium())
                .totalPhosphorus(dailyTotal.phosphorus())
                .totalTaurine(dailyTotal.taurine())
                .calciumPhosphorusRatio(calciumPhosphorusRatio)

                .calorieStatus(analyze(dailyTotal.calorie(), standard.getRecommendedCalorie()))
                .proteinStatus(analyze(dailyTotal.protein(), standard.getRecommendedProtein()))
                .fatStatus(analyze(dailyTotal.fat(), standard.getRecommendedFat()))
                .ashStatus(analyze(dailyTotal.ash(), standard.getRecommendedAsh()))
                .fiberStatus(analyze(dailyTotal.fiber(), standard.getRecommendedFiber()))
                .taurineStatus(analyzeTaurineIncluded(dailyTotal.taurine()))
                .calciumPhosphorusRatioStatus(analyzeCalciumPhosphorusRatio(calciumPhosphorusRatio))

                .meals(recommendedMeals.stream()
                        .map(this::toMealDto)
                        .toList())
                .build();
    }

    private AnalysisStatus analyzeCalciumPhosphorusRatio(BigDecimal ratio) {
        if (ratio == null) {
            return AnalysisStatus.UNKNOWN;
        }

        BigDecimal min = new BigDecimal("1.10");
        BigDecimal max = new BigDecimal("1.30");

        if (ratio.compareTo(min) < 0) {
            return AnalysisStatus.LACK;
        }

        if (ratio.compareTo(max) > 0) {
            return AnalysisStatus.EXCESS;
        }

        return AnalysisStatus.GOOD;
    }

    private AnalysisStatus analyzeTaurineIncluded(BigDecimal taurine) {
        if (taurine == null) {
            return AnalysisStatus.UNKNOWN;
        }

        if (taurine.compareTo(ZERO) <= 0) {
            return AnalysisStatus.LACK;
        }

        return AnalysisStatus.GOOD;
    }

    private DietRecommendResponse.MealRecommendDto toMealDto(RecommendedMeal meal) {
        return DietRecommendResponse.MealRecommendDto.builder()
                .mealOrder(meal.mealOrder())
                .mealTime(meal.mealTime())
                .actualCalorie(meal.nutritionSum().calorie())
                .actualProtein(meal.nutritionSum().protein())
                .actualFat(meal.nutritionSum().fat())
                .actualAsh(meal.nutritionSum().ash())
                .actualFiber(meal.nutritionSum().fiber())
                .actualCalcium(meal.nutritionSum().calcium())
                .actualPhosphorus(meal.nutritionSum().phosphorus())
                .actualTaurine(meal.nutritionSum().taurine())
                .foods(meal.foods().stream()
                        .map(this::toFoodDto)
                        .toList())
                .build();
    }

    private DietRecommendResponse.MealFoodDto toFoodDto(CalculatedFood food) {
        return DietRecommendResponse.MealFoodDto.builder()
                .foodId(food.petFood().getId())
                .foodName(food.petFood().getName())
                .foodImg(food.petFood().getFoodImg())
                .amount(food.amount())
                .unit(food.unit().name())
                .calorie(food.calorie())
                .protein(food.protein())
                .fat(food.fat())
                .ash(food.ash())
                .fiber(food.fiber())
                .calcium(food.calcium())
                .phosphorus(food.phosphorus())
                .taurine(food.taurine())
                .foodLike(food.foodLike())
                .build();
    }

    private AnalysisStatus analyze(BigDecimal actual, BigDecimal recommended) {
        if (actual == null || recommended == null || recommended.compareTo(ZERO) == 0) {
            return AnalysisStatus.UNKNOWN;
        }

        BigDecimal lowerBound = recommended.multiply(new BigDecimal("0.90"));
        BigDecimal upperBound = recommended.multiply(new BigDecimal("1.10"));

        if (actual.compareTo(lowerBound) < 0) {
            return AnalysisStatus.LACK;
        }

        if (actual.compareTo(upperBound) > 0) {
            return AnalysisStatus.EXCESS;
        }

        return AnalysisStatus.GOOD;
    }

    private BigDecimal calculateCalciumPhosphorusRatio(BigDecimal calcium, BigDecimal phosphorus) {
        if (calcium == null || phosphorus == null || phosphorus.compareTo(ZERO) == 0) {
            return null;
        }

        return calcium.divide(phosphorus, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal convertToGram(BigDecimal quantity, Unit unit) {
        return quantity;
    }

    private BigDecimal percentOf(BigDecimal amount, BigDecimal percent) {
        return amount.multiply(percent)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal percentOfNullable(BigDecimal amount, BigDecimal percent) {
        if (percent == null) {
            return ZERO;
        }

        return percentOf(amount, percent);
    }

    private BigDecimal per100gNullable(BigDecimal amount, BigDecimal valuePer100g) {
        if (valuePer100g == null) {
            return ZERO;
        }

        return amount.multiply(valuePer100g)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal diffRatio(BigDecimal actual, BigDecimal target) {
        if (target == null || target.compareTo(ZERO) == 0) {
            return ZERO;
        }

        return actual.subtract(target)
                .abs()
                .divide(target, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal diffRatioNullable(BigDecimal actual, BigDecimal target) {
        if (target == null) {
            return ZERO;
        }

        return diffRatio(actual, target);
    }

    private record MealTarget(
            BigDecimal calorie,
            BigDecimal protein,
            BigDecimal fat,
            BigDecimal ash,
            BigDecimal fiber
    ) {
        static MealTarget of(PetNutritionStandard standard, int mealCount) {
            BigDecimal divisor = BigDecimal.valueOf(mealCount);

            return new MealTarget(
                    divide(standard.getRecommendedCalorie(), divisor),
                    divide(standard.getRecommendedProtein(), divisor),
                    divide(standard.getRecommendedFat(), divisor),
                    divideNullable(standard.getRecommendedAsh(), divisor),
                    divideNullable(standard.getRecommendedFiber(), divisor)
            );
        }

        private static BigDecimal divide(BigDecimal value, BigDecimal divisor) {
            return value.divide(divisor, 2, RoundingMode.HALF_UP);
        }

        private static BigDecimal divideNullable(BigDecimal value, BigDecimal divisor) {
            if (value == null) {
                return null;
            }

            return divide(value, divisor);
        }
    }

    private record CalculatedFood(
            PetFood petFood,
            BigDecimal amount,
            Unit unit,
            BigDecimal calorie,
            BigDecimal protein,
            BigDecimal fat,
            BigDecimal ash,
            BigDecimal fiber,
            BigDecimal calcium,
            BigDecimal phosphorus,
            BigDecimal taurine,
            BigDecimal foodLike
    ) {
    }

    private record NutritionSum(
            BigDecimal calorie,
            BigDecimal protein,
            BigDecimal fat,
            BigDecimal ash,
            BigDecimal fiber,
            BigDecimal calcium,
            BigDecimal phosphorus,
            BigDecimal taurine
    ) {
        static NutritionSum empty() {
            return new NutritionSum(ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO);
        }

        static NutritionSum of(List<CalculatedFood> foods) {
            NutritionSum sum = empty();

            for (CalculatedFood food : foods) {
                sum = new NutritionSum(
                        sum.calorie.add(food.calorie),
                        sum.protein.add(food.protein),
                        sum.fat.add(food.fat),
                        sum.ash.add(food.ash),
                        sum.fiber.add(food.fiber),
                        sum.calcium.add(food.calcium),
                        sum.phosphorus.add(food.phosphorus),
                        sum.taurine.add(food.taurine)
                );
            }

            return sum.scale();
        }

        NutritionSum add(NutritionSum other) {
            return new NutritionSum(
                    this.calorie.add(other.calorie),
                    this.protein.add(other.protein),
                    this.fat.add(other.fat),
                    this.ash.add(other.ash),
                    this.fiber.add(other.fiber),
                    this.calcium.add(other.calcium),
                    this.phosphorus.add(other.phosphorus),
                    this.taurine.add(other.taurine)
            ).scale();
        }

        NutritionSum scale() {
            return new NutritionSum(
                    calorie.setScale(2, RoundingMode.HALF_UP),
                    protein.setScale(2, RoundingMode.HALF_UP),
                    fat.setScale(2, RoundingMode.HALF_UP),
                    ash.setScale(2, RoundingMode.HALF_UP),
                    fiber.setScale(2, RoundingMode.HALF_UP),
                    calcium.setScale(2, RoundingMode.HALF_UP),
                    phosphorus.setScale(2, RoundingMode.HALF_UP),
                    taurine.setScale(2, RoundingMode.HALF_UP)
            );
        }
    }

    private record RecommendedMeal(
            Integer mealOrder,
            java.time.LocalTime mealTime,
            List<CalculatedFood> foods,
            NutritionSum nutritionSum,
            BigDecimal score
    ) {
    }
}