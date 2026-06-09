package org.example.foodypet.domain.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommunityPostCreateRequestDto {

    private String title;

    private String content;

    private Long mealDiaryId;
}