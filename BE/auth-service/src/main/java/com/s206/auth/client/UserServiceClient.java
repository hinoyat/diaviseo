package com.s206.auth.client;

import com.s206.auth.client.dto.UserExistResponse;
import com.s206.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder loadBalancedWebClientBuilder;

    private static final String USER_SERVICE_BASE_URL = "http://user-service";


    public UserExistResponse getUserInfo(String email, String provider) {
        ResponseDto<UserExistResponse> response = loadBalancedWebClientBuilder
                .baseUrl(USER_SERVICE_BASE_URL)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/exist")
                        .queryParam("email", email)
                        .queryParam("provider", provider)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseDto<UserExistResponse>>() {})
                .block(); // 동기 호출

        if (response == null || response.getData() == null) {
            throw new RuntimeException("UserService 응답이 올바르지 않습니다.");
        }

        return response.getData();
    }

}
