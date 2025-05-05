package com.s206.health.exercise.favorite.service;

import com.s206.common.exception.types.NotFoundException;
import com.s206.health.exercise.entity.ExerciseType;
import com.s206.health.exercise.favorite.dto.response.FavoriteExerciseResponse;
import com.s206.health.exercise.favorite.dto.response.FavoriteToggleResponse;
import com.s206.health.exercise.favorite.entity.FavoriteExercise;
import com.s206.health.exercise.favorite.repository.FavoriteExerciseRepository;
import com.s206.health.exercise.repository.ExerciseCategoryRepository;
import com.s206.health.exercise.repository.ExerciseTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteExerciseService {

    private final FavoriteExerciseRepository favoriteExerciseRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;

    @Transactional
    public FavoriteToggleResponse toggleFavorite(Integer userId, Integer exerciseTypeId) {
        // 즐겨찾기 존재 여부 확인
        FavoriteExercise favorite = favoriteExerciseRepository
                .findByUserIdAndExerciseTypeExerciseTypeId(userId, exerciseTypeId)
                .orElse(null);

        // 이미 즐겨찾기에 있으면 해제
        if (favorite != null) {
            favoriteExerciseRepository.delete(favorite);
            return FavoriteToggleResponse.toDto(favorite, false);
        }

        // 운동 타입 존재 확인
        ExerciseType exerciseType = exerciseTypeRepository.findById(exerciseTypeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동입니다."));

        // 새로운 즐겨찾기 추가
        FavoriteExercise newFavorite = favoriteExerciseRepository.save(FavoriteExercise.builder()
                .userId(userId)
                .exerciseType(exerciseType)
                .build()
        );
        return FavoriteToggleResponse.toDto(newFavorite, true);
    }

    // 사용자의 즐겨찾기 운동 목록 조회
    // N+1 문제 해결을 위한 카테고리 정보 미리 로딩
    @Transactional(readOnly = true)
    public List<FavoriteExerciseResponse> getFavoriteExercises(Integer userId) {
        // 사용자의 즐겨찾기 조회
        List<FavoriteExercise> favorites = favoriteExerciseRepository.findAllByUserId(userId);

        // 카테고리 정보 미리 로딩
        Map<Integer, String> categoryMap = exerciseCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        category -> category.getExerciseCategoryId(),
                        category -> category.getExerciseCategoryName()
                ));

        // Response DTO 로 변환
        return favorites.stream()
                .map(favorite -> {
                    String categoryName = categoryMap.get(favorite.getExerciseType().getExerciseCategoryId());

                    return FavoriteExerciseResponse.builder()
                            .exerciseTypeId(favorite.getExerciseType().getExerciseTypeId())
                            .exerciseName(favorite.getExerciseType().getExerciseName())
                            .calorie(favorite.getExerciseType().getExerciseCalorie())
                            .categoryName(categoryName)
                            .createdAt(favorite.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
