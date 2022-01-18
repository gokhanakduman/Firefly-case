package com.firefly.reporting.repository;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.firefly.reporting.domain.event.DomainEvent;
import com.firefly.reporting.domain.event.order.OrderDetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "geometry_orders")
public class GeometryOrdersEntity {
	@Id
	@Column(name="order_id")
	private UUID orderId;
	
	@Column(name="geometry_id")
	private UUID geometryId;
	
	@Column
	private Double latitude;
	
	@Column
	private Double longitude;
	
	@Column
	private Timestamp orderTime;
	
	@Column(name="order_version")
	private Long orderVersion;
	
	public GeometryOrdersEntity(UUID geometryId, DomainEvent event, OrderDetails orderDetails) {
		this.orderId = event.getAggragateId();
		this.geometryId = geometryId;
		this.latitude = orderDetails.getLatitude();
		this.longitude = orderDetails.getLongitude();
		this.orderTime = event.getEventTime();
		this.orderVersion = event.getAggragateVersion();
	}
}
