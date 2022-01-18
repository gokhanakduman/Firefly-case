package com.firefly.orderManagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.firefly.orderManagement.domain.event.publisher.DomainEventPublisher;
import com.firefly.orderManagement.domain.event.publisher.DomainEventPublishingException;
import com.firefly.orderManagement.repository.outbox.DomainEventEntityModel;
import com.firefly.orderManagement.repository.outbox.OutboxRepository;

@Service
public class DomainEventPublisherService {

	private OutboxRepository outboxRepository;
	private DomainEventPublisher domainEventPublisher;
	
	@Autowired
	public DomainEventPublisherService(DomainEventPublisher domainEventPublisher, OutboxRepository outboxRepository) {
		this.domainEventPublisher = domainEventPublisher;
		this.outboxRepository = outboxRepository;
	}
	
	//@Scheduled(fixedRate = 1000)
	public void scheduleFixedRateTask() throws DomainEventPublishingException {
	    List<DomainEventEntityModel> models = outboxRepository.findTop10ByOrderByEventTimeDesc();
	    for (DomainEventEntityModel model : models) {
			domainEventPublisher.publishEvent(model);
			outboxRepository.delete(model);
		}
	}

}
