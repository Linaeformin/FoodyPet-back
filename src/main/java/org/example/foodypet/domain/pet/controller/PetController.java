package org.example.foodypet.domain.pet.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.foodypet.common.config.ApiSuccess;
import org.example.foodypet.common.config.CustomUserDetails;
import org.example.foodypet.domain.pet.dto.PetAssignFormDto;
import org.example.foodypet.domain.pet.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<?> assignPet(
            @AuthenticationPrincipal CustomUserDetails me,
            @Valid @RequestBody PetAssignFormDto petAssignFormDto
    ) {
        petService.assignPet(me.getId(), petAssignFormDto);

        return ResponseEntity
                .status(201)
                .body(new ApiSuccess(201, "성공적으로 처리되었습니다."));
    }
}