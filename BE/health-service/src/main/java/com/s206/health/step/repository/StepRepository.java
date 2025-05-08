package com.s206.health.step.repository;


import com.s206.health.step.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StepRepository extends JpaRepository<Step, Integer> {

    // 사용자의 특정 날짜 걸음 수 조회
    @Query("SELECT s FROM Step s WHERE s.userId = :userId AND s.stepDate = :date AND s.isDeleted = false")
    Optional<Step> findStepByStepDate(
            @Param("userId") Integer userId,
            @Param("date")LocalDate date);

    // 특정 사용자의 모든 걸음 수 기록 조회
    @Query("SELECT s FROM Step s WHERE s.userId = :userId AND s.isDeleted = false ORDER BY s.stepDate DESC")
    List<Step> findAllStepsByUser(
            @Param("userId") Integer userId);

    // 특정 사용자의 날짜 범위 내 걸음 수 조회
    @Query("SELECT s FROM Step s WHERE s.userId = :userId AND s.stepDate BETWEEN :startDate AND :endDate AND s.isDeleted = false ORDER BY s.stepDate DESC")
    List<Step> findStepsByDateRange(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
