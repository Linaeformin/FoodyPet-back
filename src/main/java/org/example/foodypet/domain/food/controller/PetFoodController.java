package org.example.foodypet.domain.food.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.food.dto.*;
import org.example.foodypet.domain.food.entity.FoodType;
import org.example.foodypet.domain.food.entity.PetFoodStockSortType;
import org.example.foodypet.domain.food.service.PetFoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class PetFoodController {

    private final PetFoodService petFoodService;

    // 전체 식품 목록 조회
    @GetMapping
    public ResponseEntity<?> getPetFoodList(
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        PetFoodListResDto response = petFoodService.getPetFoodList();

        return ResponseEntity.ok(response);
    }

    // 시스템에 등록된 재고 등록
    @PostMapping("/assign")
    public ResponseEntity<?> assignPetFood(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody PetFoodSystemFormDto dto
    ) {
        petFoodService.assignPetFood(me, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiSuccess(201, "성공적으로 처리되었습니다."));
    }

    // 재고 관리 화면 조회
    @GetMapping("/stocks")
    public ResponseEntity<?> getPetFoodStocks(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestParam(defaultValue = "COOKED") FoodType foodType,
            @RequestParam(defaultValue = "false") Boolean treat,
            @RequestParam(defaultValue = "CREATED") PetFoodStockSortType sort
    ) {
        PetFoodStockListResDto response = petFoodService.getPetFoodStocks(
                me,
                foodType,
                treat,
                sort
        );

        return ResponseEntity.ok(response);
    }

    // 재고 수량 여러 개 수정
    @PatchMapping("/stocks")
    public ResponseEntity<?> updatePetFoodStockQuantities(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody PetFoodStockUpdateReqDto dto
    ) {
        petFoodService.updatePetFoodStockQuantities(me, dto);

        return ResponseEntity.ok(
                new ApiSuccess(200, "성공적으로 처리되었습니다.")
        );
    }

    // 재고 단일 수정
    @PutMapping("/stocks/{stockId}")
    public ResponseEntity<?> updatePetFoodStock(
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable Long stockId,
            @RequestBody PetFoodStockUpdateFormDto dto
    ) {
        petFoodService.updatePetFoodStock(me, stockId, dto);

        return ResponseEntity.ok(
                new ApiSuccess(200, "성공적으로 처리되었습니다.")
        );
    }

    // 재고 여러 개 삭제
    @DeleteMapping("/stocks")
    public ResponseEntity<?> deletePetFoodStocks(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestBody PetFoodStockDeleteReqDto dto
    ) {
        petFoodService.deletePetFoodStocks(me, dto);

        return ResponseEntity.ok(
                new ApiSuccess(200, "성공적으로 처리되었습니다.")
        );
    }

    // 식품 자동완성 검색
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocompletePetFoods(
            @RequestParam String keyword
    ) {
        PetFoodAutocompleteResDto response = petFoodService.autocompletePetFoods(keyword);

        return ResponseEntity.ok(response);
    }
}