package com.firefly.orderManagement.domain.event.order;

import java.sql.Timestamp;
import java.util.UUID;

import com.firefly.orderManagement.util.Constants;

public class OrderDeletedEvent extends OrderEvent {
	
	private UUID orderId;
	public OrderDeletedEvent(UUID orderId) {
		this.orderId = orderId;
	}
	
	@Override
	public String getEventName() {
		return Constants.Event.ORDER_DELETED;
	}
	
	@Override
	public String getAggregateId() {
		return orderId.toString();
	}

	@Override
	public Long getAggregateVersion() {
		return 0L;
	}
}
