package com.s206.health.exercise.favorite.dto.response;

import com.s206.health.exercise.favorite.entity.FavoriteExercise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteToggleResponse {
    private Integer exerciseTypeId;
    private Integer exerciseNumber;
    private boolean isFavorite;
    private String message;
    private LocalDateTime toggledAt;

    public static FavoriteToggleResponse toDto(FavoriteExercise favorite, boolean isNowFavorite) {
        return FavoriteToggleResponse.builder()
                .exerciseTypeId(favorite.getExerciseType().getExerciseTypeId())
                .exerciseNumber(favorite.getExerciseType().getExerciseNumber())
                .isFavorite(isNowFavorite)
                .message(isNowFavorite ? "즐겨찾기 등록됨" : "즐겨찾기 해제됨")
                .toggledAt(favorite.getCreatedAt())
                .build();
    }
}
