package com.firefly.orderManagement.integration.service;

import static org.awaitility.Awaitility.await;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;

import com.firefly.orderManagement.configuration.RabbitMQConfig;
import com.firefly.orderManagement.containers.DatabasePostgresqlDocker;
import com.firefly.orderManagement.containers.RabbitMqDocker;
import com.firefly.orderManagement.domain.event.model.DomainEvent;
import com.firefly.orderManagement.domain.event.order.OrderCreatedEvent;
import com.firefly.orderManagement.domain.event.publisher.DomainEventPublisher;
import com.firefly.orderManagement.domain.event.publisher.DomainEventPublishingException;
import com.firefly.orderManagement.util.TestConstants;

@SpringBootTest
@ComponentScan(basePackages = {"com.firefly.orderManagement"}, excludeFilters={
		  @ComponentScan.Filter(type=FilterType.ANNOTATION, value = TestConfiguration.class)
})
@ContextConfiguration(initializers = {
		DatabasePostgresqlDocker.Initializer.class,
		RabbitMqDocker.Initializer.class
		})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class DomainEventPublisherIntegrationTest {

	@Autowired
	DomainEventPublisher domainEventPublisher;
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	RabbitAdmin rabbitAdmin;
	
	private static DomainEvent publishedDomainEvent;
	
	@BeforeEach
	public void clearQueue() {
		rabbitAdmin.purgeQueue(RabbitMQConfig.ORDER_QUEUE_NAME);
		publishedDomainEvent = null;
	}
	
	@Test
	public void whenPublishTestMessageInQueue_thenExpectMessageInQueue() throws DomainEventPublishingException {
		OrderCreatedEvent event = new OrderCreatedEvent(TestConstants.getSavedMockEntityModel());
		publishedDomainEvent = event;
		domainEventPublisher.publishEvent(event);
		await().atMost(150, TimeUnit.SECONDS).until(isMessagePublished(event));
	}
	
	private Callable<Boolean> isMessagePublished(DomainEvent domainEvent) {
		return new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try {
					Message message = rabbitTemplate.receive(RabbitMQConfig.ORDER_QUEUE_NAME);
					if (message == null) {
						return false;
					}
					Message publishedMessage = rabbitTemplate.getMessageConverter().toMessage(publishedDomainEvent, null);
					return Arrays.equals(message.getBody(), publishedMessage.getBody());
					
				} catch (Exception e) {
					return false;
				}
			}
		};
	}
	
}
