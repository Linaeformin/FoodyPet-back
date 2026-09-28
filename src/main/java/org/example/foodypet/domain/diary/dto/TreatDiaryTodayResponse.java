package org.example.foodypet.domain.diary.dto;

import java.util.List;

public record TreatDiaryTodayResponse(
        Integer treatRound,
        List<TreatDiaryItemResponse> items
) {
}