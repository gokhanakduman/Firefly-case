package com.firefly.reporting.integration.controller.event;

import static org.mockito.Mockito.timeout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firefly.reporting.configuration.RabbitMQConfig;
import com.firefly.reporting.containers.DatabasePostgresqlDocker;
import com.firefly.reporting.containers.RabbitMqDocker;
import com.firefly.reporting.controller.event.OrderEventConsumer;
import com.firefly.reporting.domain.event.DomainEvent;
import com.firefly.reporting.util.TestConstants;

@SpringBootTest
//@Import(RabbitMqIntegrationTests.TestConfig.class)
@ContextConfiguration(initializers = {
		DatabasePostgresqlDocker.Initializer.class,
		RabbitMqDocker.Initializer.class
		})

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RabbitMqIntegrationTests {
	
	@TestConfiguration
	public static class TestConfig{
		@Primary
		@Bean
		public OrderEventConsumer orderEventConsumer() {
			return Mockito.mock(OrderEventConsumer.class);
		}
	}
	
	@Autowired
	OrderEventConsumer orderEventConsumer;
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	RabbitAdmin rabbitAdmin;
	
	@BeforeEach
	public void clearQueue() {
		rabbitAdmin.purgeQueue(RabbitMQConfig.ORDER_QUEUE_NAME);
	}
	
	// TODO: Known issue here is that when I only run this, this is working
	// But when I run completely, this fails, and I could not find the reason within the deadline
	// I need more time to figure out
	@Test
	public void whenMessageReceived_thenOrderEventConsumerCalled() throws JsonProcessingException, AmqpException {
		sendMessageToQueue();
		Mockito.verify(orderEventConsumer, timeout(10000).atLeast(1)).handle(Mockito.any(DomainEvent.class));
	}
	
	private void sendMessageToQueue() throws JsonProcessingException, AmqpException {
		rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_QUEUE_NAME, TestConstants.GET_ANKARA_ORDER_CREATED_EVENT());
	}
}
