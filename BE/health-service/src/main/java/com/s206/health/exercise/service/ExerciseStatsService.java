package com.s206.health.exercise.service;

import com.s206.health.exercise.dto.response.DailyExerciseStatsResponse;
import com.s206.health.exercise.dto.response.MonthlyExerciseStatsResponse;
import com.s206.health.exercise.dto.response.TodayExerciseStatsResponse;
import com.s206.health.exercise.dto.response.WeeklyExerciseStatsResponse;
import com.s206.health.exercise.entity.Exercise;
import com.s206.health.exercise.entity.ExerciseCategory;
import com.s206.health.exercise.entity.ExerciseType;
import com.s206.health.exercise.repository.ExerciseCategoryRepository;
import com.s206.health.exercise.repository.ExerciseRepository;
import com.s206.health.exercise.repository.ExerciseTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseStatsService {

    private final ExerciseTypeRepository exerciseTypeRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final ExerciseRepository exerciseRepository;

    // 오늘의 운동 통계 조회
    @Transactional(readOnly = true)
    public TodayExerciseStatsResponse getTodayStats(Integer userId, String date) {
        // 1. 날짜 파라미터 처리 (없으면 오늘 날짜 사용)
        LocalDate targetDate;
        if (date != null && !date.isEmpty()) {
            try {
                targetDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                log.error("유효하지 않은 형식입니다. : {}", date);
                targetDate = LocalDate.now();
            }
        } else  {
            targetDate = LocalDate.now();
        }
        // 해당 날짜 범위 계산 (00:00:00 ~ 23:59:59)
        LocalDateTime startDateTime = targetDate.atStartOfDay();
        LocalDateTime endDateTime = targetDate.atTime(LocalTime.MAX);

        // 해당 날짜의 운동 기록 조회
        List<Exercise> todayExercises = exerciseRepository
                .findByUserIdAndExerciseDateBetweenAndIsDeletedFalseOrderByExerciseDateDesc(
                        userId, startDateTime, endDateTime);

        // 빈 목록 체크
        if (todayExercises == null) {
            todayExercises = new ArrayList<>();
        }

        // 운동 통계 직접 계산
        int exerciseCount = todayExercises.size();
        int totalCalories = todayExercises.stream()
                .mapToInt(Exercise::getExerciseCalorie)
                .sum();
        int totalTime = todayExercises.stream()
                .mapToInt(Exercise::getExerciseTime)
                .sum();

        // 4. 운동 타입과 카테고리 정보 로드
        Map<Integer, ExerciseType> exerciseTypeMap = exerciseTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseType::getExerciseTypeId, type -> type));

        Map<Integer, ExerciseCategory> exerciseCategoryMap = exerciseCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseCategory::getExerciseCategoryId, category -> category));

        // 5. 운동 상세 정보 구성
        List<TodayExerciseStatsResponse.ExerciseDetail> exerciseDetails = todayExercises.stream()
                .map(exercise -> {
                    ExerciseType type = exerciseTypeMap.get(exercise.getExerciseTypeId());
                    String categoryName = "";

                    if (type != null) {
                        ExerciseCategory category = exerciseCategoryMap.get(type.getExerciseCategoryId());
                        if (category != null) {
                            categoryName = category.getExerciseCategoryName();
                        }
                    }

                    return TodayExerciseStatsResponse.ExerciseDetail.builder()
                            .exerciseId(exercise.getExerciseId())
                            .exerciseName(type != null ? type.getExerciseName() : "")
                            .categoryName(categoryName)
                            .exerciseDate(exercise.getExerciseDate())
                            .exerciseTime(exercise.getExerciseTime())
                            .exerciseCalorie(exercise.getExerciseCalorie())
                            .build();
                })
                .collect(Collectors.toList());

        // 6. 응답 DTO 구성
        return TodayExerciseStatsResponse.builder()
                .date(startDateTime)
                .totalCalories(totalCalories)
                .totalExerciseTime(totalTime)
                .exerciseCount(exerciseCount)
                .exercises(exerciseDetails)
                .build();
    }

    // 일별 운동 통계 조회 (선택한 날짜 기준 이전 7일)
    @Transactional(readOnly = true)
    public DailyExerciseStatsResponse getDailyStats(Integer userId, String date) {
        // 1. 기준 날짜 처리 (없으면 오늘 날짜 사용)
        LocalDate baseDate;
        if (date != null && !date.isEmpty()) {
            try {
                baseDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                log.error("유효하지 않은 날짜 형식입니다: {}", date);
                baseDate = LocalDate.now();
            }
        } else {
            baseDate = LocalDate.now();
        }

        // 2. 날짜 범위 계산 (선택한 날짜 ~ 7일 전)
        LocalDate startDate = baseDate.minusDays(6); // 7일치 데이터 (선택한 날짜 포함)

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = baseDate.atTime(LocalTime.MAX);

        // 3. 해당 날짜 범위 내 운동 기록 조회
        List<Exercise> exercises = exerciseRepository.findByUserIdAndExerciseDateBetweenAndIsDeletedFalseOrderByExerciseDateDesc(
                userId, startDateTime, endDateTime
        );

        // 4. 운동 타입과 카테고리 정보 로드
        Map<Integer, ExerciseType> exerciseTypeMap = exerciseTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseType::getExerciseTypeId, type -> type));

        Map<Integer, ExerciseCategory> exerciseCategoryMap = exerciseCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseCategory::getExerciseCategoryId, category -> category));

        // 5. 일자별로 그룹화
        Map<LocalDate, List<Exercise>> exerciseByDate = exercises.stream()
                .collect(Collectors.groupingBy(exercise -> exercise.getExerciseDate().toLocalDate()));

        // 6. 응답 DTO
        List<DailyExerciseStatsResponse.DailyExercise> dailyExercises = new ArrayList<>();

        // 각 날짜에 대해 (선택한 날짜부터 7일 전까지)
        for (int i = 0; i <= 6; i++) {
            LocalDate targetDate = baseDate.minusDays(i);
            List<Exercise> dailyExerciseList = exerciseByDate.getOrDefault(targetDate, Collections.emptyList());

            // 해당 일자의 총 칼로리 계산
            Integer totalCalories = dailyExerciseList.stream()
                    .mapToInt(Exercise::getExerciseCalorie)
                    .sum();

            // 운동 상세 정보 구성
            List<DailyExerciseStatsResponse.ExerciseDetail> exerciseDetails = dailyExerciseList.stream()
                    .map(exercise -> {
                        ExerciseType type = exerciseTypeMap.get(exercise.getExerciseTypeId());
                        String categoryName = "";

                        if (type != null) {
                            ExerciseCategory category = exerciseCategoryMap.get(type.getExerciseCategoryId());
                            if (category != null) {
                                categoryName = category.getExerciseCategoryName();
                            }
                        }

                        return DailyExerciseStatsResponse.ExerciseDetail.builder()
                                .exerciseId(exercise.getExerciseId())
                                .exerciseName(type != null ? type.getExerciseName() : "")
                                .categoryName(categoryName)
                                .exerciseTime(exercise.getExerciseTime())
                                .exerciseCalorie(exercise.getExerciseCalorie())
                                .build();
                    })
                    .collect(Collectors.toList());

            // 일별 운동 통계 추가
            dailyExercises.add(DailyExerciseStatsResponse.DailyExercise.builder()
                    .date(targetDate)
                    .totalCalories(totalCalories)
                    .exerciseCount(dailyExerciseList.size())
                    .exercises(exerciseDetails)
                    .build());
        }

        // 날짜 기준 내림차순 정렬 (최신 날짜가 먼저)
        dailyExercises.sort(Comparator.comparing(DailyExerciseStatsResponse.DailyExercise::getDate).reversed());

        return DailyExerciseStatsResponse.builder()
                .dailyExercises(dailyExercises)
                .build();
    }

    // 주별 운동 통계 조회 (선택한 날짜 기준 이전 7주)
    @Transactional(readOnly = true)
    public WeeklyExerciseStatsResponse getWeeklyStats(Integer userId, String date) {
        // 1. 기준 날짜 처리 (없으면 오늘 날짜 사용)
        LocalDate baseDate;
        if (date != null && !date.isEmpty()) {
            try {
                baseDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                log.error("유효하지 않은 날짜 형식입니다: {}", date);
                baseDate = LocalDate.now();
            }
        } else {
            baseDate = LocalDate.now();
        }

        // 2. 날짜 범위 계산 (선택한 날짜가 포함된 주 ~ 7주 전)
        LocalDate startDate = baseDate.minusWeeks(6)
                .with(DayOfWeek.MONDAY);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = baseDate.atTime(LocalTime.MAX);

        // 3. 해당 날짜 범위 내 운동 기록 조회
        List<Exercise> exercises = exerciseRepository.findByUserIdAndExerciseDateBetweenAndIsDeletedFalseOrderByExerciseDateDesc(
                userId, startDateTime, endDateTime
        );

        // 4. 주별로 그룹화 (연도와 주차를 조합해서 그룹화)
        Map<String, List<Exercise>> exerciseByWeek = exercises.stream()
                .collect(Collectors.groupingBy(exercise -> {
                    LocalDate exerciseDate = exercise.getExerciseDate().toLocalDate();
                    int year = exerciseDate.getYear();
                    int weekNumber = exerciseDate.get(WeekFields.ISO.weekOfWeekBasedYear());
                    return year + "-" + weekNumber;
                }));

        // 5. 주별 통계 계산
        List<WeeklyExerciseStatsResponse.WeeklyExercise> weeklyExercises = new ArrayList<>();

        // 모든 주에 대해 (선택한 날짜가 포함된 주부터 7주 전까지)
        LocalDate currentWeekDate = baseDate;
        for (int i = 0; i < 7; i++) {
            // 해당 주의 첫날(월요일)과 마지막날(일요일) 계산
            LocalDate weekStart = currentWeekDate.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = currentWeekDate.with(DayOfWeek.SUNDAY);

            int year = weekStart.getYear();
            int weekNumber = weekStart.get(WeekFields.ISO.weekOfWeekBasedYear());
            String weekKey = year + "-" + weekNumber;

            // 해당 주의 운동 기록
            List<Exercise> weeklyExerciseList = exerciseByWeek.getOrDefault(weekKey, Collections.emptyList());

            // 해당 주의 총 칼로리 계산
            Integer totalCalories = weeklyExerciseList.stream()
                    .mapToInt(Exercise::getExerciseCalorie)
                    .sum();

            // 주차의 일수 계산 (최대 7일)
            int daysInWeek = 7;

            // 일평균 칼로리 계산
            double avgDailyCalories = (double) totalCalories / daysInWeek;

            // 주별 운동 통계 추가
            weeklyExercises.add(WeeklyExerciseStatsResponse.WeeklyExercise.builder()
                    .startDate(weekStart)
                    .endDate(weekEnd)
                    .totalCalories(totalCalories)
                    .totalExerciseCount(weeklyExerciseList.size())
                    .avgDailyCalories(Math.round(avgDailyCalories * 10) / 10.0)
                    .build());

            // 이전 주로 이동
            currentWeekDate = currentWeekDate.minusWeeks(1);
        }

        // 6. 시작일 기준 내림차순 정렬
        weeklyExercises.sort(Comparator.comparing(WeeklyExerciseStatsResponse.WeeklyExercise::getStartDate).reversed());

        return WeeklyExerciseStatsResponse.builder()
                .weeklyExercises(weeklyExercises)
                .build();
    }

    // 월별 운동 통계 조회 (선택한 날짜 기준 이전 7개월)
    @Transactional(readOnly = true)
    public MonthlyExerciseStatsResponse getMonthlyStats(Integer userId, String date) {
        // 1. 기준 날짜 처리 (없으면 오늘 날짜 사용)
        LocalDate baseDate;
        if (date != null && !date.isEmpty()) {
            try {
                baseDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                log.error("유효하지 않은 날짜 형식입니다: {}", date);
                baseDate = LocalDate.now();
            }
        } else {
            baseDate = LocalDate.now();
        }

        // 2. 날짜 범위 계산 (선택한 날짜가 포함된 월 ~ 7개월 전)
        LocalDate startDate = baseDate.minusMonths(6).withDayOfMonth(1);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = baseDate.atTime(LocalTime.MAX);

        // 3. 해당 날짜 범위 내 운동 기록 조회
        List<Exercise> exercises = exerciseRepository.findByUserIdAndExerciseDateBetweenAndIsDeletedFalseOrderByExerciseDateDesc(
                userId, startDateTime, endDateTime
        );

        // 4. 월별로 그룹화
        Map<YearMonth, List<Exercise>> exerciseByMonth = exercises.stream()
                .collect(Collectors.groupingBy(exercise ->
                        YearMonth.of(
                                exercise.getExerciseDate().getYear(),
                                exercise.getExerciseDate().getMonth()
                        )
                ));

        // 5. 월별 통계 계산
        List<MonthlyExerciseStatsResponse.MonthlyExercise> monthlyExercises = new ArrayList<>();

        // 모든 월에 대해 (선택한 날짜가 포함된 월부터 7개월 전까지)
        YearMonth currentMonth = YearMonth.from(baseDate);
        for (int i = 0; i < 7; i++) {
            // 해당 월의 운동 기록
            List<Exercise> monthlyExerciseList = exerciseByMonth.getOrDefault(currentMonth, Collections.emptyList());

            // 해당 월의 총 칼로리 계산
            Integer totalCalories = monthlyExerciseList.stream()
                    .mapToInt(Exercise::getExerciseCalorie)
                    .sum();

            // 해당 월의 일수
            int daysInMonth = currentMonth.lengthOfMonth();

            // 일평균 칼로리 계산
            double avgDailyCalories = (double) totalCalories / daysInMonth;

            // 월별 운동 통계 추가
            monthlyExercises.add(MonthlyExerciseStatsResponse.MonthlyExercise.builder()
                    .yearMonth(currentMonth)
                    .totalCalories(totalCalories)
                    .totalExerciseCount(monthlyExerciseList.size())
                    .avgDailyCalories(Math.round(avgDailyCalories * 10) / 10.0) // 소수점 한 자리까지
                    .build());

            // 이전 월로 이동
            currentMonth = currentMonth.minusMonths(1);
        }

        // 6. 연월 기준 내림차순 정렬 (최신 월이 먼저)
        monthlyExercises.sort(Comparator.comparing(MonthlyExerciseStatsResponse.MonthlyExercise::getYearMonth).reversed());

        return MonthlyExerciseStatsResponse.builder()
                .monthlyExercises(monthlyExercises)
                .build();
    }
}