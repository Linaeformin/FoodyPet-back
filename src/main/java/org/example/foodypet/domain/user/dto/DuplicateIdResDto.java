package org.example.foodypet.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DuplicateIdResDto {
    private String userId;
    private boolean isDuplicate;
}
