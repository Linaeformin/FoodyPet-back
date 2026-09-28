package org.example.foodypet.domain.diet.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.diet.dto.DietAnalysisResponse;
import org.example.foodypet.domain.diet.dto.DietRecommendRequest;
import org.example.foodypet.domain.diet.dto.DietRecommendSimpleResponse;
import org.example.foodypet.domain.diet.service.DietRecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diets")
@RequiredArgsConstructor
public class DietRecommendController {

    private final DietRecommendService dietRecommendService;

    // 식단 추천
    @PostMapping("/recommend")
    public ResponseEntity<DietRecommendSimpleResponse> recommendDiet(
            @RequestBody DietRecommendRequest request,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        DietRecommendSimpleResponse response =
                dietRecommendService.recommendAndSaveDiet(me.getId(), request);

        return ResponseEntity.ok(response);
    }

    // 오늘의 식단 영양 분석
    @GetMapping("/recommend/{dietId}")
    public ResponseEntity<DietAnalysisResponse> getDietAnalysis(
            @PathVariable Long dietId,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        DietAnalysisResponse response =
                dietRecommendService.getDietAnalysis(me.getId(), dietId);

        return ResponseEntity.ok(response);
    }

    // 최종 등록
    @PostMapping("/recommend/{dietId}")
    public ResponseEntity<ApiSuccess> postDietRecommend(
            @PathVariable Long dietId,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        dietRecommendService.confirmRecommendedDiet(me.getId(), dietId);

        return ResponseEntity
                .ok(new ApiSuccess(200, "성공적으로 처리되었습니다."));
    }
}