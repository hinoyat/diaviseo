package com.s206.health.exercise.service;

import com.s206.health.exercise.dto.response.ExerciseListResponse;
import com.s206.health.exercise.entity.Exercise;
import com.s206.health.exercise.entity.ExerciseCategory;
import com.s206.health.exercise.entity.ExerciseType;
import com.s206.health.exercise.repository.ExerciseCategoryRepository;
import com.s206.health.exercise.repository.ExerciseRepository;
import com.s206.health.exercise.repository.ExerciseTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;

    @Transactional(readOnly = true)
    public List<ExerciseListResponse> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findByIsDeletedFalse();

        // 모든 운동 유형 정보를 가져와서 Map 으로 변환 (ID -> ExerciseType)
        Map<Long, ExerciseType> exerciseTypeMap = exerciseTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseType::getExerciseTypeId, type -> type));

        // 모든 운동 카테고리 정보를 가져와서 Map 으로 변환 (ID -> ExerciseCategory)
        Map<Long, ExerciseCategory> exerciseCategoryMap = exerciseCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseCategory::getExerciseCategoryId, category->category));

        // 운동 기록과 운동 유형, 카테고리 정보를 조합하여 응답 DTO 생성
        return exercises.stream()
                .map(exercise -> {
                    // 운동 유형 정보 조회
                    ExerciseType exerciseType = exerciseTypeMap.get(exercise.getExerciseTypeId());
                    String exerciseName = "";
                    int exerciseCalorie = 0;
                    String exerciseCategoryName = "";

                    if (exerciseType != null) {
                        exerciseName = exerciseType.getExerciseName();
                        exerciseCalorie = exerciseType.getExerciseCalorie();

                        // 카테고리 정보 조회
                        ExerciseCategory exerciseCategory = exerciseCategoryMap.get(exerciseType.getExerciseCategoryId());
                        if (exerciseCategory != null) {
                            exerciseCategoryName = exerciseCategory.getExerciseCategoryName();
                        }
                    }

                    return ExerciseListResponse.builder()
                            .exerciseId(exercise.getExerciseId())
                            .userId(exercise.getUserId())
                            .exerciseTypeId(exercise.getExerciseTypeId())
                            .exerciseName(exerciseName)
                            .exerciseCategoryName(exerciseCategoryName)
                            .exerciseDate(exercise.getExerciseDate())
                            .exerciseTime(exercise.getExerciseTime())
                            .exerciseCalorie(exerciseCalorie)
                            .createdAt(exercise.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
