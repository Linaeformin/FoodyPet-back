package org.example.foodypet.domain.diary.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.diet.dto.DietRecommendSimpleResponse;
import org.example.foodypet.domain.diary.service.MealDiaryDietService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries/meals")
public class PetMealDiaryController {

    private final MealDiaryDietService mealDiaryDietService;

    /**
     * 식단 불러오기
     * 최종 등록된 식단만 조회
     *
     * 예:
     * GET /api/diaries/meals/diets/confirmed?petId=1&date=2026-03-30
     */
    @GetMapping("/diets/confirmed")
    public DietRecommendSimpleResponse getConfirmedDiet(
            @RequestParam Long petId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return mealDiaryDietService.getConfirmedDiet(petId, date);
    }
}