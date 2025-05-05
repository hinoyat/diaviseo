package com.s206.health.nutrition.meal.service;

import com.s206.common.exception.types.BadRequestException;
import com.s206.common.exception.types.NotFoundException;
import com.s206.common.exception.types.UnauthorizedException;
import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.food.repository.FoodRepository;
import com.s206.health.nutrition.meal.dto.request.MealCreateRequest;
import com.s206.health.nutrition.meal.dto.request.MealUpdateRequest;
import com.s206.health.nutrition.meal.dto.response.DailyNutritionResponse;
import com.s206.health.nutrition.meal.dto.response.MealDetailResponse;
import com.s206.health.nutrition.meal.dto.response.WeeklyNutritionResponse;
import com.s206.health.nutrition.meal.entity.Meal;
import com.s206.health.nutrition.meal.entity.MealFood;
import com.s206.health.nutrition.meal.entity.MealTime;
import com.s206.health.nutrition.meal.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealService {

    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;

    @Transactional
    public MealDetailResponse createMeal(MealCreateRequest request, Integer userId) {
        log.info("[CREATE] userId={} → 식단 등록 요청: 날짜={}", userId, request.getMealDate());

        Meal meal = Meal.builder()
                .userId(userId)
                .mealDate(request.getMealDate())
                .isMeal(request.getIsMeal())
                .build();

        // MealTime 객체 생성 및 MealFood 설정
        request.getMealTimes().forEach(mealTimeRequest -> {
            MealTime mealTime = MealTime.builder()
                    .meal(meal)
                    .mealType(mealTimeRequest.getMealType())
                    .build();

            // 각 음식에 대한 MealFood 생성
            mealTimeRequest.getFoods().forEach(foodRequest -> {
                Food food = foodRepository.findById(foodRequest.getFoodId())
                        .orElseThrow(() -> new BadRequestException("유효하지 않은 음식 ID입니다: " + foodRequest.getFoodId()));

                MealFood mealFood = MealFood.builder()
                        .mealTime(mealTime)
                        .food(food)
                        .quantity(foodRequest.getQuantity())
                        .foodImageUrl(foodRequest.getFoodImageUrl())
                        .build();

                mealTime.getMealFoods().add(mealFood);
            });

            meal.getMealTimes().add(mealTime);
        });

        mealRepository.save(meal);
        log.info("[CREATE] 식단 등록 완료: mealId={}", meal.getMealId());

        return MealDetailResponse.toDto(meal);
    }

    @Transactional(readOnly = true)
    public MealDetailResponse getMealDetail(Integer mealId, Integer userId) {
        log.info("[DETAIL] userId={} → 식단 상세 조회 요청: mealId={}", userId, mealId);

        Meal meal = findOwnedMeal(mealId, userId);

        log.info("[DETAIL] 조회 성공: mealId={}, date={}", mealId, meal.getMealDate());
        return MealDetailResponse.toDto(meal);
    }

    @Transactional(readOnly = true)
    public List<MealDetailResponse> getTodayMeals(Integer userId) {
        LocalDate today = LocalDate.now();
        log.info("[TODAY] userId={} → 당일 식단 조회 요청: date={}", userId, today);

        List<Meal> todayMeals = mealRepository.findByUserIdAndMealDateAndIsDeletedFalse(userId, today);

        log.info("[TODAY] 조회 성공: userId={}, 식단 수={}", userId, todayMeals.size());
        return todayMeals.stream()
                .map(MealDetailResponse::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DailyNutritionResponse getDailyNutrition(Integer userId, LocalDate date) {
        log.info("[DAILY] userId={} → 일일 영양정보 조회 요청: date={}", userId, date);

        try {
            Object[] result = mealRepository.calculateDailyNutritionRaw(userId, date);

            // null 또는 빈 배열 체크
            if (result == null || result.length == 0) {
                log.info("[DAILY] 영양 정보 없음: userId={}, date={}", userId, date);
                return DailyNutritionResponse.builder()
                        .date(date)
                        .totalCalorie(0)
                        .totalCarbohydrate(BigDecimal.ZERO)
                        .totalProtein(BigDecimal.ZERO)
                        .totalFat(BigDecimal.ZERO)
                        .build();
            }

            // 첫 번째 요소가 배열인 경우 (쿼리 결과가 배열의 배열로 반환되는 경우)
            if (result[0] instanceof Object[]) {
                result = (Object[]) result[0];
            }

            // null 체크 추가
            Number calorieNum = result[0] instanceof Number ? (Number) result[0] : null;
            Integer calories = (calorieNum != null) ? calorieNum.intValue() : 0;

            BigDecimal carbs = (result[1] instanceof BigDecimal) ? (BigDecimal) result[1] : BigDecimal.ZERO;
            BigDecimal protein = (result[2] instanceof BigDecimal) ? (BigDecimal) result[2] : BigDecimal.ZERO;
            BigDecimal fat = (result[3] instanceof BigDecimal) ? (BigDecimal) result[3] : BigDecimal.ZERO;

            log.info("[DAILY] 계산 완료: userId={}, date={}, 칼로리={}", userId, date, calories);

            return DailyNutritionResponse.builder()
                    .date(date)
                    .totalCalorie(calories)
                    .totalCarbohydrate(carbs)
                    .totalProtein(protein)
                    .totalFat(fat)
                    .build();
        } catch (Exception e) {
            log.error("[DAILY] 오류 발생: userId={}, date={}, 오류={}", userId, date, e.getMessage(), e);

            // 오류 발생 시 기본값 반환
            return DailyNutritionResponse.builder()
                    .date(date)
                    .totalCalorie(0)
                    .totalCarbohydrate(BigDecimal.ZERO)
                    .totalProtein(BigDecimal.ZERO)
                    .totalFat(BigDecimal.ZERO)
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public WeeklyNutritionResponse getWeeklyNutrition(Integer userId, LocalDate endDate) {
        log.info("[WEEKLY] userId={} → 주간 영양정보 조회 요청: endDate={}", userId, endDate);

        // 시작 날짜 계산 (끝 날짜로부터 6일 전)
        LocalDate startDate = endDate.minusDays(6);

        // 한 번의 쿼리로 주간 영양정보 조회
        List<Object[]> weeklyNutritionData = mealRepository.calculateWeeklyNutrition(userId, startDate, endDate);

        // 결과 변환
        List<DailyNutritionResponse> dailyNutritions = new ArrayList<>();

        // 날짜별로 데이터 매핑
        Map<LocalDate, DailyNutritionResponse> nutritionMap = new HashMap<>();

        // 주간 범위의 모든 날짜에 대해 객체 생성
        startDate.datesUntil(endDate.plusDays(1)).forEach(date ->
                nutritionMap.put(date, DailyNutritionResponse.builder()
                        .date(date)
                        .totalCalorie(0)
                        .totalCarbohydrate(BigDecimal.ZERO)
                        .totalProtein(BigDecimal.ZERO)
                        .totalFat(BigDecimal.ZERO)
                        .build())
        );

        // 쿼리 결과로 데이터 채우기
        for (Object[] row : weeklyNutritionData) {
            LocalDate date = (LocalDate) row[0];
            Integer calories = ((Number) row[1]).intValue();
            BigDecimal carbs = (BigDecimal) row[2];
            BigDecimal protein = (BigDecimal) row[3];
            BigDecimal fat = (BigDecimal) row[4];

            nutritionMap.put(date, DailyNutritionResponse.builder()
                    .date(date)
                    .totalCalorie(calories)
                    .totalCarbohydrate(carbs)
                    .totalProtein(protein)
                    .totalFat(fat)
                    .build());
        }

        // 날짜 순서대로 정렬
        dailyNutritions = nutritionMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        log.info("[WEEKLY] 계산 완료: userId={}, startDate={}, endDate={}", userId, startDate, endDate);

        return WeeklyNutritionResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .dailyNutritions(dailyNutritions)
                .build();
    }

    @Transactional
    public MealDetailResponse updateMeal(Integer mealId, MealUpdateRequest request, Integer userId) {
        log.info("[UPDATE] userId={} → 식단 수정 요청: mealId={}", userId, mealId);

        Meal meal = findOwnedMeal(mealId, userId);

        // 기존 MealTimes와 MealFoods 데이터 삭제 (cascade 속성으로 자동 처리)
        meal.getMealTimes().clear();

        // 요청된 새 데이터로 업데이트
        if (request.getMealDate() != null) {
            log.info("[UPDATE] 날짜 변경: {} -> {}", meal.getMealDate(), request.getMealDate());
            // 엔티티에 updateMealDate 메소드 추가 필요
            meal.updateMealDate(request.getMealDate());
        }

        if (request.getIsMeal() != null) {
            log.info("[UPDATE] 식사여부 변경: {} -> {}", meal.getIsMeal(), request.getIsMeal());
            // 엔티티에 updateIsMeal 메소드 추가 필요
            meal.updateIsMeal(request.getIsMeal());
        }

        // 새 MealTime과 MealFood 추가
        request.getMealTimes().forEach(mealTimeRequest -> {
            MealTime mealTime = MealTime.builder()
                    .meal(meal)
                    .mealType(mealTimeRequest.getMealType())
                    .build();

            mealTimeRequest.getFoods().forEach(foodRequest -> {
                Food food = foodRepository.findById(foodRequest.getFoodId())
                        .orElseThrow(() -> new BadRequestException("유효하지 않은 음식 ID입니다: " + foodRequest.getFoodId()));

                MealFood mealFood = MealFood.builder()
                        .mealTime(mealTime)
                        .food(food)
                        .quantity(foodRequest.getQuantity())
                        .foodImageUrl(foodRequest.getFoodImageUrl())
                        .build();

                mealTime.getMealFoods().add(mealFood);
            });

            meal.getMealTimes().add(mealTime);
        });

        mealRepository.save(meal);
        log.info("[UPDATE] 식단 수정 완료: mealId={}", mealId);

        return MealDetailResponse.toDto(meal);
    }

    @Transactional
    public void deleteMeal(Integer mealId, Integer userId) {
        log.info("[DELETE] userId={} → 식단 삭제 요청: mealId={}", userId, mealId);

        Meal meal = findOwnedMeal(mealId, userId);
        meal.delete(); // soft delete 수행
        mealRepository.save(meal);

        log.info("[DELETE] 식단 삭제 완료: mealId={}", mealId);
    }

    // 소유권 확인 및 식단 조회 메소드
    private Meal findOwnedMeal(Integer mealId, Integer userId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NotFoundException("식단을 찾을 수 없습니다."));

        if (!meal.getUserId().equals(userId)) {
            throw new UnauthorizedException("이 식단에 접근할 권한이 없습니다.");
        }

        if (meal.getIsDeleted()) {
            throw new BadRequestException("이미 삭제된 식단입니다.");
        }

        return meal;
    }
}