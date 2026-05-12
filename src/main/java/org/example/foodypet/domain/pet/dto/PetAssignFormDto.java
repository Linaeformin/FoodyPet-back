package org.example.foodypet.domain.pet.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.foodypet.domain.pet.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetAssignFormDto {

    @Valid
    @NotNull(message = "반려동물 정보는 필수입니다.")
    private PetInfoDto petInfo;

    @Valid
    @NotNull(message = "식단 정보는 필수입니다.")
    private MealInfoDto mealInfo;

    @Valid
    @Size(max = 5, message = "영양제는 최대 5개까지 등록할 수 있습니다.")
    private List<SupplementInfoDto> supplements;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PetInfoDto {

        @NotBlank(message = "반려동물 이름은 필수입니다.")
        @Size(max = 30, message = "반려동물 이름은 30자 이하로 입력해주세요.")
        private String name;

        @NotNull(message = "생일은 필수입니다.")
        private LocalDate birthDate;

        @NotNull(message = "몸무게는 필수입니다.")
        @DecimalMin(value = "0.1", message = "몸무게는 0.1kg 이상이어야 합니다.")
        @DecimalMax(value = "100.0", message = "몸무게는 100kg 이하로 입력해주세요.")
        private BigDecimal weightKg;

        @NotBlank(message = "반려동물 이미지는 필수입니다.")
        private String imageUrl;

        @NotNull(message = "반려동물 타입은 필수입니다.")
        private PetType petType;

        private DogBreed dogBreed;

        private CatBreed catBreed;

        @NotNull(message = "성별은 필수입니다.")
        private PetGender gender;

        @NotNull(message = "중성화 여부는 필수입니다.")
        private NeuteredStatus neuteredStatus;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MealInfoDto {

        @NotNull(message = "하루 급여 횟수는 필수입니다.")
        @Min(value = 1, message = "급여 횟수는 최소 1회입니다.")
        @Max(value = 6, message = "급여 횟수는 최대 6회입니다.")
        private Integer mealCount;

        @Valid
        @NotEmpty(message = "급여 시각은 최소 1개 이상 필요합니다.")
        @Size(max = 6, message = "급여 시각은 최대 6개까지 입력할 수 있습니다.")
        private List<MealTimeDto> mealTimes;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MealTimeDto {

        @NotNull(message = "급여 순서는 필수입니다.")
        @Min(value = 1, message = "급여 순서는 최소 1입니다.")
        @Max(value = 6, message = "급여 순서는 최대 6입니다.")
        private Integer sequence;

        @NotNull(message = "급여 시각은 필수입니다.")
        private LocalTime time;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SupplementInfoDto {

        @NotBlank(message = "영양제 이름은 필수입니다.")
        @Size(max = 30, message = "영양제 이름은 30자 이하로 입력해주세요.")
        private String name;

        @NotNull(message = "영양제 급여 횟수는 필수입니다.")
        @Min(value = 1, message = "영양제 급여 횟수는 최소 1회입니다.")
        private Integer countPerDay;
    }
}