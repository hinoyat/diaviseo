package com.s206.health.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
	@Bean
	public TopicExchange alertExchange(){
		return new TopicExchange("alert-exchange");
	}
}
