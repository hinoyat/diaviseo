package com.s206.health.nutrition.standard.loader;

import com.s206.health.nutrition.standard.entity.AgeGroup;
import com.s206.health.nutrition.standard.entity.FemaleNutrientStandard;
import com.s206.health.nutrition.standard.entity.MaleNutrientStandard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NutrientStandardLoader {

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadData() {
        // 이미 데이터가 존재하는지 확인
        if (isDataLoaded()) {
            return;
        }

        // 남성 데이터 로드
        loadMaleData();

        // 여성 데이터 로드
        loadFemaleData();
    }

    private boolean isDataLoaded() {
        Long maleCount = entityManager.createQuery(
                "SELECT COUNT(m) FROM MaleNutrientStandard m", Long.class).getSingleResult();
        Long femaleCount = entityManager.createQuery(
                "SELECT COUNT(f) FROM FemaleNutrientStandard f", Long.class).getSingleResult();
        return maleCount > 0 && femaleCount > 0;
    }

    private void loadMaleData() {
        // 10대 남성
        saveMaleStandard(AgeGroup.TEENS, 2500, 2700, 100, 130, 50, 65, 12.0, 14.0, 1500);

        // 20대 남성
        saveMaleStandard(AgeGroup.TWENTIES, 2600, 2600, 100, 130, 50, 65, 13.0, 13.0, 1500);

        // 30대 남성
        saveMaleStandard(AgeGroup.THIRTIES, 2500, 2500, 100, 130, 50, 65, 11.5, 14.0, 1500);

        // 40대 남성
        saveMaleStandard(AgeGroup.FORTIES, 2500, 2500, 100, 130, 50, 65, 11.5, 14.0, 1500);

        // 50대 남성
        saveMaleStandard(AgeGroup.FIFTIES, 2200, 2200, 100, 130, 50, 60, 9.0, 9.0, 1500);

        // 60대 남성
        saveMaleStandard(AgeGroup.SIXTIES, 2000, 2000, 100, 130, 50, 60, 7.0, 7.0, 1300);
    }

    private void loadFemaleData() {
        // 10대 여성
        saveFemaleStandard(AgeGroup.TEENS, 2000, 2000, 100, 130, 45, 55, 10.0, 10.0, 1500);

        // 20대 여성
        saveFemaleStandard(AgeGroup.TWENTIES, 2000, 2000, 100, 130, 45, 55, 10.0, 10.0, 1500);

        // 30대 여성
        saveFemaleStandard(AgeGroup.THIRTIES, 1900, 1900, 100, 130, 40, 50, 8.5, 10.0, 1500);

        // 40대 여성
        saveFemaleStandard(AgeGroup.FORTIES, 1900, 1900, 100, 130, 40, 50, 8.5, 10.0, 1500);

        // 50대 여성
        saveFemaleStandard(AgeGroup.FIFTIES, 1700, 1700, 100, 130, 40, 50, 7.0, 7.0, 1500);

        // 60대 여성
        saveFemaleStandard(AgeGroup.SIXTIES, 1600, 1600, 100, 130, 40, 50, 4.5, 4.5, 1300);
    }

    private void saveMaleStandard(AgeGroup ageGroup,
                                  Integer caloriesMin, Integer caloriesMax,
                                  Integer carbohydratesMin, Integer carbohydratesMax,
                                  Integer proteinMin, Integer proteinMax,
                                  Double fatMin, Double fatMax,
                                  Integer sodium) {
        MaleNutrientStandard standard = new MaleNutrientStandard();
        standard.setAgeGroup(ageGroup);
        standard.setCaloriesMin(caloriesMin);
        standard.setCaloriesMax(caloriesMax);
        standard.setCarbohydratesMin(carbohydratesMin);
        standard.setCarbohydratesMax(carbohydratesMax);
        standard.setProteinMin(proteinMin);
        standard.setProteinMax(proteinMax);
        standard.setFatMin(fatMin);
        standard.setFatMax(fatMax);
        standard.setSodium(sodium);

        entityManager.persist(standard);
    }

    private void saveFemaleStandard(AgeGroup ageGroup,
                                    Integer caloriesMin, Integer caloriesMax,
                                    Integer carbohydratesMin, Integer carbohydratesMax,
                                    Integer proteinMin, Integer proteinMax,
                                    Double fatMin, Double fatMax,
                                    Integer sodium) {
        FemaleNutrientStandard standard = new FemaleNutrientStandard();
        standard.setAgeGroup(ageGroup);
        standard.setCaloriesMin(caloriesMin);
        standard.setCaloriesMax(caloriesMax);
        standard.setCarbohydratesMin(carbohydratesMin);
        standard.setCarbohydratesMax(carbohydratesMax);
        standard.setProteinMin(proteinMin);
        standard.setProteinMax(proteinMax);
        standard.setFatMin(fatMin);
        standard.setFatMax(fatMax);
        standard.setSodium(sodium);

        entityManager.persist(standard);
    }
}