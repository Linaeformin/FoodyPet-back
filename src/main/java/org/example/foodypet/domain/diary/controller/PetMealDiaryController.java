package org.example.foodypet.domain.diary.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.diary.dto.MealDiaryResponse;
import org.example.foodypet.domain.diary.dto.MealDiaryWriteFormResponse;
import org.example.foodypet.domain.diary.dto.PetMealDiaryCreateRequest;
import org.example.foodypet.domain.diet.dto.DietRecommendSimpleResponse;
import org.example.foodypet.domain.diary.service.MealDiaryDietService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMealDiary(
            @RequestPart("request") PetMealDiaryCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        mealDiaryDietService.createMealDiary(request, image);
        return ResponseEntity
                .status(201)
                .body(new ApiSuccess(201, "성공적으로 처리되었습니다."));
    }


    @GetMapping("/pets/{petId}/today")
    public ResponseEntity<List<MealDiaryResponse>> getTodayMealDiaries(
            @PathVariable Long petId,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        List<MealDiaryResponse> response =
                mealDiaryDietService.getTodayMealDiaries(me.getId(), petId);

        return ResponseEntity.ok(response);
    }
}