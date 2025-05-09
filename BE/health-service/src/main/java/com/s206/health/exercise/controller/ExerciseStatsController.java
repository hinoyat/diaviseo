package com.s206.health.exercise.controller;

import com.s206.common.dto.ResponseDto;
import com.s206.health.exercise.dto.response.DailyExerciseStatsResponse;
import com.s206.health.exercise.dto.response.MonthlyExerciseStatsResponse;
import com.s206.health.exercise.dto.response.TodayExerciseStatsResponse;
import com.s206.health.exercise.dto.response.WeeklyExerciseStatsResponse;
import com.s206.health.exercise.service.ExerciseStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseStatsController {

    private final ExerciseStatsService exerciseStatsService;

    // 오늘의 운동 조회
    @GetMapping("/today")
    public ResponseEntity<ResponseDto<TodayExerciseStatsResponse>> getTodayStats(
            @RequestHeader("X-USER-ID") Integer userId,
            @RequestParam(required = false) String date) {
        TodayExerciseStatsResponse response = exerciseStatsService.getTodayStats(userId, date);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK,"오늘의 운동 조회 성공",response));
    }

    // 일별 운동 조회 (최근 7일)
    @GetMapping("/daily")
    public ResponseEntity<ResponseDto<DailyExerciseStatsResponse>> getDailyStats(
            @RequestHeader("X-User-ID") Integer userId) {
        DailyExerciseStatsResponse response = exerciseStatsService.getDailyStats(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK,"일별 운동 조회 성공",response));
    }

    // 주별 운동 조회 (최근 7주)
    @GetMapping("/weekly")
    public ResponseEntity<ResponseDto<WeeklyExerciseStatsResponse>> getWeeklyStats(
            @RequestHeader("X-USER-ID") Integer userId) {
        WeeklyExerciseStatsResponse response = exerciseStatsService.getWeeklyStats(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK,"주별 운동 조회 성공",response));
    }

    // 월별 운동 조회 (최근 7개월)
    @GetMapping("/monthly")
    public ResponseEntity<ResponseDto<MonthlyExerciseStatsResponse>> getMonthlyStats(
            @RequestHeader("X-USER-ID") Integer userId) {
        MonthlyExerciseStatsResponse response = exerciseStatsService.getMonthlyStats(userId);
        return ResponseEntity.ok(ResponseDto.success(HttpStatus.OK,"월별 운동 조회 성공",response));
    }
}
