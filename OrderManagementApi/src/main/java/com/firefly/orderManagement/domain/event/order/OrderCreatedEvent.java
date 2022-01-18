package com.firefly.orderManagement.domain.event.order;

import java.sql.Timestamp;

import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.util.Constants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderCreatedEvent extends OrderEvent {

	public OrderCreatedEvent(OrderEntityModel order) {
		this.order = order;
	}
	
	@Override
	public String getEventName() {
		return Constants.Event.ORDER_CREATED;
	}

	@Override
	public Timestamp getEventTime() {
		return order.getCreatedAt();
	}
}
