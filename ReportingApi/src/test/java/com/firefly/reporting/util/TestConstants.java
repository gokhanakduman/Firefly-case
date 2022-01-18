package com.firefly.reporting.util;

import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.reporting.domain.event.DomainEvent;
import com.firefly.reporting.domain.event.order.OrderDetails;
import com.firefly.reporting.repository.GeometryOrdersEntity;

public class TestConstants {
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static final OrderDetails KONYA_ORDER_DETAILS = new OrderDetails(50f, 32.629638671875, 37.27782559332976);
	public static final OrderDetails ANKARA_ORDER_DETAILS = new OrderDetails(50f, 33.18994140625, 39.587084981687495);
	public static final UUID ANKARA_ORDER_ID = UUID.fromString("86e63107-b33a-4d87-9002-240f8fe2b186");
	public static final UUID KONYA_ORDER_ID = UUID.fromString("35d48196-6e0b-49aa-aa72-7079a32b08fb");
	private static  DomainEvent ANKARA_ORDER_CREATED_EVENT = null;
	private static  DomainEvent KONYA_ORDER_CREATED_EVENT = null; 
	private static  DomainEvent ANKARA_ORDER_UPDATED_TO_KONYA = null; 
	private static  DomainEvent KONYA_ORDER_DELETED_EVENT = null;
	
	public static final UUID ANKARA_GEOMETRY_ID = UUID.fromString("aaeafc70-7cb5-4414-aa42-7d6e760fd065");
	public static final UUID KONYA_GEOMETRY_ID = UUID.fromString("71bb5f1a-f493-4704-8bbc-b2c401983ae7");
	
	public static DomainEvent GET_ANKARA_ORDER_CREATED_EVENT() throws JsonProcessingException {
		if (ANKARA_ORDER_CREATED_EVENT == null) {
			ANKARA_ORDER_CREATED_EVENT = DomainEvent.builder()
			.eventId(UUID.fromString("890f7aa7-988b-4d63-9d39-847d2fbb3695"))
			.eventName(Constants.ORDER_CREATED_EVENT_STRING)
			.eventTime(new Timestamp(System.currentTimeMillis()))
			.aggragateType("Order")
			.aggragateId(ANKARA_ORDER_ID)
			.aggragateVersion(0l)
			.payload(mapper.writeValueAsString(ANKARA_ORDER_DETAILS))
			.build();
		}
		return ANKARA_ORDER_CREATED_EVENT;
	}
			
	public static DomainEvent GET_KONYA_ORDER_CREATED_EVENT() throws JsonProcessingException { 
		if (KONYA_ORDER_CREATED_EVENT == null) {
			KONYA_ORDER_CREATED_EVENT = DomainEvent.builder()
			.eventId(UUID.fromString("dd0d5cb2-836a-44ab-9704-3b75baffcdcd"))
			.eventName(Constants.ORDER_CREATED_EVENT_STRING)
			.eventTime(new Timestamp(System.currentTimeMillis()))
			.aggragateType("Order")
			.aggragateId(KONYA_ORDER_ID)
			.aggragateVersion(0l)
			.payload(mapper.writeValueAsString(KONYA_ORDER_DETAILS))
			.build();
		}
		return KONYA_ORDER_CREATED_EVENT;
	}
	
	public static DomainEvent GET_ANKARA_ORDER_UPDATED_TO_KONYA() throws JsonProcessingException { 
		if (ANKARA_ORDER_UPDATED_TO_KONYA == null) {
			ANKARA_ORDER_UPDATED_TO_KONYA = DomainEvent.builder()
			.eventId(UUID.fromString("c30461a1-3750-4fbd-acf0-c748bc312366"))
			.eventName(Constants.ORDER_UPDATED_EVENT_STRING)
			.eventTime(new Timestamp(System.currentTimeMillis()))
			.aggragateType("Order")
			.aggragateId(ANKARA_ORDER_ID)
			.aggragateVersion(1l)
			.payload(mapper.writeValueAsString(KONYA_ORDER_DETAILS))
			.build();
		}
		return ANKARA_ORDER_UPDATED_TO_KONYA;
	}
			
	public static DomainEvent GET_KONYA_ORDER_DELETED_EVENT() { 		
		if (KONYA_ORDER_DELETED_EVENT == null) {
			KONYA_ORDER_DELETED_EVENT = DomainEvent.builder()
			.eventId(UUID.fromString("8b494d0e-7ad5-46c1-bdc4-bd7d1d7509ec"))
			.eventName(Constants.ORDER_DELETED_EVENT_STRING)
			.eventTime(new Timestamp(System.currentTimeMillis()))
			.aggragateType("Order")
			.aggragateId(KONYA_ORDER_ID)
			.aggragateVersion(0l)
			.payload(null)
			.build();
		}
		return KONYA_ORDER_DELETED_EVENT;
	}
	
	public static GeometryOrdersEntity ANKARA_GEOMETRY_ORDERS_ENTITY() throws JsonProcessingException {
		return new GeometryOrdersEntity(TestConstants.ANKARA_GEOMETRY_ID, TestConstants.GET_ANKARA_ORDER_CREATED_EVENT(), TestConstants.ANKARA_ORDER_DETAILS);
	}
	
	public static GeometryOrdersEntity KONYA_GEOMETRY_ORDERS_ENTITY() throws JsonProcessingException {
		return new GeometryOrdersEntity(TestConstants.KONYA_GEOMETRY_ID, TestConstants.GET_KONYA_ORDER_CREATED_EVENT(), TestConstants.KONYA_ORDER_DETAILS);
	}
}