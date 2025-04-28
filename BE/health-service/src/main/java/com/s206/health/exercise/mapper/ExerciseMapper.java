package com.s206.health.exercise.mapper;

import com.s206.health.exercise.dto.response.ExerciseListResponse;
import com.s206.health.exercise.entity.Exercise;
import com.s206.health.exercise.entity.ExerciseCategory;
import com.s206.health.exercise.entity.ExerciseType;
import org.springframework.stereotype.Component;

/**
 * 운동 관련 엔티티와 DTO 간의 변환을 담당하는 매퍼 클래스
 */
@Component
public class ExerciseMapper {

    // 1. 운동 기록 생성 Mapper
    // Exercise 엔티티와 관련 정보를 ExerciseListResponse DTO 로 변환
    public ExerciseListResponse toListResponse(
            Exercise exercise,
            ExerciseType exerciseType,
            ExerciseCategory exerciseCategory,
            Integer calculatedCalorie) {

        String exerciseName = exerciseType != null ? exerciseType.getExerciseName() : "";
        String categoryName = exerciseCategory != null ? exerciseCategory.getExerciseCategoryName() : "";

        // 칼로리가 제공되지 않은 경우 운동 시간과 분당 칼로리로 계산
        if (calculatedCalorie == null && exerciseType != null) {
            calculatedCalorie = exerciseType.getExerciseCalorie() * exercise.getExerciseTime();
        }

        return ExerciseListResponse.builder()
                .exerciseId(exercise.getExerciseId())
                .userId(exercise.getUserId())
                .exerciseTypeId(exercise.getExerciseTypeId())
                .exerciseName(exerciseName)
                .exerciseCategoryName(categoryName)
                .exerciseDate(exercise.getExerciseDate())
                .exerciseTime(exercise.getExerciseTime())
                .exerciseCalorie(calculatedCalorie)
                .createdAt(exercise.getCreatedAt())
                .build();
    }

    // 2. 운동 기록 전체 및 상세 조회 Mapper
    // Exercise 엔티티와 관련 정보를 ExerciseListResponse DTO 로 변환
    public ExerciseListResponse toListResponse(
            Exercise exercise,
            ExerciseType exerciseType,
            ExerciseCategory exerciseCategory) {

        // 저장된 칼로리 값 가져오기
        Integer storedCalorie = exercise.getExerciseCalorie();

        // 예상 칼로리 계산 (운동 종류의 분당 칼로리 × 운동 시간)
        Integer expectedCalorie = null;
        if (exerciseType != null) {
            expectedCalorie = exerciseType.getExerciseCalorie() * exercise.getExerciseTime();
        }

        // 저장된 값이 없거나 예상 값과 다르면 예상 값 사용
        Integer finalCalorie = storedCalorie;
        if (storedCalorie == null || storedCalorie == 0 ||
                (expectedCalorie != null && !expectedCalorie.equals(storedCalorie))) {
            finalCalorie = expectedCalorie;
        }

        return toListResponse(exercise, exerciseType, exerciseCategory, finalCalorie);
    }
}