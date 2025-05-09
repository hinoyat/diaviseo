package com.s206.health.step.controller;

import com.s206.health.step.dto.request.StepCreateRequest;
import com.s206.health.step.dto.response.StepResponse;
import com.s206.health.step.service.StepService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class StepController {

    private final StepService stepService;

    // 걸음 수 등록
    @PostMapping("/step")
    public ResponseEntity<StepResponse> createStepCount(
            @RequestHeader("X-USER-ID") Integer userId,
            @RequestBody StepCreateRequest request) {

        StepResponse response = stepService.createOrUpdateStepCount(userId, request);
        return ResponseEntity.ok(response);
    }

    // 특정 날짜 걸음 수 조회 (step?date=2025-05-01)
    // 날짜 파라미터가 없으면 오늘 날짜 걸음 수 조회
    @GetMapping("/step")
    public ResponseEntity<StepResponse> getStepCount(
            @RequestHeader("X-USER-ID") Integer userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // 날짜 파라미터가 없으면 오늘 날짜로 설정
        if (date == null) {
            date = LocalDate.now();
        }

        StepResponse step = stepService.getStepCountByDate(userId, date);
        return ResponseEntity.ok(step);
    }

    // 전체 걸음 수 기록 조회
    @GetMapping("/step/all")
    public ResponseEntity<List<StepResponse>> getAllStepCounts(
            @RequestHeader("X-USER-ID") Integer userId) {

        List<StepResponse> steps = stepService.getAllStepCounts(userId);
        return ResponseEntity.ok(steps);
    }

    // 주간 평균 걸음 수 조회
    @GetMapping("/step-avg")
    public ResponseEntity<Integer> getWeeklyAverageStepCount(
            @RequestHeader("X-USER-ID") Integer userId) {

        Integer averageSteps = stepService.getWeeklyAverageStepCount(userId);
        return ResponseEntity.ok(averageSteps);
    }
}