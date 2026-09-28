package org.example.foodypet.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommunityPostListResponseDto {

    private Long postId;

    private String profileImageUrl;

    private String nickname;

    private String tag;

    private String title;

    private String imgUrl;

    private String context;

    private List<CommunityPostMealResponseDto> meal;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean isMine;
}