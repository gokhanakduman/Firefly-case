package com.firefly.orderManagement.util;

public class Constants {
	public static final String ERROR_MESSAGE_REQUEST_PARAMETERS_NOT_VALID = "Request parameters are not valid.";
	public static final String ERROR_MESSAGE_UNKNOWN_ERROR_OCCURED = "An unknown error occured.";
	public static final String ERROR_MESSAGE_REQUESTED_ORDER_NOT_FOUND = "No such order found.";
	
	public static class Event {
		public static final String ORDER_CREATED = "OrderCreated";
		public static final String ORDER_UPDATED = "OrderUpdated";
		public static final String ORDER_DELETED = "OrderDeleted";
	}
}
