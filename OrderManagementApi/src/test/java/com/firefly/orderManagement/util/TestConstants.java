package com.firefly.orderManagement.util;

import java.util.UUID;

import com.firefly.orderManagement.repository.order.OrderDetails;
import com.firefly.orderManagement.repository.order.OrderEntityModel;
import com.firefly.orderManagement.repository.order.PaymentType;

public class TestConstants {
	public static final UUID MOCK_ORDER_REPOSITORY_ORDER_ID_TO_BE_SAVED = UUID.fromString("31e21b17-ba6e-4d91-96d6-79d1085b2011");
	public static final UUID MOCK_ORDER_REPOSITORY_NON_EXISTING_ID 		= UUID.fromString("dc9a6fb7-3e69-445f-b33c-79397128b10f");
	
	private static OrderEntityModel savedMockEntityModel = null;
	public static final OrderDetails ORDER_DETAILS = new OrderDetails(35f, PaymentType.Check, 27.091298828125, 38.139438891821746);
	public static OrderEntityModel getSavedMockEntityModel() {
		if (savedMockEntityModel == null) {
			savedMockEntityModel = new OrderEntityModel(ORDER_DETAILS);
			savedMockEntityModel.setId(UUID.fromString("eeafe264-8045-4cfb-a9e3-b2ec367ddf22"));
		}
		return savedMockEntityModel;
	}
}