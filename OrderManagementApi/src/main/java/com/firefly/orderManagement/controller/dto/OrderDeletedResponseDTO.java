package com.firefly.orderManagement.controller.dto;

public class OrderDeletedResponseDTO extends OrderResponseDTO{
	public OrderDeletedResponseDTO(String orderId) {
		this.orderId = orderId;
		this.message = "Order Deleted";
	}
}
