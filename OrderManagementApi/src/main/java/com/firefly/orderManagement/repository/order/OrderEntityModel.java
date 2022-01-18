package com.firefly.orderManagement.repository.order;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firefly.orderManagement.domain.event.model.DomainEvent;
import com.firefly.orderManagement.domain.event.model.ResultWithEvents;
import com.firefly.orderManagement.domain.event.order.OrderCreatedEvent;
import com.firefly.orderManagement.domain.event.order.OrderUpdatedEvent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Order Model", description = "OrderEntityModel")
public class OrderEntityModel {
	@Id
	@GeneratedValue
	@Schema(description = "Order id")
	private UUID id;
	
	@Embedded
	@Schema(description = "Order details")
	OrderDetails orderDetails;
	
	@Column(name="created_at")
	@CreationTimestamp
	@Schema(description = "Order creation time")
	private Timestamp createdAt;
	
	@Column(name="updated_at")
	@Schema(description = "Order update time")
	@UpdateTimestamp
	private Timestamp updatedAt;
	
	@Version
	@JsonIgnore
	private Long version;
	
	public OrderEntityModel(OrderDetails orderDetails) {
		this.orderDetails = orderDetails; 
	}
	
	public static ResultWithEvents<OrderEntityModel> createOrder(OrderDetails orderDetails) {
		OrderEntityModel order = new OrderEntityModel(orderDetails);
	    OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(order);
	    return new ResultWithEvents<>(order, List.of(orderCreatedEvent));
	  }
	
	public ResultWithEvents<OrderEntityModel> editOrder(OrderDetails orderDetails) {
		boolean willSendUpdatedEvent = false;
		if ( orderDetails.getLatitude() != null && 
				!this.orderDetails.getLatitude().equals(orderDetails.getLatitude())) {
			willSendUpdatedEvent = true;
			this.orderDetails.setLatitude(orderDetails.getLatitude());
		}
		
		if ( orderDetails.getLongitude() != null && 
				!this.orderDetails.getLongitude().equals(orderDetails.getLongitude()) ) {
			willSendUpdatedEvent = true;
			this.orderDetails.setLongitude(orderDetails.getLongitude());
		}

		if (orderDetails.getPaymentType() != null) {
			this.orderDetails.setPaymentType(orderDetails.getPaymentType());
		}
		
		if (orderDetails.getPrice() != null) {
			this.orderDetails.setPrice(orderDetails.getPrice());
		}
		List<DomainEvent> events = new ArrayList<>();
		if (willSendUpdatedEvent) {
			events.add(new OrderUpdatedEvent(this));
		}
		return new ResultWithEvents<>(this, events);
		
	}
}
