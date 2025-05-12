package com.s206.alert.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public TopicExchange alertExchange() {
		return new TopicExchange("alert-exchange");
	}

	@Bean
	public Queue pushQueue() {
		return new Queue("notification-queue");
	}

	@Bean
	public Binding pushBinding() {
		return BindingBuilder
				.bind(pushQueue())
				.to(alertExchange())
				.with("alert.push.#");
	}
}
