package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Integer> {

    List<ExerciseType> findByExerciseCategoryIdAndIsDeletedFalse(Integer exerciseCategoryId);

    // exerciseNumber 사용
    Optional<ExerciseType> findByExerciseNumberAndIsDeletedFalse(Integer exerciseNumber);
}
