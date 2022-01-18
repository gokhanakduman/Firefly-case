package com.firefly.orderManagement.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.firefly.orderManagement.domain.event.publisher.DomainEventPublisher;
import com.firefly.orderManagement.domain.event.publisher.RabbitMQDomainEventPublisher;

@Configuration
@EnableScheduling
public class RabbitMQConfig {
	
    public static final String ORDER_QUEUE_NAME = "orderQueue";

    @Bean
    public RabbitTemplate jsonRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
    
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE_NAME);
    }
    
    @Bean
    public DomainEventPublisher domainEventPublisher(RabbitTemplate template) {
    	return new RabbitMQDomainEventPublisher(template);
    }
}