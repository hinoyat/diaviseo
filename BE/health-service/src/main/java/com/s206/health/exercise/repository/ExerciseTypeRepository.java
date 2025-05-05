package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Integer> {

    List<ExerciseType> findByExerciseCategoryIdAndIsDeletedFalse(Integer exerciseCategoryId);
}
