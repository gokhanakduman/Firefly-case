package com.firefly.orderManagement.integration.service;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;

import com.firefly.orderManagement.containers.DatabasePostgresqlDocker;
import com.firefly.orderManagement.containers.RabbitMqDocker;
import com.firefly.orderManagement.domain.event.order.OrderCreatedEvent;
import com.firefly.orderManagement.domain.event.publisher.DomainEventPublisher;
import com.firefly.orderManagement.repository.outbox.DomainEventEntityModel;
import com.firefly.orderManagement.repository.outbox.OutboxRepository;
import com.firefly.orderManagement.service.DomainEventPublisherService;
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

public class DomainEventPublisherServiceIntegrationTest {
	
	@Autowired
	DomainEventPublisherService domainEventPublisherService;
	
	@Autowired
	OutboxRepository outboxRepository;
	
	@Test
	public void givenOutbox_whenServicePublishCalled_ThenOutboxDeletedMessagePublishedWithoutException() throws Exception {
		saveToOutbox();
		assertDoesNotThrow(() -> {
			domainEventPublisherService.scheduleFixedRateTask();			
		});
		assertFalse(outboxRepository.findAll().iterator().hasNext());
	}
	
	private void saveToOutbox() throws Exception {
		DomainEventEntityModel model = new DomainEventEntityModel(new OrderCreatedEvent(TestConstants.getSavedMockEntityModel()));
		outboxRepository.save(model);
	}
	
}
