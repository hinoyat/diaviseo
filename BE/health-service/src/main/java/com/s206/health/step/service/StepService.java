package com.s206.health.step.service;

import com.s206.health.step.dto.request.StepCreateRequest;
import com.s206.health.step.dto.response.StepResponse;
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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;
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

    // 주간 평균 걸음 수 조회
    @Transactional(readOnly = true)
    public Integer getWeeklyAverageStepCount(Integer userId) {
        LocalDate today = LocalDate.now();

        // 현재 날짜가 속한 주의 시작일과 종료일 계산
        TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
        LocalDate startOfWeek = today.with(fieldISO, 1); // 주의 첫 날
        LocalDate endOfWeek = today.with(fieldISO, 7);   // 주의 마지막 날

        List<Step> weeklySteps = stepRepository.findStepsByDateRange(userId, startOfWeek, endOfWeek);

        // 평균 계산 및 반올림
        OptionalDouble average = weeklySteps.stream()
                .mapToInt(Step::getStepCount)
                .average();

        // 평균 값을 반올림하여 정수로 반환
        return (int) Math.round(average.orElse(0.0));
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
