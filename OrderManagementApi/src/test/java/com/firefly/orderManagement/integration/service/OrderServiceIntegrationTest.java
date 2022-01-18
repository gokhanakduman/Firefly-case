package com.firefly.orderManagement.integration.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;

import com.firefly.orderManagement.containers.DatabasePostgresqlDocker;
import com.firefly.orderManagement.exception.NoOrderWithIdException;
import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.repository.order.OrderRepository;
import com.firefly.orderManagement.repository.outbox.DomainEventEntityModel;
import com.firefly.orderManagement.repository.outbox.OutboxRepository;
import com.firefly.orderManagement.service.DomainEventPublisherService;
import com.firefly.orderManagement.service.OrderService;
import com.firefly.orderManagement.util.Constants;
import com.firefly.orderManagement.util.TestConstants;

@SpringBootTest
@ComponentScan(basePackages = {"com.firefly.orderManagement"}, excludeFilters={
		  @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=DomainEventPublisherService.class),
		  @ComponentScan.Filter(type=FilterType.ANNOTATION, value = TestConfiguration.class),
})
@ContextConfiguration(initializers = {DatabasePostgresqlDocker.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderServiceIntegrationTest {
	@Autowired
	OrderService orderService;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OutboxRepository outboxRepository;
	
	@BeforeEach
	public void clearTables() {
		orderRepository.deleteAll();
		outboxRepository.deleteAll();
	}
	
	@Test
	public void whenCreateOrder_thenOrderAndOutboxSave() {
		UUID savedOrderId = orderService.createOrder(TestConstants.ORDER_DETAILS);
		Optional<OrderEntityModel> savedModel = orderRepository.findById(savedOrderId);
		assertTrue(savedModel.isPresent());
		assertEquals(0, CompareToBuilder.reflectionCompare(savedModel.get().getOrderDetails(), TestConstants.ORDER_DETAILS));
		
		DomainEventEntityModel savedEvent = outboxRepository.findAll().iterator().next();
		assertNotNull(savedEvent);
		assertEquals(savedOrderId, savedEvent.getAggragateId());
		assertEquals(savedEvent.getEventName(), Constants.Event.ORDER_CREATED);
	}
	
	@Test
	public void whenUpdateNonExistingOrder_thenThrow() {
		assertThrows(NoOrderWithIdException.class,  () -> {
			orderService.updateOrderWithId(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID, TestConstants.ORDER_DETAILS);
		});
	}
	
	@Test
	public void givenSavedOrder_whenUpdateExistingOrderNoLocationChange_thenOrderEditWithoutOutboxSave() throws NoOrderWithIdException {
		OrderEntityModel savedOrder = saveOrder();
		
		OrderDetails orderDetails = TestConstants.ORDER_DETAILS.copy();
		orderDetails.setLatitude(null);
		orderDetails.setPrice(orderDetails.getPrice() + 5);
		orderService.updateOrderWithId(savedOrder.getId(), orderDetails);
		
		// Restore latitude and check models
		orderDetails.setLatitude(TestConstants.ORDER_DETAILS.getLatitude());
		OrderEntityModel updatedOrder = orderRepository.findById(savedOrder.getId()).get();
		assertEquals(0, CompareToBuilder.reflectionCompare(updatedOrder.getOrderDetails(), orderDetails));

		// Outbox empty
		assertFalse(outboxRepository.findAll().iterator().hasNext());
	}
	
	@Test
	public void givenSavedOrder_whenUpdateExistingOrderWithLocationChange_thenOrderEditOutboxSave() throws NoOrderWithIdException {
		OrderEntityModel savedOrder = saveOrder();
		
		OrderDetails orderDetails = TestConstants.ORDER_DETAILS.copy();
		orderDetails.setLatitude(TestConstants.ORDER_DETAILS.getLatitude() + 2);
		orderDetails.setPrice(orderDetails.getPrice() + 5);
		orderService.updateOrderWithId(savedOrder.getId(), orderDetails);
		
		// check models
		OrderEntityModel updatedOrder = orderRepository.findById(savedOrder.getId()).get();
		assertEquals(0, CompareToBuilder.reflectionCompare(updatedOrder.getOrderDetails(), orderDetails));
		
		// Check Event
		DomainEventEntityModel updatedEvent = outboxRepository.findAll().iterator().next();
		assertNotNull(updatedEvent);
		assertEquals(savedOrder.getId(), updatedEvent.getAggragateId());
		assertEquals(updatedEvent.getEventName(), Constants.Event.ORDER_UPDATED);
	}
	
	@Test
	public void givenSavedOrder_whenOrderDelete_thenOutboxSave() {
		OrderEntityModel savedOrder = saveOrder();
		orderService.deleteOrderWithId(savedOrder.getId());
		
		DomainEventEntityModel deletedEvent = outboxRepository.findAll().iterator().next();
		assertNotNull(deletedEvent);
		assertEquals(savedOrder.getId(), deletedEvent.getAggragateId());
		assertEquals(deletedEvent.getEventName(), Constants.Event.ORDER_DELETED);
	}
	
	@Test
	public void givenSavedOrder_whenOrderGet_thenCorrectResponse() throws NoOrderWithIdException {
		OrderEntityModel savedOrder = saveOrder();
		OrderEntityModel getOrder = orderService.getOrderWithId(savedOrder.getId());
		assertNotNull(getOrder);
		assertEquals(getOrder.getId(), savedOrder.getId());
		assertEquals(0, CompareToBuilder.reflectionCompare(getOrder.getOrderDetails(), savedOrder.getOrderDetails()));
	}
	
	@Test
	public void whenNotExistingOrderGet_thenThrow() throws NoOrderWithIdException {
		assertThrows(NoOrderWithIdException.class,  () -> {
			orderService.getOrderWithId(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID);
		});
	}
	
	private OrderEntityModel saveOrder() {
		return orderRepository.save(TestConstants.getSavedMockEntityModel());
	}
	
}
