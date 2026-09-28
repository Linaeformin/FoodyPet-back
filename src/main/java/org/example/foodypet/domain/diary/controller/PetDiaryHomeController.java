package org.example.foodypet.domain.diary.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.diary.dto.PetTodayDiaryHomeResponse;
import org.example.foodypet.domain.diary.service.PetDiaryHomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class PetDiaryHomeController {

    private final PetDiaryHomeService petDiaryHomeService;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayDiaryHome(
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        PetTodayDiaryHomeResponse response =
                petDiaryHomeService.getTodayDiaryHome(me);

        return ResponseEntity.ok(response);
    }
}