package com.firefly.orderManagement.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.firefly.orderManagement.exception.NoOrderWithIdException;
import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.repository.outbox.DomainEventEntityModel;
import com.firefly.orderManagement.service.OrderService;
import com.firefly.orderManagement.util.Constants;
import com.firefly.orderManagement.util.MockBeanProvider;
import com.firefly.orderManagement.util.TestConstants;

public class OrderServiceUnitTest {
	private OrderService orderService = new OrderService(MockBeanProvider.mockOrderRepository(), MockBeanProvider.mockOutboxRepository());
	
	@BeforeEach
	public void clearMockProvider() {
		MockBeanProvider.savedEventEntityModels.clear();
	}
	
	@Test
	public void whenCreateOrder_thenOrderAndOutboxSave() {
		UUID savedOrderId = orderService.createOrder(TestConstants.ORDER_DETAILS);
		assertEquals(savedOrderId, TestConstants.MOCK_ORDER_REPOSITORY_ORDER_ID_TO_BE_SAVED);
		
		OrderEntityModel savedOrderEntityModel = MockBeanProvider.savedOrUpdatedEntityModel;
		assertNotNull(savedOrderEntityModel);
		assertEquals(0, CompareToBuilder.reflectionCompare(savedOrderEntityModel.getOrderDetails(), TestConstants.ORDER_DETAILS));
		
		DomainEventEntityModel savedEvent = MockBeanProvider.savedEventEntityModels.get(0);
		assertNotNull(savedEvent);
		assertEquals(savedOrderEntityModel.getId(), savedEvent.getAggragateId());
		assertEquals(savedEvent.getEventName(), Constants.Event.ORDER_CREATED);
	}
	
	@Test
	public void whenUpdateNonExistingOrder_thenThrow() {
		assertThrows(NoOrderWithIdException.class,  () -> {
			orderService.updateOrderWithId(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID, TestConstants.ORDER_DETAILS);
		});
	}
	
	@Test
	public void whenUpdateExistingOrderNoLocationChange_thenOrderEditWithoutOutboxSave() throws NoOrderWithIdException {
		OrderDetails orderDetails = TestConstants.ORDER_DETAILS.copy();
		orderDetails.setLatitude(null);
		orderDetails.setPrice(orderDetails.getPrice() + 5);
		orderService.updateOrderWithId(TestConstants.getSavedMockEntityModel().getId(), orderDetails);
		
		OrderEntityModel savedOrderEntityModel = MockBeanProvider.savedOrUpdatedEntityModel;
		assertNotNull(savedOrderEntityModel);
		orderDetails.setLatitude(TestConstants.ORDER_DETAILS.getLatitude());
		assertEquals(0, CompareToBuilder.reflectionCompare(savedOrderEntityModel.getOrderDetails(), orderDetails));
		
		assertEquals(0, MockBeanProvider.savedEventEntityModels.size());
	}
	
	@Test
	public void whenUpdateExistingOrderWithLocationChange_thenOrderEditOutboxSave() throws NoOrderWithIdException {
		OrderDetails orderDetails = TestConstants.ORDER_DETAILS.copy();
		orderDetails.setLatitude(TestConstants.ORDER_DETAILS.getLatitude() + 2);
		orderDetails.setPrice(orderDetails.getPrice() + 5);
		orderService.updateOrderWithId(TestConstants.getSavedMockEntityModel().getId(), orderDetails);
		
		OrderEntityModel savedOrderEntityModel = MockBeanProvider.savedOrUpdatedEntityModel;
		assertNotNull(savedOrderEntityModel);
		assertEquals(0, CompareToBuilder.reflectionCompare(savedOrderEntityModel.getOrderDetails(), orderDetails));
		
		DomainEventEntityModel savedEvent = MockBeanProvider.savedEventEntityModels.get(0);
		assertNotNull(savedEvent);
		assertEquals(savedOrderEntityModel.getId(), savedEvent.getAggragateId());
		assertEquals(savedEvent.getEventName(), Constants.Event.ORDER_UPDATED);
	}
	
	
	@Test
	public void whenOrderDelete_thenOutboxSave() {
		orderService.deleteOrderWithId(TestConstants.MOCK_ORDER_REPOSITORY_ORDER_ID_TO_BE_SAVED);
		DomainEventEntityModel savedEvent = MockBeanProvider.savedEventEntityModels.get(0);
		assertNotNull(savedEvent);
		assertEquals(TestConstants.MOCK_ORDER_REPOSITORY_ORDER_ID_TO_BE_SAVED, savedEvent.getAggragateId());
	}
	
	@Test
	public void whenSavedOrderOrderGet_thenCorrectResponse() throws NoOrderWithIdException {
		OrderEntityModel model = orderService.getOrderWithId(TestConstants.getSavedMockEntityModel().getId());
		assertNotNull(model);
		assertEquals(0, CompareToBuilder.reflectionCompare(model, TestConstants.getSavedMockEntityModel()));
	}
	
	@Test
	public void whenNotExistingOrderGet_thenThrow() throws NoOrderWithIdException {
		assertThrows(NoOrderWithIdException.class,  () -> {
			orderService.getOrderWithId(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID);
		});
	}
	
}
