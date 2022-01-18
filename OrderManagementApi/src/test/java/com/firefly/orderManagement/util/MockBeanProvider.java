package com.firefly.orderManagement.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.mockito.ArgumentMatchers;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import java.util.List;
import java.util.ArrayList;

import com.firefly.orderManagement.exception.NoOrderWithIdException;
import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.repository.order.OrderRepository;
import com.firefly.orderManagement.repository.outbox.DomainEventEntityModel;
import com.firefly.orderManagement.repository.outbox.OutboxRepository;
import com.firefly.orderManagement.service.OrderService;

public class MockBeanProvider {
	
	public static OrderEntityModel savedOrUpdatedEntityModel = null;
	
	public static OrderRepository mockOrderRepository() {
		OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
		Answer<OrderEntityModel> answer = new Answer<OrderEntityModel>() {
			@Override
			public OrderEntityModel answer(InvocationOnMock invocation) throws Throwable {
				OrderEntityModel model= invocation.getArgument(0, OrderEntityModel.class);
				if (model.getId() == null) {
					// CREATE
					model.setId(TestConstants.MOCK_ORDER_REPOSITORY_ORDER_ID_TO_BE_SAVED);					
				}
				savedOrUpdatedEntityModel = model;
				return model;
			}	
		};
		
		when(orderRepository.saveAndFlush(any(OrderEntityModel.class))).thenAnswer(answer);
		when(orderRepository.findByIdForUpdating(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID))
			.thenReturn(Optional.empty());
		when(orderRepository.findByIdForUpdating(TestConstants.getSavedMockEntityModel().getId()))
			.thenReturn(Optional.of(TestConstants.getSavedMockEntityModel()));
		when(orderRepository.findById(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID))
			.thenReturn(Optional.empty());
		when(orderRepository.findById(TestConstants.getSavedMockEntityModel().getId()))
			.thenReturn(Optional.of(TestConstants.getSavedMockEntityModel()));		
		
		return orderRepository;
	}
	
	public static List<DomainEventEntityModel> savedEventEntityModels = new ArrayList<>();
	
	public static OutboxRepository mockOutboxRepository() {
		OutboxRepository outboxRepository = Mockito.mock(OutboxRepository.class);
		Answer<Iterable<DomainEventEntityModel>> answer = new Answer<Iterable<DomainEventEntityModel>>() {
			@Override
			public Iterable<DomainEventEntityModel> answer(InvocationOnMock invocation) throws Throwable {
				Iterable<DomainEventEntityModel> iterable = invocation.getArgument(0);
				for(DomainEventEntityModel model : iterable) {
					savedEventEntityModels.add(model);
				}
				return iterable;
			}	
		};
		
		when(outboxRepository.saveAll(Mockito.anyList())).thenAnswer(answer);
		return outboxRepository;
	}
	
	public static OrderService mockOrderService() throws NoOrderWithIdException {
		OrderService orderService = Mockito.mock(OrderService.class);
		when(orderService.createOrder(any()))
			.thenReturn(TestConstants.MOCK_ORDER_REPOSITORY_ORDER_ID_TO_BE_SAVED);
		
		doThrow(NoOrderWithIdException.class)
			.when(orderService).updateOrderWithId(eq(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID), any(OrderDetails.class));
		doNothing().when(orderService).updateOrderWithId(eq(TestConstants.getSavedMockEntityModel().getId()), any(OrderDetails.class));
		doNothing().when(orderService).deleteOrderWithId(any());
		when(orderService.getOrderWithId(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID)).thenThrow(NoOrderWithIdException.class);
		when(orderService.getOrderWithId(TestConstants.getSavedMockEntityModel().getId())).thenReturn(TestConstants.getSavedMockEntityModel());		
		return orderService;
	}
}
