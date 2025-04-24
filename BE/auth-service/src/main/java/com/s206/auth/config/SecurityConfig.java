package com.s206.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API 서버 csrf 비활성화
                .csrf(csrf -> csrf.disable())

                // JWT 사용으로 세션 사용 X
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 개발 중 포인트 모두 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/oauth2/**",                // OAuth 콜백
                                "/login/**",                  // Spring Security 자동 제공
                                "/auth/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/oauth2/success", true)
                );
        return http.build();
    }
}
