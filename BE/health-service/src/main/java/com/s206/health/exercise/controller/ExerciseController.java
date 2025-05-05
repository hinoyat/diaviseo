package com.s206.health.exercise.controller;

import com.s206.health.exercise.dto.request.ExerciseCreateRequest;
import com.s206.health.exercise.dto.request.ExerciseUpdateRequest;
import com.s206.health.exercise.dto.response.ExerciseCategoryResponse;
import com.s206.health.exercise.dto.response.ExerciseListResponse;
import com.s206.health.exercise.dto.response.ExerciseTypeResponse;
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
    public ResponseEntity<List<ExerciseListResponse>> getAllExercisesByUser(
            @RequestHeader("X-USER-ID") Integer userId) {
        List<ExerciseListResponse> exercises = exerciseService.getAllExercisesByUser(userId);
        return ResponseEntity.ok(exercises);
    }

    // 운동 기록 상세 조회
    @GetMapping("/{exerciseId}")
    public ResponseEntity<ExerciseListResponse> getExerciseById(
            @RequestHeader("X-USER-ID") Integer userId,
            @PathVariable Integer exerciseId) {
        ExerciseListResponse exercise = exerciseService.getExerciseById(userId, exerciseId);
        return ResponseEntity.ok(exercise);
    }

    // 운동 기록 생성
    @PostMapping
    public ResponseEntity<ExerciseListResponse> createExercise(
            @RequestHeader("X-USER-ID") Integer userId,
            @RequestBody ExerciseCreateRequest request) {
        ExerciseListResponse response = exerciseService.createExercise(userId, request);
        return ResponseEntity.ok(response);
    }

    // 운동 기록 수정
    @PutMapping("/{exerciseId}")
    public ResponseEntity<ExerciseListResponse> updateExercise(
            @RequestHeader("X-USER-ID") Integer userId,
            @PathVariable Integer exerciseId,
            @RequestBody ExerciseUpdateRequest request) {
        ExerciseListResponse response = exerciseService.updateExercise(userId, exerciseId, request);
        return ResponseEntity.ok(response);
    }

    // 운동 기록 삭제
    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(
            @RequestHeader("X-USER-ID") Integer userId,
            @PathVariable Integer exerciseId) {
        exerciseService.deleteExercise(userId, exerciseId);
        return ResponseEntity.ok().build();
    }

    // 운동 카테고리 조회
    @GetMapping("/category")
    public ResponseEntity<List<ExerciseCategoryResponse>> getAllCategories() {
        List<ExerciseCategoryResponse> categories = exerciseService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // 운동 카테고리별 운동 조회
    @GetMapping("/category/{exerciseCategoryId}")
    public ResponseEntity<List<ExerciseTypeResponse>> getExercisesByCategory(
            @PathVariable Integer exerciseCategoryId) {
        List<ExerciseTypeResponse> exercises = exerciseService.getExercisesByCategory(exerciseCategoryId);
        return ResponseEntity.ok(exercises);
    }
}