package com.s206.health.exercise.controller;

import com.s206.health.exercise.dto.response.DailyExerciseStatsResponse;
import com.s206.health.exercise.dto.response.MonthlyExerciseStatsResponse;
import com.s206.health.exercise.dto.response.TodayExerciseStatsResponse;
import com.s206.health.exercise.dto.response.WeeklyExerciseStatsResponse;
import com.s206.health.exercise.service.ExerciseStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseStatsController {

    private final ExerciseStatsService exerciseStatsService;

    // 오늘의 운동 조회
    @GetMapping("/today")
    public ResponseEntity<TodayExerciseStatsResponse> getTodayStats(
            @RequestHeader("X-USER-ID") Integer userId) {
        TodayExerciseStatsResponse response = exerciseStatsService.getTodayStats(userId);
        return ResponseEntity.ok(response);
    }

    // 일별 운동 조회 (최근 7일)
    @GetMapping("/daily")
    public ResponseEntity<DailyExerciseStatsResponse> getDailyStats(
            @RequestHeader("X-User-ID") Integer userId) {
        DailyExerciseStatsResponse response = exerciseStatsService.getDailyStats(userId);
        return ResponseEntity.ok(response);
    }

    // 주별 운동 조회 (최근 7주)
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyExerciseStatsResponse> getWeeklyStats(
            @RequestHeader("X-USER-ID") Integer userId) {
        WeeklyExerciseStatsResponse response = exerciseStatsService.getWeeklyStats(userId);
        return ResponseEntity.ok(response);
    }

    // 월별 운동 조회 (최근 7개월)
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyExerciseStatsResponse> getMonthlyStats(
            @RequestHeader("X-USER-ID") Integer userId) {
        MonthlyExerciseStatsResponse response = exerciseStatsService.getMonthlyStats(userId);
        return ResponseEntity.ok(response);
    }
}
