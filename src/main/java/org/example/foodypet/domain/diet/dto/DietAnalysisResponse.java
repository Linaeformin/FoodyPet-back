package org.example.foodypet.domain.diet.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DietAnalysisResponse {

    private Long dailyDietId;

    private Long petId;
    private String petName;

    private LocalDate dietDate;

    // 예: "랑이의 1회 식단 분석"
    private String title;

    // 최종 등록 버튼 제어용
    private Boolean confirmed;
    private Boolean canConfirm;

    // 도넛 차트
    private List<NutrientRatioDto> nutrientRatios;

    // 칼로리, 단백질, 지방, 조회분, 조섬유 막대
    private List<NutrientBarDto> nutrientBars;

    // 칼슘 : 인 카드
    private CalciumPhosphorusDto calciumPhosphorus;

    // 타우린 카드
    private TaurineDto taurine;

    @Getter
    @Builder
    public static class NutrientRatioDto {
        private String name;          // 단백질, 지방, 조섬유 등
        private BigDecimal value;     // 26
        private String unit;          // %, kcal, mg 등
        private String displayText;   // 단백질(26%)
    }

    @Getter
    @Builder
    public static class NutrientBarDto {
        private String name;              // 칼로리, 단백질, 지방, 조회분, 조섬유
        private BigDecimal value;         // 220, 26, 9, 8, 5
        private String unit;              // kcal, %
        private String displayValue;      // 220kcal, 26%
        private BigDecimal percent;       // progress bar 채우는 비율. 0~100
        private AnalysisStatus status;    // GOOD, LACK, EXCESS, UNKNOWN
        private String statusText;        // 양호, 부족, 많음
    }

    @Getter
    @Builder
    public static class CalciumPhosphorusDto {
        private BigDecimal calcium;
        private BigDecimal phosphorus;
        private BigDecimal ratio;

        // 예: "1.2 : 1"
        private String displayRatio;

        private AnalysisStatus status;
        private String statusText;
    }

    @Getter
    @Builder
    public static class TaurineDto {
        private BigDecimal value;
        private String unit;

        // 예: "25mg"
        private String displayValue;

        private AnalysisStatus status;
        private String statusText;
    }
}