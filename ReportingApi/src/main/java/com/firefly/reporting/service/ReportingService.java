package com.firefly.reporting.service;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.reporting.controller.rest.dto.GeometryOrderCount;
import com.firefly.reporting.controller.rest.dto.ReportDateIntervalDTO;
import com.firefly.reporting.domain.event.DomainEvent;
import com.firefly.reporting.domain.event.order.OrderDetails;
import com.firefly.reporting.repository.GeometryOrdersEntity;
import com.firefly.reporting.repository.GeometryRepository;

@Service
public class ReportingService {
	
	GeometryRepository geometryRepository;
	
	@Autowired
	public ReportingService(GeometryRepository geometryRepository) {
		this.geometryRepository = geometryRepository;
	}
	
	public void handleOrderCreatedEvent(DomainEvent event) {
		OrderDetails orderDetails = OrderDetails.fromDomainEvent(event);
		if (orderDetails != null) {
			UUID geometryId = getGeometryId(orderDetails.getLatitude(), orderDetails.getLongitude());
			if (geometryId != null) {
				GeometryOrdersEntity entity = new GeometryOrdersEntity(geometryId, event, orderDetails);
				geometryRepository.save(entity);
			}
		}
	}
	
	private UUID getGeometryId(Double latitude, Double longitude) {
		UUID geometryId = null;
		String geometryIdString = geometryRepository.getGeometryIdFromLatLong(latitude, longitude);
		if (geometryIdString != null) {
			geometryId = UUID.fromString(geometryIdString);
		}
		return geometryId;
	}

	@Transactional
	public void handleOrderUpdatedEvent(DomainEvent event) {
		OrderDetails orderDetails = OrderDetails.fromDomainEvent(event);
		if (orderDetails != null) {
			UUID geometryId = getGeometryId(orderDetails.getLatitude(),  orderDetails.getLongitude());			
			if (geometryId != null) {
				geometryRepository.updateOrder(geometryId, event.getAggragateVersion(), event.getAggragateId(),
						orderDetails.getLatitude(), orderDetails.getLongitude());				
			}
		}
	}
	
	public void handleOrderDeletedEvent(DomainEvent event) {
		geometryRepository.deleteById(event.getAggragateId());
	}
	
	public List<GeometryOrderCount> getGeometryOrderCountList() {
		return geometryRepository.getGeometryOrderCountList();
	}
	
	public List<GeometryOrderCount> getGeometryOrderCountList(ReportDateIntervalDTO reportDateIntervalDTO) {
		return geometryRepository.getGeometryOrderCountListBetweenDates(reportDateIntervalDTO.getStartDate(), reportDateIntervalDTO.getEndDate());
	}
	
}
