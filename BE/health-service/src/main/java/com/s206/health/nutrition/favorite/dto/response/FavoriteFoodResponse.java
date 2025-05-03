package com.s206.health.nutrition.favorite.dto.response;

import com.s206.health.nutrition.favorite.entity.FavoriteFood;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FavoriteFoodResponse {
    private Integer foodId;
    private String foodName;
    private Integer calorie;
    private LocalDateTime createdAt;

    public static FavoriteFoodResponse toDto(FavoriteFood favorite) {
        return FavoriteFoodResponse.builder()
                .foodId(favorite.getFood().getFoodId())
                .foodName(favorite.getFood().getFoodName())
                .calorie(favorite.getFood().getCalorie())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}