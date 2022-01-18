package com.firefly.orderManagement.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OrderCreatedResponseDTO extends OrderResponseDTO{
	public OrderCreatedResponseDTO(String orderId) {
		this.orderId = orderId;
		this.message = "Order Created";
	}
}
