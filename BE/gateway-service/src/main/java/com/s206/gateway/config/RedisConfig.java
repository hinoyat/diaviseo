package com.s206.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        log.info("Redis 연결 설정: {}:{}", redisHost, redisPort);
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        log.info("Redis connection factory created");
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        log.info("ReactiveRedisTemplate 등록 시작");

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext()
                .key(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        log.info("ReactiveRedisTemplate 직렬화 설정 완료");
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    public org.springframework.data.redis.connection.RedisConnectionFactory syncRedisConnectionFactory(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port
    ) {
        return new org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory(host, port);
    }

    @Bean
    public org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate(
            org.springframework.data.redis.connection.RedisConnectionFactory syncRedisConnectionFactory
    ) {
        var t = new org.springframework.data.redis.core.RedisTemplate<String, Object>();
        t.setConnectionFactory(syncRedisConnectionFactory);

        t.setKeySerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
        t.setValueSerializer(new org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer<>(Object.class));
        t.setHashKeySerializer(new org.springframework.data.redis.serializer.StringRedisSerializer());
        t.setHashValueSerializer(new org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer<>(Object.class));
        t.afterPropertiesSet();
        return t;
    }

}