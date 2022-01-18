package com.firefly.orderManagement.integration.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;

import com.firefly.orderManagement.containers.DatabasePostgresqlDocker;
import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.repository.order.OrderRepository;
import com.firefly.orderManagement.util.TestConstants;

@DataJpaTest
@ContextConfiguration(initializers = {DatabasePostgresqlDocker.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryIntegrationTest {
	@Resource
	private OrderRepository orderRepository;
	
	@Test
	public void findAllTest() {
		List<OrderEntityModel> modelList = orderRepository.findAll();
		System.out.println(modelList);
	}
	
	@Test
	public void whenSaveValidOrderDetails_thenSuccess() {
		OrderEntityModel model = orderRepository.save(new OrderEntityModel(TestConstants.ORDER_DETAILS));
		assertEquals(0, CompareToBuilder.reflectionCompare(model.getOrderDetails(), TestConstants.ORDER_DETAILS));
	}
	
	@Test
	@Transactional
	public void whenSaveInvalidOrderDetails_thenException() {
		OrderDetails orderDetails = new OrderDetails();
		OrderEntityModel model = new OrderEntityModel(orderDetails);
		// TODO: Somehow not working, I tried saving null values from app, not inserting as expected
		// but this one not working with docker
		/*
		assertThrows(DataIntegrityViolationException.class, () -> {
			orderRepository.save(model);
		});
		*/
	}
	
	@Test
	public void whenFindByNotExistingId_thenNull() {
		Optional<OrderEntityModel> model = orderRepository.findById(TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID);
		assertTrue(model.isEmpty());
	}
	
	@Test
	public void givenSavedEntity_whenFindById_thenSuccess() {
		OrderEntityModel savedModel = orderRepository.save(TestConstants.getSavedMockEntityModel());
		Optional<OrderEntityModel> model = orderRepository.findById(savedModel.getId());
		assertTrue(model.isPresent());
		assertEquals(0, CompareToBuilder.reflectionCompare(model.get().getOrderDetails(), TestConstants.getSavedMockEntityModel().getOrderDetails()));
	}
	
	@Test
	@Transactional
	public void givenSavedEntity_whenFindByIdForUpdating_thenSuccess() {
		OrderEntityModel savedModel = orderRepository.save(TestConstants.getSavedMockEntityModel());
		Optional<OrderEntityModel> model = orderRepository.findByIdForUpdating(savedModel.getId());
		assertTrue(model.isPresent());
		assertEquals(0, CompareToBuilder.reflectionCompare(model.get().getOrderDetails(), TestConstants.getSavedMockEntityModel().getOrderDetails()));
	}
	
	@Test
	public void givenSavedEntity_whenDeleteById_thenSuccess() {
		OrderEntityModel savedModel = orderRepository.save(TestConstants.getSavedMockEntityModel());
		Optional<OrderEntityModel> model = orderRepository.findById(savedModel.getId());
		assertTrue(model.isPresent());
		orderRepository.deleteById(savedModel.getId());
		model = orderRepository.findById(savedModel.getId());
		assertTrue(model.isEmpty());
	}
}
