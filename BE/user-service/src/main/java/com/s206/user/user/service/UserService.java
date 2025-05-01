package com.s206.user.user.service;

import com.s206.common.exception.types.BadRequestException;
import com.s206.common.exception.types.ConflictException;
import com.s206.common.exception.types.NotFoundException;
import com.s206.user.user.dto.request.UserCreateRequest;
import com.s206.user.user.dto.request.UserUpdateRequest;
import com.s206.user.user.dto.response.UserDetailResponse;
import com.s206.user.user.dto.response.UserExistResponse;
import com.s206.user.user.entity.User;
import com.s206.user.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDetailResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("이미 가입된 이메일입니다.");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("이미 등록된 전화번호입니다.");
        }

        User user = User.builder()
                .name(request.getName())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .height(request.getHeight())
                .weight(request.getWeight())
                .birthday(request.getBirthday())
                .phone(request.getPhone())
                .email(request.getEmail())
                .provider(request.getProvider())
                .consentPersonal(request.getConsentPersonal())
                .locationPersonal(request.getLocationPersonal())
                .build();

        userRepository.save(user);
        return UserDetailResponse.toDto(user);
    }


    public UserDetailResponse getUserByUserId(Integer userId) {
        User user = validUser(userId);

        return UserDetailResponse.toDto(user);
    }

    @Transactional
    public UserDetailResponse updateUser(Integer userId, UserUpdateRequest request) {
        User user = validUser(userId);

        if (request.getNickname() != null) {
            if (request.getNickname().isBlank()) {
                throw new BadRequestException("닉네임은 비어있을 수 없습니다.");
            }
            user.updateNickname(request.getNickname());
        }

        if (request.getPhone() != null) {
            if (request.getPhone().isBlank()) {
                throw new BadRequestException("전화번호는 비어있을 수 없습니다.");
            }
            user.updatePhone(request.getPhone());
        }

        if (request.getHeight() != null) {
            if (request.getHeight().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("키는 0보다 커야 합니다.");
            }
            user.updateHeight(request.getHeight());
        }

        if (request.getWeight() != null) {
            if (request.getWeight().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("몸무게는 0보다 커야 합니다.");
            }
            user.updateWeight(request.getWeight());
        }

        if (request.getBirthday() != null) {
            if (request.getBirthday().isAfter(LocalDate.now())) {
                throw new BadRequestException("생일은 미래일 수 없습니다.");
            }
            user.updateBirthday(request.getBirthday());
        }

        if (request.getNotificationEnabled() != null) {
            user.updateNotificationEnabled(request.getNotificationEnabled());
        }

        return UserDetailResponse.toDto(user);
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = validUser(userId);
        user.delete();
        userRepository.save(user);
    }


    public UserExistResponse existsByEmail(String email, String provider) {

        Optional<User> optionalUser = userRepository.findByEmailAndProvider(email, provider);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return UserExistResponse.toDto(user, true);
        } else {
            return UserExistResponse.toDto(false);
        }

    }

    private User validUser(Integer userId) {
        User user = userRepository.findUserByUserId(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

        if (user.getIsDeleted()) {
            throw new BadRequestException("이미 탈퇴한 회원입니다.");
        }

        return user;
    }

}
