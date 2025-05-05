package com.s206.health.nutrition.foodset.repository;

import com.s206.health.nutrition.foodset.entity.FoodSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodSetRepository extends JpaRepository<FoodSet, Integer> {
}
