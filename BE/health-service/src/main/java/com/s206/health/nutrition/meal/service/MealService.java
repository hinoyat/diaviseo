package com.s206.health.nutrition.meal.service;

import com.s206.common.exception.types.BadRequestException;
import com.s206.common.exception.types.InternalServerErrorException;
import com.s206.common.exception.types.NotFoundException;
import com.s206.common.exception.types.UnauthorizedException;
import com.s206.health.nutrition.food.entity.Food;
import com.s206.health.nutrition.food.repository.FoodRepository;
import com.s206.health.nutrition.meal.dto.request.MealCreateRequest;
import com.s206.health.nutrition.meal.dto.request.MealFoodRequest;
import com.s206.health.nutrition.meal.dto.request.MealTimeRequest;
import com.s206.health.nutrition.meal.dto.response.*;
import com.s206.health.nutrition.meal.entity.Meal;
import com.s206.health.nutrition.meal.entity.MealFood;
import com.s206.health.nutrition.meal.entity.MealTime;
import com.s206.health.nutrition.meal.entity.MealType;
import com.s206.health.nutrition.meal.repository.MealFoodRepository;
import com.s206.health.nutrition.meal.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealService {

    private final MealRepository mealRepository;
    private final FoodRepository foodRepository;
    private final MealFoodRepository mealFoodRepository;

    @Transactional
    public MealDetailResponse createOrUpdateMeal(MealCreateRequest request, Integer userId) {
        log.info("[CREATE/UPDATE] userId={} → 식단 등록/수정 요청: 날짜={}", userId, request.getMealDate());

        // 1. 해당 날짜의 식단이 있는지 확인
        List<Meal> existingMeals = mealRepository.findByUserIdAndMealDateAndIsDeletedFalse(userId, request.getMealDate());

        Meal meal;
        if (!existingMeals.isEmpty()) {
            // 기존 식단이 있으면 가져옴
            meal = existingMeals.get(0);
            log.info("[UPDATE] 기존 식단 업데이트: mealId={}", meal.getMealId());

            // isMeal 값 업데이트
            if (request.getIsMeal() != null) {
                meal.updateIsMeal(request.getIsMeal());
            }
        } else {
            // 기존 식단이 없으면 새로 생성
            meal = Meal.builder()
                    .userId(userId)
                    .mealDate(request.getMealDate())
                    .isMeal(request.getIsMeal())
                    .build();
            log.info("[CREATE] 새 식단 생성: 날짜={}", request.getMealDate());
        }

        // 2. 요청의 각 MealTime 처리
        if (request.getMealTimes() != null) {
            for (MealTimeRequest mealTimeRequest : request.getMealTimes()) {
                // 같은 시간대(mealType)의 기존 MealTime 찾기
                Optional<MealTime> existingMealTime = meal.getMealTimes().stream()
                        .filter(mt -> mt.getMealType() == mealTimeRequest.getMealType() && !mt.getIsDeleted())
                        .findFirst();

                if (existingMealTime.isPresent()) {
                    // 기존 MealTime이 있으면 MealFoods를 지우고 새로 추가
                    MealTime mealTime = existingMealTime.get();
                    mealTime.getMealFoods().clear();

                    // eatingTime 업데이트
                    mealTime.updateEatingTime(mealTimeRequest.getEatingTime());

                    // 새 음식 추가
                    addFoodsToMealTime(mealTime, mealTimeRequest.getFoods());
                    log.info("[UPDATE] 기존 시간대 업데이트: 시간대={}", mealTimeRequest.getMealType());
                } else {
                    // 기존 MealTime이 없으면 새로 생성
                    MealTime mealTime = MealTime.builder()
                            .meal(meal)
                            .mealType(mealTimeRequest.getMealType())
                            .eatingTime(mealTimeRequest.getEatingTime())
                            .build();

                    // 음식 추가
                    addFoodsToMealTime(mealTime, mealTimeRequest.getFoods());
                    meal.getMealTimes().add(mealTime);
                    log.info("[CREATE] 새 시간대 추가: 시간대={}", mealTimeRequest.getMealType());
                }
            }
        }

        // 3. 저장 및 결과 반환
        mealRepository.save(meal);
        log.info("[COMPLETE] 식단 등록/수정 완료: mealId={}", meal.getMealId());

        return MealDetailResponse.toDto(meal);
    }

    // 음식 추가 헬퍼 메서드
    private void addFoodsToMealTime(MealTime mealTime, List<MealFoodRequest> foods) {
        if (foods != null) {
            for (MealFoodRequest foodRequest : foods) {
                Food food = foodRepository.findById(foodRequest.getFoodId())
                        .orElseThrow(() -> new BadRequestException("유효하지 않은 음식 ID입니다: " + foodRequest.getFoodId()));

                MealFood mealFood = MealFood.builder()
                        .mealTime(mealTime)
                        .food(food)
                        .quantity(foodRequest.getQuantity())
                        .foodImageUrl(foodRequest.getFoodImageUrl())
                        .build();

                mealTime.getMealFoods().add(mealFood);
            }
        }
    }

    // 특정 시간대만 수정하는 메서드
    @Transactional
    public MealTimeResponse updateMealTimeByDateAndType(LocalDate date, MealType mealType,
                                                        MealTimeRequest request, Integer userId) {
        log.info("[UPDATE_MEALTIME] userId={} → 특정 시간대 수정 요청: 날짜={}, 시간대={}", userId, date, mealType);

        // 1. 해당 날짜의 식단 조회
        List<Meal> meals = mealRepository.findByUserIdAndMealDateAndIsDeletedFalse(userId, date);
        if (meals.isEmpty()) {
            // 해당 날짜 식단이 없으면 새로 생성
            MealCreateRequest createRequest = MealCreateRequest.builder()
                    .mealDate(date)
                    .isMeal(true)
                    .mealTimes(Collections.singletonList(request))
                    .build();

            MealDetailResponse response = createOrUpdateMeal(createRequest, userId);
            return response.getMealTimes().stream()
                    .filter(mt -> mt.getMealType() == mealType)
                    .findFirst()
                    .orElseThrow(() -> new InternalServerErrorException("식단 생성 후 해당 시간대를 찾을 수 없습니다."));
        }

        Meal meal = meals.get(0);

        // 2. 해당 시간대의 MealTime 찾기
        Optional<MealTime> existingMealTime = meal.getMealTimes().stream()
                .filter(mt -> mt.getMealType() == mealType && !mt.getIsDeleted())
                .findFirst();

        MealTime mealTime;
        if (existingMealTime.isPresent()) {
            // 기존 MealTime이 있으면 음식 목록 초기화
            mealTime = existingMealTime.get();
            mealTime.getMealFoods().clear();

            // eatingTime 업데이트
            mealTime.updateEatingTime(request.getEatingTime());
        } else {
            // 없으면 새로 생성
            mealTime = MealTime.builder()
                    .meal(meal)
                    .mealType(mealType)
                    .eatingTime(request.getEatingTime())
                    .build();
            meal.getMealTimes().add(mealTime);
        }

        // 3. 새 음식 추가
        addFoodsToMealTime(mealTime, request.getFoods());

        // 4. 저장 및 결과 반환
        mealRepository.save(meal);
        log.info("[UPDATE_MEALTIME] 시간대 수정 완료: 날짜={}, 시간대={}", date, mealType);

        return MealTimeResponse.toDto(mealTime);
    }

    // 날짜별 식단 삭제
    @Transactional
    public void deleteMealByDate(LocalDate date, Integer userId) {
        log.info("[DELETE_DATE] userId={} → 날짜별 식단 삭제 요청: date={}", userId, date);

        List<Meal> meals = mealRepository.findByUserIdAndMealDateAndIsDeletedFalse(userId, date);
        if (!meals.isEmpty()) {
            Meal meal = meals.get(0);
            meal.delete(); // soft delete 수행
            mealRepository.save(meal);
            log.info("[DELETE_DATE] 식단 삭제 완료: date={}", date);
        } else {
            throw new NotFoundException("해당 날짜의 식단을 찾을 수 없습니다.");
        }
    }

    // 시간대별 식단 삭제
    @Transactional
    public void deleteMealTimeByDateAndType(LocalDate date, MealType mealType, Integer userId) {
        log.info("[DELETE_MEALTIME] userId={} → 시간대별 식단 삭제 요청: date={}, type={}", userId, date, mealType);

        List<Meal> meals = mealRepository.findByUserIdAndMealDateAndIsDeletedFalse(userId, date);
        if (!meals.isEmpty()) {
            Meal meal = meals.get(0);
            // 해당 시간대의 MealTime 찾아서 삭제 처리
            meal.getMealTimes().stream()
                    .filter(mt -> mt.getMealType() == mealType && !mt.getIsDeleted())
                    .forEach(mt -> {
                        mt.delete(); // MealTime에 delete 메서드 추가 필요
                        log.info("[DELETE_MEALTIME] 시간대 삭제 완료: date={}, type={}", date, mealType);
                    });
            mealRepository.save(meal);
        } else {
            throw new NotFoundException("해당 날짜의 식단을 찾을 수 없습니다.");
        }
    }

    // 개별 음식 삭제
    @Transactional
    public void deleteMealFood(Integer mealFoodId, Integer userId) {
        log.info("[DELETE_FOOD] userId={} → 음식 삭제 요청: mealFoodId={}", userId, mealFoodId);

        // MealFood 찾기
        MealFood mealFood = mealFoodRepository.findById(mealFoodId)
                .orElseThrow(() -> new NotFoundException("해당 음식을 찾을 수 없습니다."));

        // 소유권 확인
        MealTime mealTime = mealFood.getMealTime();
        Meal meal = mealTime.getMeal();
        if (!meal.getUserId().equals(userId)) {
            throw new UnauthorizedException("이 음식에 접근할 권한이 없습니다.");
        }

        // MealFood 삭제
        mealTime.getMealFoods().remove(mealFood);
        mealFoodRepository.delete(mealFood);

        log.info("[DELETE_FOOD] 음식 삭제 완료: mealFoodId={}", mealFoodId);
    }

    // 날짜별 식단 조회 (영양소 정보 포함)
    @Transactional(readOnly = true)
    public MealDailyResponse getMealByDate(LocalDate date, Integer userId) {
        log.info("[GET_DATE] userId={} → 날짜별 식단 조회 요청: date={}", userId, date);

        List<Meal> meals = mealRepository.findByUserIdAndMealDateAndIsDeletedFalse(userId, date);
        if (meals.isEmpty()) {
            // 해당 날짜 식단이 없으면 빈 식단 반환
            return MealDailyResponse.builder()
                    .mealDate(date)
                    .isMeal(false)
                    .totalNutrition(createEmptyNutrition())
                    .mealTimes(Collections.emptyList())
                    .build();
        }

        Meal meal = meals.get(0);

        // 전체 영양소 계산
        MealNutritionDto totalNutrition = calculateTotalNutrition(meal);

        // 시간대별 응답 생성 (영양소 포함)
        List<MealTimeNutritionResponse> mealTimeResponses = meal.getMealTimes().stream()
                .filter(mt -> !mt.getIsDeleted())
                .map(this::convertToMealTimeNutritionResponse)
                .collect(Collectors.toList());

        log.info("[GET_DATE] 조회 성공: date={}, 시간대 수={}", date, mealTimeResponses.size());

        return MealDailyResponse.builder()
                .mealId(meal.getMealId())
                .mealDate(meal.getMealDate())
                .isMeal(meal.getIsMeal())
                .totalNutrition(totalNutrition)
                .mealTimes(mealTimeResponses)
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .build();
    }

    // 시간대별 영양소 응답 생성
    private MealTimeNutritionResponse convertToMealTimeNutritionResponse(MealTime mealTime) {
        // 음식 응답 생성
        List<MealFoodResponse> foodResponses = mealTime.getMealFoods().stream()
                .map(MealFoodResponse::toDto)
                .collect(Collectors.toList());

        // 시간대별 영양소 계산
        MealNutritionDto nutrition = calculateMealTimeNutrition(mealTime);

        return MealTimeNutritionResponse.builder()
                .mealTimeId(mealTime.getMealTimeId())
                .mealType(mealTime.getMealType())
                .eatingTime(mealTime.getEatingTime())
                .foods(foodResponses)
                .nutrition(nutrition)
                .createdAt(mealTime.getCreatedAt())
                .updatedAt(mealTime.getUpdatedAt())
                .build();
    }

    // 빈 영양소 객체 생성
    private MealNutritionDto createEmptyNutrition() {
        return MealNutritionDto.builder()
                .totalCalorie(0)
                .totalCarbohydrate(BigDecimal.ZERO)
                .totalProtein(BigDecimal.ZERO)
                .totalFat(BigDecimal.ZERO)
                .totalSugar(BigDecimal.ZERO)
                .totalSodium(BigDecimal.ZERO)
                .build();
    }

    // 시간대별 영양소 계산
    private MealNutritionDto calculateMealTimeNutrition(MealTime mealTime) {
        int totalCalorie = 0;
        BigDecimal totalCarb = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalSugar = BigDecimal.ZERO;
        BigDecimal totalSodium = BigDecimal.ZERO;

        for (MealFood mealFood : mealTime.getMealFoods()) {
            Food food = mealFood.getFood();
            int quantity = mealFood.getQuantity();

            totalCalorie += food.getCalorie() * quantity;
            totalCarb = totalCarb.add(food.getCarbohydrate().multiply(BigDecimal.valueOf(quantity)));
            totalProtein = totalProtein.add(food.getProtein().multiply(BigDecimal.valueOf(quantity)));
            totalFat = totalFat.add(food.getFat().multiply(BigDecimal.valueOf(quantity)));
            totalSugar = totalSugar.add(food.getSweet().multiply(BigDecimal.valueOf(quantity)));
            totalSodium = totalSodium.add(food.getSodium().multiply(BigDecimal.valueOf(quantity)));
        }

        return MealNutritionDto.builder()
                .totalCalorie(totalCalorie)
                .totalCarbohydrate(totalCarb)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .totalSugar(totalSugar)
                .totalSodium(totalSodium)
                .build();
    }

    // 전체 식단 영양소 계산
    private MealNutritionDto calculateTotalNutrition(Meal meal) {
        int totalCalorie = 0;
        BigDecimal totalCarb = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalSugar = BigDecimal.ZERO;
        BigDecimal totalSodium = BigDecimal.ZERO;

        for (MealTime mealTime : meal.getMealTimes()) {
            if (mealTime.getIsDeleted()) continue;

            MealNutritionDto timeNutrition = calculateMealTimeNutrition(mealTime);
            totalCalorie += timeNutrition.getTotalCalorie();
            totalCarb = totalCarb.add(timeNutrition.getTotalCarbohydrate());
            totalProtein = totalProtein.add(timeNutrition.getTotalProtein());
            totalFat = totalFat.add(timeNutrition.getTotalFat());
            totalSugar = totalSugar.add(timeNutrition.getTotalSugar());
            totalSodium = totalSodium.add(timeNutrition.getTotalSodium());
        }

        return MealNutritionDto.builder()
                .totalCalorie(totalCalorie)
                .totalCarbohydrate(totalCarb)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .totalSugar(totalSugar)
                .totalSodium(totalSodium)
                .build();
    }

    // 기존 getMealDetail 메서드
    @Transactional(readOnly = true)
    public MealDetailResponse getMealDetail(Integer mealId, Integer userId) {
        log.info("[DETAIL] userId={} → 식단 상세 조회 요청: mealId={}", userId, mealId);

        Meal meal = findOwnedMeal(mealId, userId);

        log.info("[DETAIL] 조회 성공: mealId={}, date={}", mealId, meal.getMealDate());
        return MealDetailResponse.toDto(meal);
    }

    // 소유권 확인 및 식단 조회 메소드 (기존 코드에서 가져옴)
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

    // 기존 deleteMeal 메서드
    @Transactional
    public void deleteMeal(Integer mealId, Integer userId) {
        log.info("[DELETE] userId={} → 식단 삭제 요청: mealId={}", userId, mealId);

        Meal meal = findOwnedMeal(mealId, userId);
        meal.delete(); // soft delete 수행
        mealRepository.save(meal);

        log.info("[DELETE] 식단 삭제 완료: mealId={}", mealId);
    }

    // 영양 정보 계산 관련 메서드
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
                        .totalSodium(BigDecimal.ZERO)
                        .totalSugar(BigDecimal.ZERO)
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
            BigDecimal sugar = (result[4] instanceof BigDecimal) ? (BigDecimal) result[4] : BigDecimal.ZERO;
            BigDecimal sodium = (result[5] instanceof BigDecimal) ? (BigDecimal) result[5] : BigDecimal.ZERO;


            log.info("[DAILY] 계산 완료: userId={}, date={}, 칼로리={}", userId, date, calories);

            return DailyNutritionResponse.builder()
                    .date(date)
                    .totalCalorie(calories)
                    .totalCarbohydrate(carbs)
                    .totalProtein(protein)
                    .totalFat(fat)
                    .totalSugar(sugar)
                    .totalSodium(sodium)
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
                    .totalSugar(BigDecimal.ZERO)
                    .totalSodium(BigDecimal.ZERO)
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
                        .totalSugar(BigDecimal.ZERO)
                        .totalSodium(BigDecimal.ZERO)
                        .build())
        );

        // 쿼리 결과로 데이터 채우기
        for (Object[] row : weeklyNutritionData) {
            LocalDate date = (LocalDate) row[0];
            Integer calories = ((Number) row[1]).intValue();
            BigDecimal carbs = (BigDecimal) row[2];
            BigDecimal protein = (BigDecimal) row[3];
            BigDecimal fat = (BigDecimal) row[4];
            BigDecimal sugar = (BigDecimal) row[5];
            BigDecimal sodium = (BigDecimal) row[6];

            nutritionMap.put(date, DailyNutritionResponse.builder()
                    .date(date)
                    .totalCalorie(calories)
                    .totalCarbohydrate(carbs)
                    .totalProtein(protein)
                    .totalFat(fat)
                    .totalSugar(sugar)
                    .totalSodium(sodium)
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
}