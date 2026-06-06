package org.example.foodypet.domain.water.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.water.dto.WaterIntakeRequestDto;
import org.example.foodypet.domain.water.service.WaterIntakeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/water")
@RequiredArgsConstructor
public class WaterIntakeController {

    private final WaterIntakeService waterIntakeService;

    @PostMapping("/{petId}/water-intakes")
    public ResponseEntity<?> addWaterIntake(
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable Long petId,
            @Valid @RequestBody WaterIntakeRequestDto requestDto
    ) {
        waterIntakeService.addWaterIntake(me.getId(), petId, requestDto);
        return ResponseEntity
                .status(201)
                .body(new ApiSuccess(201, "성공적으로 처리되었습니다."));
    }
}