package com.firefly.orderManagement.domain.event.publisher;

import com.firefly.orderManagement.domain.event.model.DomainEvent;

public interface DomainEventPublisher {
	public void publishEvent(DomainEvent event) throws DomainEventPublishingException;
}
