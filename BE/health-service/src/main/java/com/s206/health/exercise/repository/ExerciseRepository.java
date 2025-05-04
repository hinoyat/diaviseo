package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    // 특정 사용자의 운동 기록 전체 조회 (삭제되지 않은 것만)
    List<Exercise> findByUserIdAndIsDeletedFalse(Integer userId);

    // 특정 사용자의 특정 운동 기록 조회
    Optional<Exercise> findByExerciseIdAndUserId(Integer exerciseId, Integer userId);
}