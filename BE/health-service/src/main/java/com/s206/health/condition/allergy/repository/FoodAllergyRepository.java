package com.s206.health.condition.allergy.repository;

import com.s206.health.condition.allergy.entity.FoodAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodAllergyRepository extends JpaRepository<FoodAllergy, Long> {
}
