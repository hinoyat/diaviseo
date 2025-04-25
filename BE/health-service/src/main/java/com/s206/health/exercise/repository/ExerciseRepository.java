package com.s206.health.exercise.repository;

import com.s206.health.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    // Exercise 전체 조회 (삭제되지 않은 것만)
    List<Exercise> findByIsDeletedFalse();
}
