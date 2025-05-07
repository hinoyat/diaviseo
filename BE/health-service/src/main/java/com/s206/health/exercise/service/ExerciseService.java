package com.s206.health.exercise.service;

import com.s206.common.exception.types.BadRequestException;
import com.s206.common.exception.types.NotFoundException;
import com.s206.health.exercise.dto.request.ExerciseCreateRequest;
import com.s206.health.exercise.dto.request.ExerciseUpdateRequest;
import com.s206.health.exercise.dto.response.ExerciseCategoryResponse;
import com.s206.health.exercise.dto.response.ExerciseListResponse;
import com.s206.health.exercise.dto.response.ExerciseTypeResponse;
import com.s206.health.exercise.entity.Exercise;
import com.s206.health.exercise.entity.ExerciseCategory;
import com.s206.health.exercise.entity.ExerciseType;
import com.s206.health.exercise.mapper.ExerciseMapper;
import com.s206.health.exercise.repository.ExerciseCategoryRepository;
import com.s206.health.exercise.repository.ExerciseRepository;
import com.s206.health.exercise.repository.ExerciseTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final ExerciseCategoryRepository exerciseCategoryRepository;
    private final ExerciseMapper exerciseMapper;

    // 특정 사용자의 운동 기록 전체 조회
    @Transactional(readOnly = true)
    public List<ExerciseListResponse> getAllExercisesByUser(Integer userId) {
        List<Exercise> exercises = exerciseRepository.findByUserIdAndIsDeletedFalse(userId);

        // 모든 운동 유형 정보를 가져와서 Map 으로 변환 (ID -> ExerciseType)
        Map<Integer, ExerciseType> exerciseTypeMap = exerciseTypeRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseType::getExerciseTypeId, type -> type));

        // 모든 운동 카테고리 정보를 가져와서 Map 으로 변환 (ID -> ExerciseCategory)
        Map<Integer, ExerciseCategory> exerciseCategoryMap = exerciseCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(ExerciseCategory::getExerciseCategoryId, category->category));

        // 운동 기록과 운동 유형, 카테고리 정보를 조합하여 응답 DTO 생성
        return exercises.stream()
                .map(exercise -> {
                    // 운동 유형 및 카테고리 정보 조회
                    ExerciseType exerciseType = exerciseTypeMap.get(exercise.getExerciseTypeId());
                    ExerciseCategory exerciseCategory = null;

                    if (exerciseType != null) {
                        exerciseCategory = exerciseCategoryMap.get(exerciseType.getExerciseCategoryId());
                    }

                    // 매퍼를 사용하여 DTO 변환
                    return exerciseMapper.toListResponse(exercise, exerciseType, exerciseCategory, exercise.getExerciseCalorie());
                })
                .collect(Collectors.toList());
    }

    // 특정 사용자의 운동 기록 상세 조회
    @Transactional(readOnly = true)
    public ExerciseListResponse getExerciseById(Integer userId, Integer exerciseId) {
        // 1. 운동 기록 조회 (해당 사용자 확인)
        Exercise exercise = exerciseRepository.findByExerciseIdAndUserId(exerciseId, userId)
                .orElseThrow(() -> new NotFoundException("해당 사용자의 운동 기록을 찾을 수 없습니다."));

        // 삭제된 운동 기록인 경우 예외 발생
        if (exercise.getIsDeleted()) {
            throw new BadRequestException("삭제된 운동 기록입니다.");
        }

        // 2. 운동 종류 정보 조회
        ExerciseType exerciseType = exerciseTypeRepository.findById(exercise.getExerciseTypeId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 종류입니다."));

        // 3. 운동 카테고리 정보 조회
        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(exerciseType.getExerciseCategoryId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 카테고리입니다."));

        // 4. 매퍼를 사용하여 응답 DTO 생성 및 반환
        return exerciseMapper.toListResponse(exercise, exerciseType, exerciseCategory, exercise.getExerciseCalorie());
    }

    // 운동 기록 생성
    @Transactional
    public ExerciseListResponse createExercise(Integer userId, ExerciseCreateRequest request) {
        LocalDateTime exerciseDate = LocalDateTime.now();

        // 1. 운동 종류 존재 여부 확인
        ExerciseType exerciseType = exerciseTypeRepository.findById(request.getExerciseTypeId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 종류입니다."));

        // 2. 칼로리 계산
        Integer totalCalorie;
        if (request.getExerciseCalorie() != null) {
            totalCalorie = request.getExerciseCalorie();
        } else {
            totalCalorie = exerciseType.getExerciseCalorie() * request.getExerciseTime();
        }

        Exercise exercise = Exercise.builder()
                .userId(userId)  // 경로 변수의 userId 사용
                .exerciseTypeId(request.getExerciseTypeId())
                .exerciseDate(exerciseDate)
                .exerciseTime(request.getExerciseTime())
                .exerciseCalorie(totalCalorie)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        // 3. 운동 기록 저장
        Exercise savedExercise = exerciseRepository.save(exercise);

        // 4. 카테고리 정보 조회
        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(exerciseType.getExerciseCategoryId())
                .orElse(null);

        // 5. 매퍼를 사용하여 응답 DTO 생성 및 반환
        return exerciseMapper.toListResponse(savedExercise, exerciseType, exerciseCategory, totalCalorie);
    }

    // 운동 기록 수정
    @Transactional
    public ExerciseListResponse updateExercise(Integer userId, Integer exerciseId, ExerciseUpdateRequest request) {
        // 1. 운동 기록 조회 (해당 사용자 확인)
        Exercise exercise = exerciseRepository.findByExerciseIdAndUserId(exerciseId, userId)
                .orElseThrow(() -> new NotFoundException("해당 사용자의 운동 기록을 찾을 수 없습니다."));

        // 삭제된 운동 기록인 경우 예외
        if (exercise.getIsDeleted()) {
            throw new BadRequestException("삭제된 운동 기록입니다.");
        }

        // 2. 운동 종류 존재 여부 확인
        ExerciseType exerciseType = exerciseTypeRepository.findById(request.getExerciseTypeId())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 종류 입니다."));

        // 3. 칼로리
        Integer totalCalorie;
        if (request.getExerciseCalorie() != null) {
            totalCalorie = request.getExerciseCalorie();
        } else {
            totalCalorie = exerciseType.getExerciseCalorie() * request.getExerciseTime();
        }

        Exercise updatedExercise = Exercise.builder()
                .exerciseId(exercise.getExerciseId())
                .userId(exercise.getUserId())  // 기존 userId 유지
                .exerciseTypeId(request.getExerciseTypeId())
                .exerciseDate(request.getExerciseDate())
                .exerciseTime(request.getExerciseTime())
                .exerciseCalorie(totalCalorie)
                .createdAt(exercise.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .deletedAt(exercise.getDeletedAt())
                .isDeleted(exercise.getIsDeleted())
                .build();

        // 4. 변경 사항 저장
        Exercise savedExercise = exerciseRepository.save(updatedExercise);

        // 5. 카테고리 정보 조회
        ExerciseCategory exerciseCategory = exerciseCategoryRepository.findById(exerciseType.getExerciseCategoryId())
                .orElse(null);

        // 6. 매퍼를 사용하여 응답 DTO 생성 및 반환
        return exerciseMapper.toListResponse(savedExercise, exerciseType, exerciseCategory, totalCalorie);
    }

    // 운동 기록 삭제
    @Transactional
    public void deleteExercise(Integer userId, Integer exerciseId) {
        // 1. 운동 기록 조회 (해당 사용자 확인)
        Exercise exercise = exerciseRepository.findByExerciseIdAndUserId(exerciseId, userId)
                .orElseThrow(() -> new NotFoundException("해당 사용자의 운동 기록을 찾을 수 없습니다."));

        // 이미 삭제된 운동 기록인 경우 예외 발생
        if (exercise.getIsDeleted()) {
            throw new BadRequestException("이미 삭제된 운동 기록입니다.");
        }

        // 2. 소프트 삭제 처리
        Exercise deletedExercise = Exercise.builder()
                .exerciseId(exercise.getExerciseId())
                .userId(exercise.getUserId())
                .exerciseTypeId(exercise.getExerciseTypeId())
                .exerciseDate(exercise.getExerciseDate())
                .exerciseTime(exercise.getExerciseTime())
                .exerciseCalorie(exercise.getExerciseCalorie())
                .createdAt(exercise.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .isDeleted(true)
                .build();

        // 3. 운동 기록 저장
        exerciseRepository.save(deletedExercise);
    }

    // 운동 카테고리 전체 조회
    @Transactional(readOnly = true)
    public List<ExerciseCategoryResponse> getAllCategories() {
        List<ExerciseCategory> categories = exerciseCategoryRepository.findByIsDeletedFalse();

        return categories.stream()
                .map(category -> ExerciseCategoryResponse.builder()
                        .exerciseCategoryId(category.getExerciseCategoryId())
                        .exerciseCategoryName(category.getExerciseCategoryName())
                        .createdAt(category.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 카테고리별 운동 조회
    @Transactional(readOnly = true)
    public List<ExerciseTypeResponse> getExercisesByCategory(Integer exerciseCategoryId) {
        // 카테고리 존재 여부 확인
        ExerciseCategory category = exerciseCategoryRepository.findById(exerciseCategoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 운동 카테고리입니다."));

        // 해당 카테고리의 운동 타입 조회
        List<ExerciseType> exerciseTypes = exerciseTypeRepository
                .findByExerciseCategoryIdAndIsDeletedFalse(exerciseCategoryId);

        return exerciseTypes.stream()
                .map(exerciseType -> ExerciseTypeResponse.builder()
                        .exerciseTypeId(exerciseType.getExerciseTypeId())
                        .exerciseCategoryId(exerciseType.getExerciseCategoryId())
                        .exerciseName(exerciseType.getExerciseName())
                        .exerciseCalorie(exerciseType.getExerciseCalorie())
                        .createdAt(exerciseType.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
