package com.s206.health.condition.allergy.repository;

import com.s206.health.condition.allergy.entity.UserAllergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAllergyRepository extends JpaRepository<UserAllergy, Integer> {

    Optional<UserAllergy> findByUserIdAndAllergyId(Integer userId, Long allergyId);

    List<UserAllergy> findAllByUserId(Integer userId);

    boolean existsByUserIdAndAllergyId(Integer userId, Long allergyId);
}
