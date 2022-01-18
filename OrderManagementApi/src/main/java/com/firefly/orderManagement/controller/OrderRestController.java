package com.firefly.orderManagement.controller;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.firefly.orderManagement.controller.dto.OrderCreatedResponseDTO;
import com.firefly.orderManagement.controller.dto.OrderDeletedResponseDTO;
import com.firefly.orderManagement.controller.dto.OrderResponseDTO;
import com.firefly.orderManagement.controller.dto.OrderUpdatedResposeDTO;
import com.firefly.orderManagement.controller.validation.AtLeastOneNotNullFieldValidation;
import com.firefly.orderManagement.controller.validation.NoNullFieldsValidation;
import com.firefly.orderManagement.exception.NoOrderWithIdException;
import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order Management Api")
@RestController
@Validated
public class OrderRestController {

	private OrderService orderService;
	
	@Autowired
    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

	@PostMapping(value= "/order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create order")
	public @ResponseBody OrderCreatedResponseDTO createOrder(
			@RequestBody
			@NoNullFieldsValidation
			@Parameter(description = "Order details")
			OrderDetails orderDetails
			) {
		
		UUID orderId = orderService.createOrder(orderDetails);
        return new OrderCreatedResponseDTO(orderId.toString());
    }
	
	@PutMapping(value= "/order/{id}", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update order")
	public @ResponseBody OrderUpdatedResposeDTO updateOrder(
			@PathVariable
			@NotNull
			@Parameter(description = "Order id")
			UUID id,
			@RequestBody
			@AtLeastOneNotNullFieldValidation
			@Parameter(description = "Order details")
			OrderDetails orderDetails
			) throws NoOrderWithIdException {
		orderService.updateOrderWithId(id, orderDetails);
        return new OrderUpdatedResposeDTO(id.toString());
    }
	
	@DeleteMapping(value= "/order/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete order")
	public @ResponseBody OrderResponseDTO deleteOrder(
			@PathVariable
			@NotNull
			@Parameter(description = "Order id")
			UUID id
			){
		orderService.deleteOrderWithId(id);
        return new OrderDeletedResponseDTO(id.toString());
    }
	
	@GetMapping(value= "/order/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get order details")
	public @ResponseBody OrderEntityModel getOrder(
			@PathVariable
			@NotNull
			@Parameter(description = "Order id")
			UUID id
			) throws NoOrderWithIdException{
		return orderService.getOrderWithId(id);
    }
}
