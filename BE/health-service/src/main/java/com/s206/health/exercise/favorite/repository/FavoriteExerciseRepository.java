package com.s206.health.exercise.favorite.repository;

import com.s206.health.exercise.favorite.entity.FavoriteExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteExerciseRepository extends JpaRepository<FavoriteExercise, Integer> {
    // 해당 운동 즐겨찾기 조회
    // ExerciseType 과의 관계를 활용한 쿼리 메서드
    Optional<FavoriteExercise> findByUserIdAndExerciseTypeExerciseTypeId(Integer userId, Integer exerciseTypeId);

    // 사용자의 모든 즐겨찾기 조회
    List<FavoriteExercise> findAllByUserId(Integer userId);
}