package com.firefly.orderManagement.domain.event.model;

import java.sql.Timestamp;

public interface DomainEvent {
	String getEventName();
	String getAggregateType();
	String getAggregateId();
	Long getAggregateVersion();
	String getMessageId();
	default Timestamp getEventTime(){
		return new Timestamp(System.currentTimeMillis());
	}
;
	String getPayload() throws Exception;
}
