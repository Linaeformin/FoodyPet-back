package org.example.foodypet.domain.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ConnectMealTimeRequestDto {

    private Long petId;

    private LocalDate mealDate;
}