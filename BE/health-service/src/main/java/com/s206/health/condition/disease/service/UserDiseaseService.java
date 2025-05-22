package com.s206.health.condition.disease.service;

import com.s206.common.exception.types.NotFoundException;
import com.s206.health.condition.disease.dto.response.DiseaseResponse;
import com.s206.health.condition.disease.dto.response.UserDiseaseResponse;
import com.s206.health.condition.disease.dto.response.UserDiseaseToggleResponse;
import com.s206.health.condition.disease.entity.Disease;
import com.s206.health.condition.disease.entity.UserDisease;
import com.s206.health.condition.disease.repository.DiseaseRepository;
import com.s206.health.condition.disease.repository.UserDiseaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDiseaseService {

    private final UserDiseaseRepository userDiseaseRepository;
    private final DiseaseRepository diseaseRepository;

    @Transactional
    public UserDiseaseToggleResponse toggleDisease(Integer userId, Long diseaseId) {
        UserDisease existing = userDiseaseRepository.findByUserIdAndDiseaseId(userId, diseaseId).orElse(null);

        if (existing != null) {
            userDiseaseRepository.delete(existing);
            return UserDiseaseToggleResponse.toDto(existing, false);
        }

        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new NotFoundException("해당 질환을 찾을 수 없습니다."));

        UserDisease newDisease = userDiseaseRepository.save(UserDisease.builder()
                .userId(userId)
                .disease(disease)
                .build());

        return UserDiseaseToggleResponse.toDto(newDisease, true);
    }

    @Transactional(readOnly = true)
    public List<UserDiseaseResponse> getUserDiseases(Integer userId) {
        return userDiseaseRepository.findAllByUserId(userId).stream()
                .map(UserDiseaseResponse::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DiseaseResponse> getAllDiseases() {
        return diseaseRepository.findAll().stream()
                .map(DiseaseResponse::toDto)
                .collect(Collectors.toList());
    }
}
