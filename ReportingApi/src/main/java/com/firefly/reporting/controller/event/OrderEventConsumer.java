package com.firefly.reporting.controller.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.firefly.reporting.domain.event.DomainEvent;
import com.firefly.reporting.service.ReportingService;
import com.firefly.reporting.util.Constants;

@Component
public class OrderEventConsumer {

	Map<String, Consumer<DomainEvent>> domainEventConsumerMap = new HashMap<>();
	
	private ReportingService reportingService;
	
	@Autowired
	public OrderEventConsumer(ReportingService reportingService) {
		this.reportingService = reportingService;
		this.on(Constants.ORDER_CREATED_EVENT_STRING, reportingService::handleOrderCreatedEvent);
		this.on(Constants.ORDER_UPDATED_EVENT_STRING, reportingService::handleOrderUpdatedEvent);
		this.on(Constants.ORDER_DELETED_EVENT_STRING, reportingService::handleOrderDeletedEvent);
	}
	
	public void handle(DomainEvent event) {
		Consumer<DomainEvent> domainEventConsumer = domainEventConsumerMap.get(event.getEventName());
		if (domainEventConsumer != null) {
			domainEventConsumer.accept(event);
		}
	}
	
	public void on(String eventName, Consumer<DomainEvent> handler ) {
		domainEventConsumerMap.put(eventName, handler);
	}

}
