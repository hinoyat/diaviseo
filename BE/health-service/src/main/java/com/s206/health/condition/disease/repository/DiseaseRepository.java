package com.s206.health.condition.disease.repository;

import com.s206.health.condition.disease.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
}
