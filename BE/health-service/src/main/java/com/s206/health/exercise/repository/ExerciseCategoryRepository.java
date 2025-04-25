package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseCategoryRepository extends JpaRepository<ExerciseCategory, Integer> {
}
