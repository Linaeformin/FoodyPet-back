package org.example.foodypet.domain.diary.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.foodypet.domain.diary.entity.MealStatus;
import org.example.foodypet.domain.diary.entity.PetMealDiary;
import org.example.foodypet.domain.diary.entity.Satisfaction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MealDiaryResponse {

    private Long mealDiaryId;
    private Long petId;

    private LocalDate diaryDate;

    private Integer mealOrder;
    private LocalTime mealTime;

    private String imageUrl;

    private Satisfaction satisfaction;
    private MealStatus mealStatus;

    private String dietSummary;
    private List<MealDiaryFoodResponse> foods;

    private String memo;

    public static MealDiaryResponse of(
            PetMealDiary diary,
            List<MealDiaryFoodResponse> foods
    ) {
        return MealDiaryResponse.builder()
                .mealDiaryId(diary.getId())
                .petId(diary.getPet().getId())
                .diaryDate(diary.getDiaryDate())
                .mealOrder(diary.getPetMealSchedule().getMealOrder())
                .mealTime(diary.getPetMealSchedule().getMealTime())
                .imageUrl(diary.getImageUrl())
                .satisfaction(diary.getSatisfaction())
                .mealStatus(diary.getMealStatus())
                .foods(foods)
                .dietSummary(makeDietSummary(foods))
                .memo(diary.getMemo())
                .build();
    }

    private static String makeDietSummary(List<MealDiaryFoodResponse> foods) {
        if (foods == null || foods.isEmpty()) {
            return "";
        }

        return foods.stream()
                .map(MealDiaryFoodResponse::getDisplayText)
                .collect(Collectors.joining(", "));
    }
}