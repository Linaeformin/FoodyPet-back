package org.example.foodypet.domain.diet.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.diet.dto.DietRecommendRequest;
import org.example.foodypet.domain.diet.dto.DietRecommendResponse;
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

    @PostMapping("/recommend")
    public ResponseEntity<DietRecommendSimpleResponse> recommendDiet(
            @RequestBody DietRecommendRequest request,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        DietRecommendSimpleResponse response =
                dietRecommendService.recommendAndSaveDiet(me.getId(), request);

        return ResponseEntity.ok(response);
    }
}