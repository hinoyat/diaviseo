package com.s206.user.physical.service;

import com.s206.common.exception.types.BadRequestException;
import com.s206.common.exception.types.NotFoundException;
import com.s206.user.physical.dto.request.UserPhysicalInfoSaveRequest;
import com.s206.user.physical.dto.response.UserPhysicalInfoResponse;
import com.s206.user.physical.entity.UserPhysicalInfo;
import com.s206.user.physical.repository.UserPhysicalInfoRepository;
import com.s206.user.user.entity.User;
import com.s206.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPhysicalInfoService {

    private final UserPhysicalInfoRepository physicalInfoRepository;
    private final UserRepository userRepository;

    public User getValidUser(Integer userId) {
        return userRepository.findUserByUserId(userId)
                .filter(u -> !u.getIsDeleted())
                .orElseThrow(() -> new BadRequestException("유효하지 않은 사용자입니다."));
    }

    @Transactional
    public void saveOrUpdate(Integer userId, UserPhysicalInfoSaveRequest request) {
        User user = getValidUser(userId);

        physicalInfoRepository.findByUserAndDate(user, request.getDate())
                .ifPresentOrElse(
                        existing -> existing.update(request.getHeight(), request.getWeight(), request.getBirthday(), request.getGoal()),
                        () -> {
                            UserPhysicalInfo info = UserPhysicalInfo.builder()
                                    .user(user)
                                    .height(request.getHeight())
                                    .weight(request.getWeight())
                                    .birthday(request.getBirthday())
                                    .goal(request.getGoal())
                                    .date(request.getDate())
                                    .build();
                            physicalInfoRepository.save(info);
                        }
                );
    }

    @Transactional(readOnly = true)
    public UserPhysicalInfoResponse getLatestBeforeDate(Integer userId, LocalDate date) {
        User user = getValidUser(userId);

        UserPhysicalInfo info = physicalInfoRepository
                .findTopByUserAndDateLessThanEqualOrderByDateDesc(user, date)
                .orElseThrow(() -> new NotFoundException("해당 날짜 이전에 등록된 신체 정보가 없습니다."));

        return UserPhysicalInfoResponse.toDto(info, user.getGender().name());
    }

    @Transactional(readOnly = true)
    public List<UserPhysicalInfoResponse> getAll(Integer userId) {
        User user = getValidUser(userId);

        return physicalInfoRepository.findAllByUserOrderByDateDesc(user).stream()
                .map(info -> UserPhysicalInfoResponse.toDto(info, user.getGender().name()))
                .collect(Collectors.toList());
    }
}
