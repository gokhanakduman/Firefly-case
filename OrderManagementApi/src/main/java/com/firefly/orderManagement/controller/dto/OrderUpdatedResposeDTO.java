package com.firefly.orderManagement.controller.dto;

public class OrderUpdatedResposeDTO extends OrderResponseDTO{
	public OrderUpdatedResposeDTO(String orderId) {
		this.orderId = orderId;
		this.message = "Order Updated";
	}
}
