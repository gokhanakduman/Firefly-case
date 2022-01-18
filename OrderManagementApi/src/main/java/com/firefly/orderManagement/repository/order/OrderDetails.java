package com.firefly.orderManagement.repository.order;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
@Builder(toBuilder = true)
@Schema(title = "Order Details", description = "OrderDetails")
public class OrderDetails {
	@Schema(description = "Order price")
	@Column(nullable = false)
	@DecimalMin(value="0")
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Float price;
	
	@Schema(description = "Order payment type")
	@Enumerated(EnumType.STRING)
	@Column(name="payment_type", nullable = false)
	@GeneratedValue(strategy = GenerationType.TABLE)
	private PaymentType paymentType;
	
	@Schema(description = "Order latitude")
	@Column(nullable = false)
	@DecimalMin(value = "-90")
	@DecimalMax(value = "90")
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Double latitude;
	
	@Schema(description = "Order longitude")
	@Column(nullable = false)
	@DecimalMin(value = "-180")
	@DecimalMax(value = "180")
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Double longitude;
	
	@JsonIgnore
	public OrderDetails copy() {
		return this.toBuilder().build();
	}
}
