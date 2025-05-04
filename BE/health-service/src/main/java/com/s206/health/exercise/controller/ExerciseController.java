package com.s206.health.exercise.controller;

import com.s206.health.exercise.dto.request.ExerciseCreateRequest;
import com.s206.health.exercise.dto.request.ExerciseUpdateRequest;
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

    // 특정 사용자의 운동 기록 전체 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<ExerciseListResponse>> getAllExercisesByUser(@PathVariable Integer userId) {
        List<ExerciseListResponse> exercises = exerciseService.getAllExercisesByUser(userId);
        return ResponseEntity.ok(exercises);
    }

    // 특정 사용자의 운동 기록 상세 조회
    @GetMapping("/{userId}/{exerciseId}")
    public ResponseEntity<ExerciseListResponse> getExerciseById(
            @PathVariable Integer userId,
            @PathVariable Integer exerciseId) {
        ExerciseListResponse exercise = exerciseService.getExerciseById(userId, exerciseId);
        return ResponseEntity.ok(exercise);
    }

    // 운동 기록 생성
    @PostMapping("/{userId}")
    public ResponseEntity<ExerciseListResponse> createExercise(
            @PathVariable Integer userId,
            @RequestBody ExerciseCreateRequest request) {
        ExerciseListResponse response = exerciseService.createExercise(userId, request);
        return ResponseEntity.ok(response);
    }

    // 운동 기록 수정
    @PutMapping("/{userId}/{exerciseId}")
    public ResponseEntity<ExerciseListResponse> updateExercise(
            @PathVariable Integer userId,
            @PathVariable Integer exerciseId,
            @RequestBody ExerciseUpdateRequest request) {
        ExerciseListResponse response = exerciseService.updateExercise(userId, exerciseId, request);
        return ResponseEntity.ok(response);
    }

    // 운동 기록 삭제
    @DeleteMapping("/{userId}/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(
            @PathVariable Integer userId,
            @PathVariable Integer exerciseId) {
        exerciseService.deleteExercise(userId, exerciseId);
        return ResponseEntity.ok().build();
    }
}