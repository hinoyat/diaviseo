package com.s206.health.nutrition.food.repository;

import com.s206.health.nutrition.food.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Integer> {

}
