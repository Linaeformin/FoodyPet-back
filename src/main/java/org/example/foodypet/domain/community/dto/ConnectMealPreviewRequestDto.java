package org.example.foodypet.domain.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ConnectMealPreviewRequestDto {

    private Long petId;

    private LocalDate mealDate;

    private LocalTime mealTime;
}