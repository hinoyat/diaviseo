package com.s206.health.nutrition.statistics.service;

import com.s206.health.nutrition.meal.repository.MealRepository;
import com.s206.health.nutrition.statistics.dto.response.NutritionStatsEntry;
import com.s206.health.nutrition.statistics.dto.response.NutritionStatsResponse;
import com.s206.health.nutrition.statistics.type.PeriodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealStatisticsService {

    private final MealRepository mealRepository;

    @Transactional(readOnly = true)
    public NutritionStatsResponse getNutritionStats(Integer userId, LocalDate endDate, PeriodType periodType) {
        log.info("[STATS] userId={} → 영양 통계 조회 요청: 종료일={}, 기간 타입={}", userId, endDate, periodType);

        switch (periodType) {
            case DAY:
                return getDailyNutritionStats(userId, endDate);
            case WEEK:
                return getWeeklyNutritionStats(userId, endDate);
            case MONTH:
                return getMonthlyNutritionStats(userId, endDate);
            default:
                throw new IllegalArgumentException("지원하지 않는 기간 타입입니다: " + periodType);
        }
    }

    // 일별 통계 (최근 7일)
    private NutritionStatsResponse getDailyNutritionStats(Integer userId, LocalDate endDate) {
        LocalDate startDate = endDate.minusDays(6); // 오늘 포함 최근 7일
        log.info("[STATS_DAY] 일별 통계 계산: userId={}, 시작일={}, 종료일={}", userId, startDate, endDate);

        // 일별 영양소 데이터 조회
        List<Object[]> dailyData = mealRepository.calculateWeeklyNutrition(userId, startDate, endDate);
        List<NutritionStatsEntry> entries = createDailyEntries(startDate, endDate, dailyData);

        log.info("[STATS_DAY] 일별 통계 계산 완료: userId={}, 항목 수={}", userId, entries.size());

        return NutritionStatsResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .periodType(PeriodType.DAY)
                .data(entries)
                .build();
    }

    // 주별 통계 (최근 7주)
    private NutritionStatsResponse getWeeklyNutritionStats(Integer userId, LocalDate endDate) {
        // 7주 전 시작일 계산
        LocalDate startDate = endDate.minusWeeks(6);
        log.info("[STATS_WEEK] 주별 통계 계산: userId={}, 시작일={}, 종료일={}", userId, startDate, endDate);

        try {
            // 주별 영양소 데이터 조회
            List<Object[]> weeklyData = mealRepository.calculateWeeklyAverageNutrition(userId, startDate, endDate);
            List<NutritionStatsEntry> entries = createWeeklyEntries(startDate, weeklyData);

            log.info("[STATS_WEEK] 주별 통계 계산 완료: userId={}, 항목 수={}", userId, entries.size());

            return NutritionStatsResponse.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .periodType(PeriodType.WEEK)
                    .data(entries)
                    .build();
        } catch (Exception e) {
            log.error("[STATS_WEEK] 주별 통계 계산 오류: userId={}, 오류={}", userId, e.getMessage(), e);

            // 오류 발생 시 일별 데이터를 사용하여 주별 통계 계산
            return getWeeklyStatsUsingDailyData(userId, endDate);
        }
    }

    // 일별 데이터를 사용하여 주별 통계 계산 (대체 로직)
    private NutritionStatsResponse getWeeklyStatsUsingDailyData(Integer userId, LocalDate endDate) {
        LocalDate startDate = endDate.minusWeeks(6);
        log.info("[STATS_WEEK_ALT] 일별 데이터 활용 주별 통계 계산: userId={}, 시작일={}, 종료일={}", userId, startDate, endDate);

        // 전체 기간 일별 데이터 조회
        List<Object[]> allDailyData = mealRepository.calculateWeeklyNutrition(userId, startDate, endDate);

        // 주별 데이터 그룹화
        List<NutritionStatsEntry> entries = new ArrayList<>();
        for (int weekIdx = 0; weekIdx < 7; weekIdx++) {
            LocalDate weekStart = startDate.plusWeeks(weekIdx);
            LocalDate weekEnd = weekStart.plusDays(6);
            if (weekEnd.isAfter(endDate)) {
                weekEnd = endDate;
            }

            // 해당 주 데이터 필터링
            final LocalDate wsStart = weekStart;
            final LocalDate wsEnd = weekEnd;
            List<Object[]> weekData = allDailyData.stream()
                    .filter(row -> {
                        LocalDate date = (LocalDate) row[0];
                        return !date.isBefore(wsStart) && !date.isAfter(wsEnd);
                    })
                    .collect(Collectors.toList());

            // 평균 계산
            int totalCalorie = 0;
            BigDecimal totalCarbs = BigDecimal.ZERO;
            BigDecimal totalProtein = BigDecimal.ZERO;
            BigDecimal totalFat = BigDecimal.ZERO;
            BigDecimal totalSugar = BigDecimal.ZERO;

            for (Object[] row : weekData) {
                totalCalorie += ((Number) row[1]).intValue();
                totalCarbs = totalCarbs.add((BigDecimal) row[2]);
                totalProtein = totalProtein.add((BigDecimal) row[3]);
                totalFat = totalFat.add((BigDecimal) row[4]);
                totalSugar = totalSugar.add((BigDecimal) row[5]);
            }

            int daysWithData = weekData.size();
            int calorieAvg = daysWithData > 0 ? totalCalorie / daysWithData : 0;
            int carbsAvg = daysWithData > 0 ? totalCarbs.divide(BigDecimal.valueOf(daysWithData), RoundingMode.HALF_UP).intValue() : 0;
            int proteinAvg = daysWithData > 0 ? totalProtein.divide(BigDecimal.valueOf(daysWithData), RoundingMode.HALF_UP).intValue() : 0;
            int fatAvg = daysWithData > 0 ? totalFat.divide(BigDecimal.valueOf(daysWithData), RoundingMode.HALF_UP).intValue() : 0;
            int sugarAvg = daysWithData > 0 ? totalSugar.divide(BigDecimal.valueOf(daysWithData), RoundingMode.HALF_UP).intValue() : 0;

            entries.add(NutritionStatsEntry.builder()
                    .label(weekStart.toString()) // 주의 시작일
                    .calorie(calorieAvg)
                    .carbs(carbsAvg)
                    .protein(proteinAvg)
                    .fat(fatAvg)
                    .sugar(sugarAvg)
                    .build());
        }

        log.info("[STATS_WEEK_ALT] 일별 데이터 활용 주별 통계 계산 완료: userId={}, 항목 수={}", userId, entries.size());

        return NutritionStatsResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .periodType(PeriodType.WEEK)
                .data(entries)
                .build();
    }

    // 월별 통계 (최근 7개월)
    private NutritionStatsResponse getMonthlyNutritionStats(Integer userId, LocalDate endDate) {
        // 7개월 전 시작일 계산
        LocalDate startDate = endDate.withDayOfMonth(1).minusMonths(6);
        log.info("[STATS_MONTH] 월별 통계 계산: userId={}, 시작일={}, 종료일={}", userId, startDate, endDate);

        try {
            // 월별 영양소 데이터 조회
            List<Object[]> monthlyData = mealRepository.calculateMonthlyAverageNutrition(userId, startDate, endDate);
            List<NutritionStatsEntry> entries = createMonthlyEntries(startDate, monthlyData);

            log.info("[STATS_MONTH] 월별 통계 계산 완료: userId={}, 항목 수={}", userId, entries.size());

            return NutritionStatsResponse.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .periodType(PeriodType.MONTH)
                    .data(entries)
                    .build();
        } catch (Exception e) {
            log.error("[STATS_MONTH] 월별 통계 계산 오류: userId={}, 오류={}", userId, e.getMessage(), e);

            // 오류 발생 시 대체 로직: 일별 데이터를 사용하여 월별 통계 계산
            return getMonthlyStatsUsingDailyData(userId, endDate);
        }
    }

    // 일별 데이터를 사용하여 월별 통계 계산 (대체 로직)
    private NutritionStatsResponse getMonthlyStatsUsingDailyData(Integer userId, LocalDate endDate) {
        LocalDate startDate = endDate.withDayOfMonth(1).minusMonths(6);
        log.info("[STATS_MONTH_ALT] 일별 데이터 활용 월별 통계 계산: userId={}, 시작일={}, 종료일={}", userId, startDate, endDate);

        // 전체 기간 일별 데이터 조회
        List<Object[]> allDailyData = mealRepository.calculateWeeklyNutrition(userId, startDate, endDate);

        // 월별 데이터 그룹화
        List<NutritionStatsEntry> entries = new ArrayList<>();
        for (int monthIdx = 0; monthIdx < 7; monthIdx++) {
            LocalDate monthStart = startDate.plusMonths(monthIdx);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
            if (monthEnd.isAfter(endDate)) {
                monthEnd = endDate;
            }

            // 해당 월 데이터 필터링
            final LocalDate msStart = monthStart;
            final LocalDate msEnd = monthEnd;
            List<Object[]> monthData = allDailyData.stream()
                    .filter(row -> {
                        LocalDate date = (LocalDate) row[0];
                        return !date.isBefore(msStart) && !date.isAfter(msEnd);
                    })
                    .collect(Collectors.toList());

            // 평균 계산
            int totalCalorie = 0;
            BigDecimal totalCarbs = BigDecimal.ZERO;
            BigDecimal totalProtein = BigDecimal.ZERO;
            BigDecimal totalFat = BigDecimal.ZERO;
            BigDecimal totalSugar = BigDecimal.ZERO;

            for (Object[] row : monthData) {
                totalCalorie += ((Number) row[1]).intValue();
                totalCarbs = totalCarbs.add((BigDecimal) row[2]);
                totalProtein = totalProtein.add((BigDecimal) row[3]);
                totalFat = totalFat.add((BigDecimal) row[4]);
                totalSugar = totalSugar.add((BigDecimal) row[5]);
            }

            int daysWithData = monthData.size();
            int calorieAvg = daysWithData > 0 ? totalCalorie / daysWithData : 0;
            int carbsAvg = daysWithData > 0 ? totalCarbs.divide(BigDecimal.valueOf(daysWithData), RoundingMode.HALF_UP).intValue() : 0;
            int proteinAvg = daysWithData > 0 ? totalProtein.divide(BigDecimal.valueOf(daysWithData), RoundingMode.HALF_UP).intValue() : 0;
            int fatAvg = daysWithData > 0 ? totalFat.divide(BigDecimal.valueOf(daysWithData), RoundingMode.HALF_UP).intValue() : 0;
            int sugarAvg = daysWithData > 0 ? totalSugar.divide(BigDecimal.valueOf(daysWithData), RoundingMode.HALF_UP).intValue() : 0;

            entries.add(NutritionStatsEntry.builder()
                    .label(monthStart.toString()) // 월의 시작일
                    .calorie(calorieAvg)
                    .carbs(carbsAvg)
                    .protein(proteinAvg)
                    .fat(fatAvg)
                    .sugar(sugarAvg)
                    .build());
        }

        log.info("[STATS_MONTH_ALT] 일별 데이터 활용 월별 통계 계산 완료: userId={}, 항목 수={}", userId, entries.size());

        return NutritionStatsResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .periodType(PeriodType.MONTH)
                .data(entries)
                .build();
    }

    // 일별 데이터 항목 생성
    private List<NutritionStatsEntry> createDailyEntries(LocalDate startDate, LocalDate endDate, List<Object[]> dailyData) {
        // 날짜별 데이터 맵 생성
        Map<LocalDate, Object[]> dataMap = new HashMap<>();
        for (Object[] row : dailyData) {
            dataMap.put((LocalDate) row[0], row);
        }

        // 모든 날짜에 대해 항목 생성
        List<NutritionStatsEntry> entries = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String label = date.toString(); // ISO 형식 날짜

            // 해당 날짜 데이터가 있으면 사용, 없으면 0 값으로 처리
            Object[] data = dataMap.getOrDefault(date, new Object[]{date, 0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});

            // 영양소 값 추출
            int calorie = ((Number) data[1]).intValue();
            BigDecimal carbs = (BigDecimal) data[2];
            BigDecimal protein = (BigDecimal) data[3];
            BigDecimal fat = (BigDecimal) data[4];
            BigDecimal sugar = (BigDecimal) data[5];

            entries.add(NutritionStatsEntry.builder()
                    .label(label)
                    .calorie(calorie)
                    .carbs(carbs.intValue())
                    .protein(protein.intValue())
                    .fat(fat.intValue())
                    .sugar(sugar.intValue())
                    .build());
        }

        return entries;
    }

    // 주별 데이터 항목 생성
    private List<NutritionStatsEntry> createWeeklyEntries(LocalDate startDate, List<Object[]> weeklyData) {
        // 로그 추가: 실제 쿼리 결과 확인
        log.info("[STATS_WEEK_DEBUG] 주별 데이터 건수: {}", weeklyData.size());
        for (Object[] row : weeklyData) {
            log.info("[STATS_WEEK_DEBUG] 주별 데이터: yearWeek={}, 타입={}, 값들={},{},{},{}",
                    row[0], row[0].getClass().getName(), row[1], row[2], row[3], row[4]);
        }

        // yearWeek 별 데이터 맵 생성
        Map<Object, Object[]> dataMap = new HashMap<>();
        for (Object[] row : weeklyData) {
            // 타입에 관계없이 원시 객체를 키로 사용
            dataMap.put(row[0], row);
        }

        // 결과 항목 생성 (7주)
        List<NutritionStatsEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate weekStart = startDate.plusWeeks(i);
            String label = weekStart.toString(); // ISO 형식 날짜 (YYYY-MM-DD)

            // 각 주의 yearWeek 값 계산 (로그에 출력)
            int calculatedYearWeek = calculateMySqlYearWeek(weekStart);
            log.info("[STATS_WEEK_DEBUG] 계산된 주 값: 주={}, yearWeek={}", i, calculatedYearWeek);

            // 해당 주 데이터 조회 시도 (다양한 형태로)
            Object[] data = null;
            // 1. 계산된 정수값으로 시도
            data = dataMap.get(calculatedYearWeek);
            // 2. 정수값을 Long으로 변환하여 시도
            if (data == null) data = dataMap.get(Long.valueOf(calculatedYearWeek));
            // 3. 정수값을 문자열로 변환하여 시도
            if (data == null) data = dataMap.get(String.valueOf(calculatedYearWeek));

            log.info("[STATS_WEEK_DEBUG] 주={}, 일치하는 데이터 찾음={}", i, data != null);

            int calorie = 0, carbs = 0, protein = 0, fat = 0, sugar = 0;
            if (data != null) {
                // 평균 값 추출
                calorie = ((Number) data[1]).intValue();
                carbs = ((BigDecimal) data[2]).intValue();
                protein = ((BigDecimal) data[3]).intValue();
                fat = ((BigDecimal) data[4]).intValue();
                sugar = ((BigDecimal) data[5]).intValue();
            }

            entries.add(NutritionStatsEntry.builder()
                    .label(label)
                    .calorie(calorie)
                    .carbs(carbs)
                    .protein(protein)
                    .fat(fat)
                    .sugar(sugar)
                    .build());
        }

        return entries;
    }

    // 월별 데이터 항목 생성
    private List<NutritionStatsEntry> createMonthlyEntries(LocalDate startDate, List<Object[]> monthlyData) {
        // 년/월별 데이터 맵 생성
        Map<String, Object[]> dataMap = new HashMap<>();
        for (Object[] row : monthlyData) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            String key = year + "-" + month;
            dataMap.put(key, row);
        }

        // 결과 항목 생성 (7개월)
        List<NutritionStatsEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate monthStart = startDate.plusMonths(i);
            String label = monthStart.toString(); // ISO 형식 날짜 (YYYY-MM-DD)

            // 해당 월 키 생성
            String key = monthStart.getYear() + "-" + monthStart.getMonthValue();

            // 해당 월 데이터 조회
            Object[] data = dataMap.getOrDefault(key, null);

            int calorie = 0, carbs = 0, protein = 0, fat = 0, sugar = 0;
            if (data != null) {
                // 평균 값 추출
                calorie = ((Number) data[2]).intValue();
                carbs = ((BigDecimal) data[3]).intValue();
                protein = ((BigDecimal) data[4]).intValue();
                fat = ((BigDecimal) data[5]).intValue();
                sugar = ((BigDecimal) data[6]).intValue();
            }

            entries.add(NutritionStatsEntry.builder()
                    .label(label)
                    .calorie(calorie)
                    .carbs(carbs)
                    .protein(protein)
                    .fat(fat)
                    .sugar(sugar)
                    .build());
        }

        return entries;
    }

    private int calculateMySqlYearWeek(LocalDate date) {
        // MySQL YEARWEEK 첫째 모드(1)는 월요일이 주의 첫째 날이고, 일년의 첫 주는 4일 이상이 있는 주입니다
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        // java.util.Calendar를 사용하여 계산 (Java 8 이전 방식이지만 MySQL과 일치하는 계산을 제공)
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4); // 첫 주에 최소 4일이 있어야 1주차로 계산 (MySQL mode 1과 일치)
        calendar.set(year, month - 1, day); // 월은 0부터 시작하므로 -1

        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int yearOfWeek = calendar.get(Calendar.YEAR); // 1월 첫째 주나 12월 마지막 주는 전년도나 다음년도일 수 있음

        return yearOfWeek * 100 + week;
    }
}