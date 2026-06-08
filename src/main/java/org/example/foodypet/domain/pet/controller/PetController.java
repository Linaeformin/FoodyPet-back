package org.example.foodypet.domain.pet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.S3Uploader;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.pet.dto.PetAssignFormDto;
import org.example.foodypet.domain.pet.dto.PetCapsuleIntakeListResponseDto;
import org.example.foodypet.domain.pet.dto.PetCapsuleIntakeRequestDto;
import org.example.foodypet.domain.pet.service.PetService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;
    private final S3Uploader s3Uploader;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> assignPet(
            @AuthenticationPrincipal CustomUserDetails me,
            @RequestPart("data") @Valid PetAssignFormDto petAssignFormDto,
            @RequestPart("image") MultipartFile image
    ) {
        String imageUrl = s3Uploader.uploadPetImage(image);

        petAssignFormDto.getPetInfo().setImageUrl(imageUrl);

        petService.assignPet(me.getId(), petAssignFormDto);

        return ResponseEntity
                .status(201)
                .body(new ApiSuccess(201, "성공적으로 처리되었습니다."));
    }

    @PostMapping("/{petId}/capsule-intakes")
    public ResponseEntity<?> updateTodayCapsuleIntakes(
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable Long petId,
            @Valid @RequestBody PetCapsuleIntakeRequestDto requestDto
    ) {
        petService.updateTodayCapsuleIntakes(me.getId(), petId, requestDto);

        return ResponseEntity
                .status(201)
                .body(new ApiSuccess(201, "성공적으로 처리되었습니다."));
    }

    @GetMapping("/{petId}/capsule-intakes")
    public ResponseEntity<?> getTodayCapsuleIntakes(
            @AuthenticationPrincipal CustomUserDetails me,
            @PathVariable Long petId
    ) {
        PetCapsuleIntakeListResponseDto response = petService.getTodayCapsuleIntakes(me.getId(), petId);

        return ResponseEntity.ok(response);
    }
}