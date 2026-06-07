package org.example.foodypet.domain.diary.dto;

import java.math.BigDecimal;
import java.util.List;

public record PetTreatDiaryCreateRequest(
        Integer treatRound,
        List<Item> items
) {
    public record Item(
            Long stockId,
            BigDecimal amount
    ) {
    }
}