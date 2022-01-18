package com.firefly.orderManagement.domain.event.order;

import java.sql.Timestamp;

import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.util.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OrderUpdatedEvent extends OrderEvent{
	
	public OrderUpdatedEvent(OrderEntityModel order) {
		this.order = order;
	}
	
	public String getEventName() {
		return Constants.Event.ORDER_UPDATED;
	}

	@Override
	public Timestamp getEventTime() {
		return order.getUpdatedAt();
	}
}
