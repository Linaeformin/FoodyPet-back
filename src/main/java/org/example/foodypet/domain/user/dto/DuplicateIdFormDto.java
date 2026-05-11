package org.example.foodypet.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DuplicateIdFormDto {
    // 아이디
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;
}
