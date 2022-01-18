package com.firefly.orderManagement.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class OrderResponseDTO {
	String message;
	String orderId;
}
