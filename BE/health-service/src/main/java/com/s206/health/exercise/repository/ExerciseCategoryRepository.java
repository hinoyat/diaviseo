package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseCategoryRepository extends JpaRepository<ExerciseCategory, Integer> {

    List<ExerciseCategory> findByIsDeletedFalse();
}
