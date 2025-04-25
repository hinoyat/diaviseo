package com.s206.health.exercise.controller;

import com.s206.health.exercise.dto.response.ExerciseListResponse;
import com.s206.health.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<List<ExerciseListResponse>> getAllExercises() {
        List<ExerciseListResponse> exercises = exerciseService.getAllExercises();
        return ResponseEntity.ok(exercises);
    }
}
