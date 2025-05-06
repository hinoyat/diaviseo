package com.s206.health.exercise.controller;

import com.s206.health.exercise.dto.response.TodayExerciseStatsResponse;
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

    // 오늘의 운동 통계
    @GetMapping("/today")
    public ResponseEntity<TodayExerciseStatsResponse> getTodayStats(
            @RequestHeader("X-USER-ID") Integer userId) {
        TodayExerciseStatsResponse response = exerciseStatsService.getTodayStats(userId);
        return ResponseEntity.ok(response);
    }
}
