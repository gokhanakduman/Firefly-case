package com.firefly.reporting.domain.event.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.reporting.domain.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDetails {
	private Float price;
	private Double latitude;
	private Double longitude;
	
	public static OrderDetails fromDomainEvent(DomainEvent event) {
		if (event != null && event.getPayload() != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return mapper.readValue(event.getPayload(), OrderDetails.class);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
}
