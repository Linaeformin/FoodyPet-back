package org.example.foodypet.domain.pet.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class PetCapsuleIntakeRequestDto {

    @NotEmpty(message = "영양제 섭취 목록은 비어 있을 수 없습니다.")
    @Valid
    private List<CapsuleIntakeItem> capsuleIntakes;

    @Getter
    public static class CapsuleIntakeItem {

        @NotNull(message = "영양제 ID는 필수입니다.")
        private Long petCapsuleId;

        @NotNull(message = "섭취 횟수는 필수입니다.")
        @Min(value = 0, message = "섭취 횟수는 0 이상이어야 합니다.")
        private Integer givenCount;
    }
}