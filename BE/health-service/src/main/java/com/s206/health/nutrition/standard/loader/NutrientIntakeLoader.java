package com.s206.health.nutrition.standard.loader;

import com.s206.health.nutrition.standard.entity.AgeGroup;
import com.s206.health.nutrition.standard.entity.FemaleNutrientIntake;
import com.s206.health.nutrition.standard.entity.MaleNutrientIntake;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NutrientIntakeLoader {

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
                "SELECT COUNT(m) FROM MaleNutrientIntake m", Long.class).getSingleResult();
        Long femaleCount = entityManager.createQuery(
                "SELECT COUNT(f) FROM FemaleNutrientIntake f", Long.class).getSingleResult();
        return maleCount > 0 && femaleCount > 0;
    }

    private void loadMaleData() {
        // 전체 평균
        saveMaleIntake(AgeGroup.ALL, 2103.02, 80.7, 58.67, 287.0, 60.81, 3695.95);

        // 10대
        saveMaleIntake(AgeGroup.TEENS, 2219.39, 90.22, 69.46, 299.96, 73.91, 3387.31);

        // 20대
        saveMaleIntake(AgeGroup.TWENTIES, 2295.8, 94.03, 74.04, 284.05, 62.4, 3807.43);

        // 30대
        saveMaleIntake(AgeGroup.THIRTIES, 2224.94, 85.64, 65.85, 287.41, 61.32, 3957.7);

        // 40대
        saveMaleIntake(AgeGroup.FORTIES, 2224.94, 85.64, 65.85, 287.41, 61.32, 3957.7);

        // 50대
        saveMaleIntake(AgeGroup.FIFTIES, 2092.18, 77.38, 51.71, 296.53, 57.98, 3954.47);

        // 60대
        saveMaleIntake(AgeGroup.SIXTIES, 2092.18, 77.38, 51.71, 296.53, 57.98, 3954.47);

        // 65세 이상
        saveMaleIntake(AgeGroup.ELDERLY, 1869.5, 69.29, 41.25, 289.99, 54.85, 3513.95);
    }

    private void loadFemaleData() {
        // 전체 평균
        saveFemaleIntake(AgeGroup.ALL, 1565.34, 59.01, 44.27, 225.29, 55.77, 2575.67);

        // 10대
        saveFemaleIntake(AgeGroup.TEENS, 1634.24, 61.26, 51.27, 228.86, 62.48, 2461.11);

        // 20대
        saveFemaleIntake(AgeGroup.TWENTIES, 1704.86, 66.25, 56.71, 214.58, 58.6, 2540.07);

        // 30대
        saveFemaleIntake(AgeGroup.THIRTIES, 1635.96, 62.35, 49.88, 222.58, 56.51, 2773.8);

        // 40대
        saveFemaleIntake(AgeGroup.FORTIES, 1635.96, 62.35, 49.88, 222.58, 56.51, 2773.8);

        // 50대
        saveFemaleIntake(AgeGroup.FIFTIES, 1567.96, 59.55, 41.4, 234.21, 57.35, 2721.39);

        // 60대
        saveFemaleIntake(AgeGroup.SIXTIES, 1567.96, 59.55, 41.4, 234.21, 57.35, 2721.39);

        // 65세 이상
        saveFemaleIntake(AgeGroup.ELDERLY, 1382.98, 50.65, 30.13, 226.07, 47.87, 2419.84);
    }

    private void saveMaleIntake(AgeGroup ageGroup,
                                Double calories,
                                Double protein,
                                Double fat,
                                Double carbohydrates,
                                Double sugar,
                                Double sodium) {
        MaleNutrientIntake intake = new MaleNutrientIntake();
        intake.setAgeGroup(ageGroup);
        intake.setCalories(calories);
        intake.setProtein(protein);
        intake.setFat(fat);
        intake.setCarbohydrates(carbohydrates);
        intake.setSugar(sugar);
        intake.setSodium(sodium);

        entityManager.persist(intake);
    }

    private void saveFemaleIntake(AgeGroup ageGroup,
                                  Double calories,
                                  Double protein,
                                  Double fat,
                                  Double carbohydrates,
                                  Double sugar,
                                  Double sodium) {
        FemaleNutrientIntake intake = new FemaleNutrientIntake();
        intake.setAgeGroup(ageGroup);
        intake.setCalories(calories);
        intake.setProtein(protein);
        intake.setFat(fat);
        intake.setCarbohydrates(carbohydrates);
        intake.setSugar(sugar);
        intake.setSodium(sodium);

        entityManager.persist(intake);
    }
}