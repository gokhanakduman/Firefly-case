package com.firefly.orderManagement.domain.event.order;

import java.sql.Timestamp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.orderManagement.domain.event.model.DomainEvent;
import com.firefly.orderManagement.repository.order.OrderEntityModel;

public abstract class OrderEvent implements DomainEvent{
	protected OrderEntityModel order;
	
	@Override
	public String getAggregateId() {
		return order.getId().toString();
	}

	@Override
	public Long getAggregateVersion() {
		return order.getVersion();
	}
	
	@Override
	public String getAggregateType() {
		return "Order";
	}

	@Override
	public String getPayload() throws Exception {
		if (order != null && order.getOrderDetails() != null) {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(order.getOrderDetails());			
		}
		return null;
	}	
	
	@Override
	public String getMessageId() {
		return null;
	}
}
