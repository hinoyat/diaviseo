package com.s206.health.nutrition.meal.repository;

import com.s206.health.nutrition.meal.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Integer> {

    List<Meal> findByUserIdAndMealDate(Integer userId, LocalDate mealDate);

    List<Meal> findByUserIdAndMealDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);

    List<Meal> findByUserIdAndMealDateAndIsDeletedFalse(Integer userId, LocalDate mealDate);

    @Query("SELECT " +
            "COALESCE(SUM(f.calorie * mf.quantity), 0), " +
            "COALESCE(SUM(f.carbohydrate * mf.quantity), 0), " +
            "COALESCE(SUM(f.protein * mf.quantity), 0), " +
            "COALESCE(SUM(f.fat * mf.quantity), 0), " +
            "COALESCE(SUM(f.sweet * mf.quantity), 0), " +
            "COALESCE(SUM(f.sodium * mf.quantity), 0) " +
            "FROM Meal m JOIN m.mealTimes mt JOIN mt.mealFoods mf JOIN mf.food f " +
            "WHERE m.userId = :userId AND m.mealDate = :date " +
            "AND m.isDeleted = false AND mt.isDeleted = false")
    Object[] calculateDailyNutritionRaw(@Param("userId") Integer userId, @Param("date") LocalDate date);

    @Query("SELECT m.mealDate as date, " +
            "SUM(f.calorie * CAST(mf.quantity AS float)) as totalCalorie, " +
            "SUM(f.carbohydrate * CAST(mf.quantity AS float)) as totalCarbohydrate, " +
            "SUM(f.protein * CAST(mf.quantity AS float)) as totalProtein, " +
            "SUM(f.fat * CAST(mf.quantity AS float)) as totalFat, " +
            "SUM(f.sweet * CAST(mf.quantity AS float)) as totalSugar, " +
            "SUM(f.sodium * CAST(mf.quantity AS float)) as totalSodium " +
            "FROM Meal m JOIN m.mealTimes mt JOIN mt.mealFoods mf JOIN mf.food f " +
            "WHERE m.userId = :userId AND m.mealDate BETWEEN :startDate AND :endDate " +
            "AND m.isDeleted = false AND mt.isDeleted = false " +
            "GROUP BY m.mealDate")
    List<Object[]> calculateWeeklyNutrition(@Param("userId") Integer userId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // 주간 영양소 평균 섭취량 (모든 식사 유형 포함)
    @Query(value = "SELECT YEARWEEK(m.meal_date, 1) as year_week, " +
            "SUM(f.calorie * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_calorie, " +
            "SUM(f.carbohydrate * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_carbohydrate, " +
            "SUM(f.protein * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_protein, " +
            "SUM(f.fat * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_fat, " +
            "SUM(f.sweet * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_sugar, " +
            "COUNT(DISTINCT m.meal_date) as day_count " +
            "FROM meal_tb m " +
            "JOIN meal_time_tb mt ON m.meal_id = mt.meal_id " +
            "JOIN meal_food_tb mf ON mt.meal_time_id = mf.meal_time_id " +
            "JOIN food_information_tb f ON mf.food_id = f.food_id " +
            "WHERE m.user_id = :userId AND m.meal_date BETWEEN :startDate AND :endDate " +
            "AND m.is_deleted = false AND mt.is_deleted = false " +
            "GROUP BY YEARWEEK(m.meal_date, 1) " +
            "ORDER BY year_week", nativeQuery = true)
    List<Object[]> calculateWeeklyAverageNutrition(@Param("userId") Integer userId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    // 월별 영양소 평균 섭취량 (모든 식사 유형 포함)
    @Query(value = "SELECT YEAR(m.meal_date) as year, " +
            "MONTH(m.meal_date) as month, " +
            "SUM(f.calorie * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_calorie, " +
            "SUM(f.carbohydrate * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_carbohydrate, " +
            "SUM(f.protein * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_protein, " +
            "SUM(f.fat * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_fat, " +
            "SUM(f.sweet * mf.quantity) / COUNT(DISTINCT m.meal_date) as avg_daily_sugar, " +
            "COUNT(DISTINCT m.meal_date) as day_count " +
            "FROM meal_tb m " +
            "JOIN meal_time_tb mt ON m.meal_id = mt.meal_id " +
            "JOIN meal_food_tb mf ON mt.meal_time_id = mf.meal_time_id " +
            "JOIN food_information_tb f ON mf.food_id = f.food_id " +
            "WHERE m.user_id = :userId AND m.meal_date BETWEEN :startDate AND :endDate " +
            "AND m.is_deleted = false AND mt.is_deleted = false " +
            "GROUP BY YEAR(m.meal_date), MONTH(m.meal_date) " +
            "ORDER BY year, month", nativeQuery = true)
    List<Object[]> calculateMonthlyAverageNutrition(@Param("userId") Integer userId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

}