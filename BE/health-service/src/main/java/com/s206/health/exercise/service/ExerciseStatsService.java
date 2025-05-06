package com.s206.health.exercise.service;

import com.s206.health.exercise.dto.response.TodayExerciseStatsResponse;
import com.s206.health.exercise.entity.Exercise;
import com.s206.health.exercise.entity.ExerciseCategory;
import com.s206.health.exercise.entity.ExerciseType;
import com.s206.health.exercise.repository.ExerciseCategoryRepository;
import com.s206.health.exercise.repository.ExerciseRepository;
import com.s206.health.exercise.repository.ExerciseTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseStatsService {

    private final ExerciseTypeRepository exerciseTypeRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final ExerciseRepository exerciseRepository;

    // 오늘의 운동 통계 조회
    @Transactional(readOnly = true)
    public TodayExerciseStatsResponse getTodayStats(Integer userId) {
        // 1. 오늘 날짜 범위 계산 (00:00:00 ~ 23:59:59)
        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = today.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(LocalTime.MAX);

        // 오늘의 운동 기록 조회
        List<Exercise> todayExercises = exerciseRepository
                .findByUserIdAndExerciseDateBetweenAndIsDeletedFalseOrderByExerciseDateDesc(
                        userId, startDateTime, endDateTime);

        // 빈 목록 체크
        if (todayExercises == null) {
            todayExercises = new ArrayList<>();
        }

        // 운동 통계 직접 계산 (쿼리 결과에 의존하지 않음)
        int exerciseCount = todayExercises.size();
        int totalCalories = todayExercises.stream()
                .mapToInt(Exercise::getExerciseCalorie)
                .sum();
        int totalTime = todayExercises.stream()
                .mapToInt(Exercise::getExerciseTime)
                .sum();

        // 4. 운동 타입과 카테고리 정보 로드
        Map<Integer, ExerciseType> exerciseTypeMap = exerciseTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseType::getExerciseTypeId, type -> type));

        Map<Integer, ExerciseCategory> exerciseCategoryMap = exerciseCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseCategory::getExerciseCategoryId, category -> category));

        // 5. 운동 상세 정보 구성
        List<TodayExerciseStatsResponse.ExerciseDetail> exerciseDetails = todayExercises.stream()
                .map(exercise -> {
                    ExerciseType type = exerciseTypeMap.get(exercise.getExerciseTypeId());
                    String categoryName = "";

                    if (type != null) {
                        ExerciseCategory category = exerciseCategoryMap.get(type.getExerciseCategoryId());
                        if (category != null) {
                            categoryName = category.getExerciseCategoryName();
                        }
                    }

                    return TodayExerciseStatsResponse.ExerciseDetail.builder()
                            .exerciseId(exercise.getExerciseId())
                            .exerciseName(type != null ? type.getExerciseName() : "")
                            .categoryName(categoryName)
                            .exerciseDate(exercise.getExerciseDate())
                            .exerciseTime(exercise.getExerciseTime())
                            .exerciseCalorie(exercise.getExerciseCalorie())
                            .build();
                })
                .collect(Collectors.toList());

        // 6. 응답 DTO 구성
        return TodayExerciseStatsResponse.builder()
                .date(startDateTime)
                .totalCalories(totalCalories)
                .totalExerciseTime(totalTime)
                .exerciseCount(exerciseCount)
                .exercises(exerciseDetails)
                .build();
    }
}
