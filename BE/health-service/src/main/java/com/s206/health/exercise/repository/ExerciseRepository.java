package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    // 특정 사용자의 운동 기록 전체 조회 (삭제되지 않은 것만)
    List<Exercise> findByUserIdAndIsDeletedFalse(Integer userId);

    // 특정 사용자의 특정 운동 기록 조회
    Optional<Exercise> findByExerciseIdAndUserId(Integer exerciseId, Integer userId);

    // 특정 날짜의 운동 기록 조회
    List<Exercise> findByUserIdAndExerciseDateBetweenAndIsDeletedFalseOrderByExerciseDateDesc(
            Integer userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // 오늘 날짜의 운동 통계 요약
    @Query("SELECT COUNT(e), SUM(e.exerciseCalorie), SUM(e.exerciseTime) " +
            "FROM Exercise e " +
            "WHERE e.userId = :userId AND e.isDeleted = false " +
            "AND e.exerciseDate BETWEEN :startDate AND :endDate")
    Object[] getTodayStats(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}