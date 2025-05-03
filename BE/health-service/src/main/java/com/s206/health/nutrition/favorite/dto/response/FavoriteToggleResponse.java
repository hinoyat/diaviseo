package com.s206.health.nutrition.favorite.dto.response;

import com.s206.health.nutrition.favorite.entity.FavoriteFood;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FavoriteToggleResponse {
    private Integer foodId;
    private boolean isFavorite;
    private String message;
    private LocalDateTime toggledAt;

    public static FavoriteToggleResponse toDto(FavoriteFood favorite, boolean isNowFavorite) {
        return FavoriteToggleResponse.builder()
                .foodId(favorite.getFood().getFoodId())
                .isFavorite(isNowFavorite)
                .message(isNowFavorite ? "즐겨찾기 등록됨" : "즐겨찾기 해제됨")
                .toggledAt(favorite.getCreatedAt())
                .build();
    }
}