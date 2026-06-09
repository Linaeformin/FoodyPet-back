package org.example.foodypet.domain.diary.dto;

import org.example.foodypet.domain.diary.entity.MealStatus;
import org.example.foodypet.domain.diary.entity.Satisfaction;
import org.example.foodypet.domain.diary.entity.Symptom;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record MealDiaryDetailResponse(
        Long diaryId,
        Long petId,
        Long dailyDietId,
        Long petMealScheduleId,
        LocalDate diaryDate,
        String imageUrl,
        Satisfaction satisfaction,
        MealStatus mealStatus,
        BigDecimal waterIntakeMl,
        String memo,
        List<Symptom> symptoms,
        List<CapsuleResponse> capsules
) {

    public record CapsuleResponse(
            Long petCapsuleId,
            String capsuleName,
            Integer givenCount
    ) {
    }
}