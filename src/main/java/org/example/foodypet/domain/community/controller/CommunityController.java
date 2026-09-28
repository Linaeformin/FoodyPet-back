package org.example.foodypet.domain.community.controller;

import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.community.dto.*;
import org.example.foodypet.domain.community.service.CommunityService;
import org.example.foodypet.domain.diary.dto.MealDiaryResponse;
import org.example.foodypet.domain.diary.dto.PetTreatDiaryCreateRequest;
import org.example.foodypet.domain.diary.dto.TreatDiaryTodayResponse;
import org.example.foodypet.domain.diary.dto.TreatStockAutocompleteResponse;
import org.example.foodypet.domain.diary.service.PetTreatDiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/community/connect-meals/times")
    public ResponseEntity<List<ConnectMealTimeResponseDto>> getConnectMealTimes(
            @RequestBody ConnectMealTimeRequestDto request,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        List<ConnectMealTimeResponseDto> response =
                communityService.getConnectMealTimes(me.getId(), request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/community/connect-meals/preview")
    public ResponseEntity<ConnectMealPreviewResponseDto> getConnectMealPreview(
            @RequestBody ConnectMealPreviewRequestDto request,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        ConnectMealPreviewResponseDto response =
                communityService.getConnectMealPreview(me.getId(), request);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/community/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommunityPostCreateResponseDto> createCommunityPost(
            @RequestPart("image") MultipartFile image,
            @RequestPart("request") CommunityPostCreateRequestDto request,
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        CommunityPostCreateResponseDto response =
                communityService.createCommunityPost(me.getId(), image, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/community/posts")
    public ResponseEntity<List<CommunityPostListResponseDto>> getCommunityPosts(
            @AuthenticationPrincipal CustomUserDetails me
    ) {
        List<CommunityPostListResponseDto> response =
                communityService.getCommunityPosts(me.getId());

        return ResponseEntity.ok(response);
    }
}