package org.example.foodypet.domain.food.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.food.dto.PetFoodListResDto;
import org.example.foodypet.domain.food.service.PetFoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class PetFoodController {

    private final PetFoodService petFoodService;

    /**
     * 사료 목록 조회
     * 토큰 인증 필요
     */
    @GetMapping
    public ResponseEntity<?> getPetFoodList(
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        PetFoodListResDto response = petFoodService.getPetFoodList();

        return ResponseEntity.ok(response);
    }
}