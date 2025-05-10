package com.s206.health.step.service;

import com.s206.health.step.dto.request.StepCreateRequest;
import com.s206.health.step.dto.response.StepResponse;
import com.s206.health.step.dto.response.StepWeeklyResponse;
import com.s206.health.step.entity.Step;
import com.s206.health.step.repository.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StepService {

    private final StepRepository stepRepository;

    // 걸음 수 등록 (새로운 걸음 수 등록 또는 기존 기록 업데이트)
    @Transactional
    public StepResponse createOrUpdateStepCount(Integer userId, StepCreateRequest request) {
        LocalDate stepDate = request.getStepDate();
        // 날짜가 없으면 오늘로 인식
        if (stepDate == null) {
            stepDate = LocalDate.now();
        }

        // 해당 날짜에 이미 기록이 있는지 확인
        Optional<Step> existingStep = stepRepository.findStepByStepDate(userId, stepDate);

        if (existingStep.isPresent()) {
            // 기존 기록 업데이트
            Step step = existingStep.get();

            Step updatedStep = Step.builder()
                    .stepCountId(step.getStepCountId())
                    .userId(userId)
                    .stepDate(stepDate)
                    .stepCount(request.getStepCount())
                    .createdAt(step.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .deletedAt(null)
                    .isDeleted(false)
                    .build();

            Step savedStep = stepRepository.save(updatedStep);
            return mapToResponse(savedStep);
        } else {
            // 새 기록 생성
            Step newStep = Step.builder()
                    .userId(userId)
                    .stepDate(stepDate)
                    .stepCount(request.getStepCount())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .deletedAt(null)
                    .isDeleted(false)
                    .build();

            Step savedStep = stepRepository.save(newStep);
            return mapToResponse(savedStep);
        }
    }

    // 사용자의 모든 걸을 수 기록 조회
    @Transactional(readOnly = true)
    public List<StepResponse> getAllStepCounts(Integer userId) {
        List<Step> steps = stepRepository.findAllStepsByUser(userId);

        return steps.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 사용자의 특정 날짜 걸음 수 조회
    @Transactional(readOnly = true)
    public StepResponse getStepCountByDate(Integer userId, LocalDate date) {
        Optional<Step> stepOptional = stepRepository.findStepByStepDate(userId, date);

        // 해당 날짜의 기록이 없으면 기본값으로 응답
        if (stepOptional.isEmpty()) {
            // 기록이 없는 경우 기본 응답
            return StepResponse.builder()
                    .userId(userId)
                    .stepDate(date)
                    .stepCount(0)
                    .createdAt(null)
                    .build();
        }

        return mapToResponse(stepOptional.get());
    }

    // 일별 걸음 수 조회 (7일)
    @Transactional(readOnly = true)
    public StepWeeklyResponse getWeeklyStepCounts(Integer userId) {
        LocalDate today = LocalDate.now();

        // 현재 날짜가 속한 주의 시작일과 종료일 계산
        TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
        LocalDate startOfWeek = today.with(fieldISO, 1); // 주의 첫 날
        LocalDate endOfWeek = today.with(fieldISO, 7);   // 주의 마지막 날

        // 일주일 동안의 걸음 수 기록 조회
        List<Step> weeklySteps = stepRepository.findStepsByDateRange(
                userId, startOfWeek, endOfWeek);

        List<StepWeeklyResponse.StepDailyData> dailyData = new ArrayList<>();

        // 일주일의 각 날짜에 대해 반복
        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            // 해당 날짜의 걸음 수 기록 찾기
            Optional<Step> stepForDate = weeklySteps.stream()
                    .filter(step -> step.getStepDate().equals(currentDate))
                    .findFirst();

            // 일별 데이터 추가
            dailyData.add(StepWeeklyResponse.StepDailyData.builder()
                    .date(date)
                    .stepCount(stepForDate.map(Step::getStepCount).orElse(0))
                    .build());
        }

        // 총 걸음 수 및 평균 계산
        int totalSteps = weeklySteps.stream()
                .mapToInt(Step::getStepCount)
                .sum();

        // 평균 걸음 수 계산 - 항상 7로 나눔 (일주일)
        double avgSteps = (double) totalSteps / 7;

        // 걸음 수 평균 반올림 후 정수로 변환
        Integer intAvgSteps = (int) Math.round(avgSteps);

        // 응답 생성
        return StepWeeklyResponse.builder()
                .weeklySteps(dailyData)
                .startDate(startOfWeek)
                .endDate(endOfWeek)
                .totalSteps(totalSteps)
                .avgSteps(intAvgSteps)
                .build();
    }

    // Entity를 Response DTO로 변환하는 헬퍼 메서드
    private StepResponse mapToResponse(Step step) {
        return StepResponse.builder()
                .stepCountId(step.getStepCountId())
                .userId(step.getUserId())
                .stepDate(step.getStepDate())
                .stepCount(step.getStepCount())
                .createdAt(step.getCreatedAt())
                .build();
    }
}
