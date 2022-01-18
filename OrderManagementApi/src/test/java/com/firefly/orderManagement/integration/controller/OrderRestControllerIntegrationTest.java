package com.firefly.orderManagement.integration.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.orderManagement.containers.DatabasePostgresqlDocker;
import com.firefly.orderManagement.exception.NoOrderWithIdException;
import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.repository.order.OrderRepository;
import com.firefly.orderManagement.service.DomainEventPublisherService;
import com.firefly.orderManagement.service.OrderService;
import com.firefly.orderManagement.util.MockBeanProvider;
import com.firefly.orderManagement.util.TestConstants;

@SpringBootTest
@ComponentScan(basePackages = {"com.firefly.orderManagement"}, excludeFilters={
		  @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=DomainEventPublisherService.class),
		  @ComponentScan.Filter(type=FilterType.ANNOTATION, value = TestConfiguration.class),
})
@ContextConfiguration(initializers = {DatabasePostgresqlDocker.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class OrderRestControllerIntegrationTest {
	@Autowired
	MockMvc mvc;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	OrderRepository orderRepository;
	
	@Test
	public void whenCreateOrder_thenCorrectResponse() throws Exception {
		mvc.perform(
			post("/order")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestConstants.ORDER_DETAILS))
		)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").exists());
	}
	
	@Test
	public void whenCreateOrderWithNullFields_then400ErrorResponse() throws Exception {
		OrderDetails orderDetails = TestConstants.ORDER_DETAILS.copy();
		orderDetails.setLatitude(null);
		mvc.perform(
			post("/order")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(orderDetails))
		)
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
	
	@Test
	public void whenUpdateNonExistingOrder_then404ErrorResponse() throws Exception {
		mvc.perform(
			put("/order/{id}", TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID.toString())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestConstants.ORDER_DETAILS))
		)
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	public void whenUpdateExistingOrderWithAllNullParameters_then400ErrorResponse() throws Exception {
		OrderDetails orderDetails = new OrderDetails();
		mvc.perform(
				put("/order/{id}", TestConstants.getSavedMockEntityModel().getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(orderDetails))
			)
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
	
	@Test
	public void whenUpdateExistingOrder_thenCorrectResponse() throws Exception {
		OrderDetails details = TestConstants.ORDER_DETAILS.copy();
		details.setPrice(10f);
		details.setLatitude(details.getLatitude() + 2);
		OrderEntityModel modelToUpdate = orderRepository.save(new OrderEntityModel(details));
		mvc.perform(
			put("/order/{id}", modelToUpdate.getId().toString())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestConstants.ORDER_DETAILS))
		)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId", is(modelToUpdate.getId().toString())));
	}
	
	@Test
	public void whenGetNonExistingOrder_then404ErrorResponse() throws Exception {
		mvc.perform(
			get("/order/{id}", TestConstants.MOCK_ORDER_REPOSITORY_NON_EXISTING_ID.toString())
		)
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
	}
	
	@Test
	public void whenGetExistingOrder_thenCorrectResponse() throws Exception {
		OrderEntityModel modelToGet = orderRepository.save(new OrderEntityModel(TestConstants.ORDER_DETAILS));
		MvcResult mvcResult = mvc.perform(
			get("/order/{id}", modelToGet.getId().toString())
		)
        .andExpect(status().isOk())
        .andReturn();
		
		OrderEntityModel responseModel = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), OrderEntityModel.class);
		assertEquals(responseModel.getId(), modelToGet.getId());
		assertEquals(0, CompareToBuilder.reflectionCompare(responseModel.getOrderDetails(), modelToGet.getOrderDetails()));
	}
	
	@Test
	public void whenDeleteOrder_thenCorrectResponse() throws Exception {
		OrderEntityModel modelToDelete = orderRepository.save(new OrderEntityModel(TestConstants.ORDER_DETAILS));
		mvc.perform(
			delete("/order/{id}", modelToDelete.getId().toString())
		)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId", is(modelToDelete.getId().toString())));
	}
}
