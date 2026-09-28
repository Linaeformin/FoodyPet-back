package org.example.foodypet.domain.diary.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.diary.dto.MealDiaryResponse;
import org.example.foodypet.domain.diary.dto.PetTreatDiaryCreateRequest;
import org.example.foodypet.domain.diary.dto.TreatDiaryTodayResponse;
import org.example.foodypet.domain.diary.dto.TreatStockAutocompleteResponse;
import org.example.foodypet.domain.diary.service.PetTreatDiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PetTreatDiaryController {

    private final PetTreatDiaryService petTreatDiaryService;

    @GetMapping("/treat-diaries/autocomplete")
    public ResponseEntity<List<TreatStockAutocompleteResponse>> autocompleteTreatStocks(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        List<TreatStockAutocompleteResponse> response =
                petTreatDiaryService.autocompleteTreatStocks(me.getId(), keyword);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pets/{petId}/treat-diaries/today")
    public ResponseEntity<List<TreatDiaryTodayResponse>> getTodayTreatDiaries(
            @PathVariable Long petId,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        List<TreatDiaryTodayResponse> response =
                petTreatDiaryService.getTodayTreatDiaries(me.getId(), petId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/pets/{petId}/treat-diaries")
    public ResponseEntity<?> createTreatDiary(
            @PathVariable Long petId,
            @RequestBody PetTreatDiaryCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        petTreatDiaryService.createTreatDiary(me.getId(), petId, request);

        return ResponseEntity.ok(
                new ApiSuccess(200, "성공적으로 처리되었습니다.")
        );
    }
}