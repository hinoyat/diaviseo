package com.s206.health.condition.disease.repository;

import com.s206.health.condition.disease.entity.UserDisease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDiseaseRepository extends JpaRepository<UserDisease, Integer> {

    Optional<UserDisease> findByUserIdAndDiseaseId(Integer userId, Long diseaseId);

    List<UserDisease> findAllByUserId(Integer userId);

    boolean existsByUserIdAndDiseaseId(Integer userId, Long diseaseId);
}
