package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Integer> {
}
