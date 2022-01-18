package com.firefly.orderManagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firefly.orderManagement.domain.event.model.DomainEvent;
import com.firefly.orderManagement.domain.event.model.ResultWithEvents;
import com.firefly.orderManagement.domain.event.order.OrderDeletedEvent;
import com.firefly.orderManagement.exception.NoOrderWithIdException;
import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.repository.order.OrderRepository;
import com.firefly.orderManagement.repository.outbox.DomainEventEntityModel;
import com.firefly.orderManagement.repository.outbox.OutboxRepository;


@Service
public class OrderService {
	private OrderRepository orderRepository;
	private OutboxRepository outboxRepository;
	
	@Autowired
	public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository) {
		this.orderRepository = orderRepository;
		this.outboxRepository = outboxRepository;
	}
	
	@Transactional
	public UUID createOrder(OrderDetails orderDetails) {
		ResultWithEvents<OrderEntityModel> resultWithEvents = OrderEntityModel.createOrder(orderDetails);
		OrderEntityModel persistedOrder = orderRepository.saveAndFlush(resultWithEvents.result);
		saveDomainEvents(resultWithEvents.events);
		return persistedOrder.getId();
	}
	
	@Transactional
	public void updateOrderWithId(UUID id, OrderDetails orderDetails) throws NoOrderWithIdException {
		Optional<OrderEntityModel> order = orderRepository.findByIdForUpdating(id);
		if (order.isPresent()) {
			OrderEntityModel persistedOrderToBeUpdated = order.get();
			ResultWithEvents<OrderEntityModel> resultWithEvents = persistedOrderToBeUpdated.editOrder(orderDetails);
			persistedOrderToBeUpdated = orderRepository.saveAndFlush(resultWithEvents.result);
			saveDomainEvents(resultWithEvents.events);
		} else {
			throw new NoOrderWithIdException(id);
		}
	}
	
	@Transactional
	public void deleteOrderWithId(UUID id) {
		orderRepository.deleteById(id);
		OrderDeletedEvent orderDeletedEvent = new OrderDeletedEvent(id);
		saveDomainEvents(List.of(orderDeletedEvent));
	}
	
	public OrderEntityModel getOrderWithId(UUID id) throws NoOrderWithIdException {
		Optional<OrderEntityModel> order = orderRepository.findById(id);
		if (order.isPresent()) {
			return order.get();
		} else {
			throw new NoOrderWithIdException(id);
		}
	}
	
	private void saveDomainEvents(List<DomainEvent> domainEvents) {
		List<DomainEventEntityModel> domainEventEntities = domainEvents.stream().map(event -> {
			try {
				return new DomainEventEntityModel(event);
			} catch (Exception e) {
				throw new RuntimeException();
			}
		})
				.collect(Collectors.toList());
		outboxRepository.saveAll(domainEventEntities);
	}
}
