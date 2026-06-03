package org.example.foodypet.domain.diary.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.domain.diary.dto.MealDiaryWriteFormResponse;
import org.example.foodypet.domain.diary.dto.PetMealDiaryCreateRequest;
import org.example.foodypet.domain.diet.dto.DietRecommendSimpleResponse;
import org.example.foodypet.domain.diary.service.MealDiaryDietService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries/meals")
public class PetMealDiaryController {

    private final MealDiaryDietService mealDiaryDietService;

    /**
     * 식단 일기 작성 화면용 데이터 조회
     *
     * 예:
     * GET /api/diaries/meals/write-form?petId=15&date=2026-06-03
     */
    @GetMapping("/write-form")
    public MealDiaryWriteFormResponse getMealDiaryWriteForm(
            @RequestParam Long petId,
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return mealDiaryDietService.getMealDiaryWriteForm(petId, date);
    }

    /**
     * 식단 일기 등록
     *
     * 예:
     * POST /api/diaries/meals
     */
    @PostMapping
    public ResponseEntity<Void> createMealDiary(
            @RequestBody PetMealDiaryCreateRequest request
    ) {
        mealDiaryDietService.createMealDiary(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}