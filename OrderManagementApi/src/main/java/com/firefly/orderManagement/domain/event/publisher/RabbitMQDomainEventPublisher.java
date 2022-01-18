package com.firefly.orderManagement.domain.event.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.firefly.orderManagement.configuration.RabbitMQConfig;
import com.firefly.orderManagement.domain.event.model.DomainEvent;


public class RabbitMQDomainEventPublisher implements DomainEventPublisher{
	RabbitTemplate template;
	
	public RabbitMQDomainEventPublisher(RabbitTemplate template) {
		this.template = template;
	}

	@Override
	public void publishEvent(DomainEvent event) throws DomainEventPublishingException {
		try {
			template.convertAndSend(RabbitMQConfig.ORDER_QUEUE_NAME,event);
		} catch (Exception e) {
			throw new DomainEventPublishingException();
		}
	}
}
