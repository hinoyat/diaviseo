package com.s206.health.exercise.dto.response;

import com.s206.health.exercise.entity.ExerciseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseTypeDetailResponse {
    private Integer exerciseTypeId;
    private Integer exerciseCategoryId;
    private String exerciseName;
    private String exerciseEnglishName;
    private Integer exerciseCalorie;
    private Integer exerciseNumber;
    private String exerciseCategoryName;  // 카테고리 이름도 포함
    private Boolean isFavorite;           // 즐겨찾기 여부
    private LocalDateTime createdAt;

    public static ExerciseTypeDetailResponse fromEntity(
            ExerciseType exerciseType,
            String categoryName,
            Boolean isFavorite) {
        return ExerciseTypeDetailResponse.builder()
                .exerciseTypeId(exerciseType.getExerciseTypeId())
                .exerciseCategoryId(exerciseType.getExerciseCategoryId())
                .exerciseName(exerciseType.getExerciseName())
                .exerciseEnglishName(exerciseType.getExerciseEnglishName())
                .exerciseCalorie(exerciseType.getExerciseCalorie())
                .exerciseNumber(exerciseType.getExerciseNumber())
                .exerciseCategoryName(categoryName)
                .isFavorite(isFavorite)
                .createdAt(exerciseType.getCreatedAt())
                .build();
    }
}
