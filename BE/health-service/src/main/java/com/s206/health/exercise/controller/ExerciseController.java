package com.s206.health.exercise.controller;

import com.s206.health.exercise.dto.request.ExerciseCreateRequest;
import com.s206.health.exercise.dto.response.ExerciseListResponse;
import com.s206.health.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    // 운동 기록 전체 조회
    @GetMapping
    public ResponseEntity<List<ExerciseListResponse>> getAllExercises() {
        List<ExerciseListResponse> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }

    // 운동 기록 생성
    @PostMapping
    public ResponseEntity<ExerciseListResponse> createExercise(@RequestBody ExerciseCreateRequest request) {
        ExerciseListResponse response = exerciseService.createExercise(request);
        return ResponseEntity.ok(response);
    }
}
