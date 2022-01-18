package com.firefly.orderManagement.unit.controller;

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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.orderManagement.exception.NoOrderWithIdException;
import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.service.OrderService;
import com.firefly.orderManagement.util.MockBeanProvider;
import com.firefly.orderManagement.util.TestConstants;


@WebMvcTest
//@ContextConfiguration(classes= {OrderRestControllerUnitTest.MockConfiguration.class})
//@ComponentScan(basePackages = "com.firefly.orderManagement")

public class OrderRestControllerUnitTest {
	@Autowired
	MockMvc mvc;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@TestConfiguration
	public static class MockConfiguration {
		@Primary
		@Bean
		OrderService orderService() throws NoOrderWithIdException {
			return MockBeanProvider.mockOrderService();
		}
	}
	
	@Test
	public void whenCreateOrder_thenCorrectResponse() throws Exception {
		mvc.perform(
			post("/order")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(TestConstants.ORDER_DETAILS))
		)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId", is(TestConstants.MOCK_ORDER_REPOSITORY_ORDER_ID_TO_BE_SAVED.toString())));
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
		OrderEntityModel modelToUpdate = TestConstants.getSavedMockEntityModel();
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
		OrderEntityModel modelToGet = TestConstants.getSavedMockEntityModel();
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
		mvc.perform(
			delete("/order/{id}", TestConstants.getSavedMockEntityModel().getId().toString())
		)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId", is(TestConstants.getSavedMockEntityModel().getId().toString())));
	}
}
