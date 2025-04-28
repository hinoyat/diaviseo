package com.s206.health.exercise.service;

import com.s206.health.exercise.dto.request.ExerciseCreateRequest;
import com.s206.health.exercise.dto.response.ExerciseListResponse;
import com.s206.health.exercise.entity.Exercise;
import com.s206.health.exercise.entity.ExerciseCategory;
import com.s206.health.exercise.entity.ExerciseType;
import com.s206.health.exercise.mapper.ExerciseMapper;
import com.s206.health.exercise.repository.ExerciseCategoryRepository;
import com.s206.health.exercise.repository.ExerciseRepository;
import com.s206.health.exercise.repository.ExerciseTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final ExerciseMapper exerciseMapper;

    // 운동 기록 전체 조회
    @Transactional(readOnly = true)
    public List<ExerciseListResponse> getAllExercises() {
        List<Exercise> exercises = exerciseRepository.findByIsDeletedFalse();

        // 모든 운동 유형 정보를 가져와서 Map 으로 변환 (ID -> ExerciseType)
        Map<Integer, ExerciseType> exerciseTypeMap = exerciseTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseType::getExerciseTypeId, type -> type));

        // 모든 운동 카테고리 정보를 가져와서 Map 으로 변환 (ID -> ExerciseCategory)
        Map<Integer, ExerciseCategory> exerciseCategoryMap = exerciseCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseCategory::getExerciseCategoryId, category->category));

        // 운동 기록과 운동 유형, 카테고리 정보를 조합하여 응답 DTO 생성
        return exercises.stream()
                .map(exercise -> {
                    // 운동 유형 및 카테고리 정보 조회
                    ExerciseType exerciseType = exerciseTypeMap.get(exercise.getExerciseTypeId());
                    ExerciseCategory exerciseCategory = null;

                    if (exerciseType != null) {
                        exerciseCategory = exerciseCategoryMap.get(exerciseType.getExerciseCategoryId());
                    }

                    // 매퍼를 사용하여 DTO 변환
                    return exerciseMapper.toListResponse(exercise, exerciseType, exerciseCategory);
                })
                .collect(Collectors.toList());
    }

    // 운동 기록 상세 조회
    @Transactional(readOnly = true)
    public ExerciseListResponse getExerciseById(Integer exerciseId) {
        // 1. 운동 기록 조회
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 운동 기록입니다."));

        // 삭제된 운동 기록인 경우 예외 발생
        if (exercise.getIsDeleted()) {
            throw new IllegalArgumentException("삭제된 운동 기록입니다.");
        }

        // 2. 운동 종류 정보 조회
        ExerciseType exerciseType = exerciseTypeRepository.findById(exercise.getExerciseTypeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 운동 종류입니다."));

        // 3. 운동 카테고리 정보 조회
        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(exerciseType.getExerciseCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 운동 카테고리입니다."));

        // 4. 매퍼를 사용하여 응답 DTO 생성 및 반환
        return exerciseMapper.toListResponse(exercise, exerciseType, exerciseCategory);
    }

    // 운동 기록 생성
    @Transactional
    public ExerciseListResponse createExercise(ExerciseCreateRequest request) {
        // 1. 운동 종류 존재 여부 확인
        ExerciseType exerciseType = exerciseTypeRepository.findById(request.getExerciseTypeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 운동 종류입니다."));

        // 2. 운동 기록 생성 (운동 시간에 비례한 칼로리 계산)
        Integer totalCalorie = exerciseType.getExerciseCalorie() * request.getExerciseTime();

        Exercise exercise = Exercise.builder()
                .userId(request.getUserId())
                .exerciseTypeId(request.getExerciseTypeId())
                .exerciseDate(request.getExerciseDate())
                .exerciseTime(request.getExerciseTime())
                .exerciseCalorie(totalCalorie)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // 3. 운동 기록 저장
        Exercise savedExercise = exerciseRepository.save(exercise);

        // 4. 카테고리 정보 조회
        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(exerciseType.getExerciseCategoryId())
                .orElse(null);

        // 5. 매퍼를 사용하여 응답 DTO 생성 및 반환
        return exerciseMapper.toListResponse(savedExercise, exerciseType, exerciseCategory, totalCalorie);
    }
}
