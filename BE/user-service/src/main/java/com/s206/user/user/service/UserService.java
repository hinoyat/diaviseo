package com.s206.user.user.service;

import com.s206.common.exception.types.ConflictException;
import com.s206.common.exception.types.NotFoundException;
import com.s206.user.user.dto.request.UserCreateRequest;
import com.s206.user.user.dto.response.UserDetailResponse;
import com.s206.user.user.entity.User;
import com.s206.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserDetailResponse createUser(UserCreateRequest userCreateRequest) {
        if (userRepository.existsByEmail(userCreateRequest.getEmail())) {
            throw new ConflictException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .name(userCreateRequest.getName())
                .nickname(userCreateRequest.getNickname())
                .gender(userCreateRequest.getGender())
                .birthday(userCreateRequest.getBirthday())
                .phone(userCreateRequest.getPhone())
                .email(userCreateRequest.getEmail())
                .consentPersonal(userCreateRequest.getConsentPersonal())
                .locationPersonal(userCreateRequest.getLocationPersonal())
                .build();
        userRepository.save(user);

        return UserDetailResponse.toDto(user);
    }

    public UserDetailResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("해당 이메일로 가입된 유저가 없습니다."));

        return UserDetailResponse.toDto(user);
    }


    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
