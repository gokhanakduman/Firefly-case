package com.firefly.orderManagement.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import com.firefly.orderManagement.containers.DatabasePostgresqlDocker;
import com.firefly.orderManagement.domain.event.model.DomainEvent;
import com.firefly.orderManagement.domain.event.order.OrderCreatedEvent;
import com.firefly.orderManagement.repository.outbox.DomainEventEntityModel;
import com.firefly.orderManagement.repository.outbox.OutboxRepository;
import com.firefly.orderManagement.util.TestConstants;

@DataJpaTest
@ContextConfiguration(initializers = {DatabasePostgresqlDocker.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OutboxRepositoryIntegrationTest extends DatabasePostgresqlDocker {
	@Resource
	OutboxRepository outboxRepository;
	
	@Test
	public void whenSaveDomainEvent_thenSuccess() throws Exception {
		DomainEvent domainEvent = new OrderCreatedEvent(TestConstants.getSavedMockEntityModel());
		DomainEventEntityModel model = new DomainEventEntityModel(domainEvent);
		DomainEventEntityModel savedModel = outboxRepository.save(model);
		assertEquals(0, CompareToBuilder.reflectionCompare(model, savedModel));
	}
}
