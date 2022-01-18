package com.firefly.reporting.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.firefly.reporting.controller.rest.dto.GeometryOrderCount;
import com.firefly.reporting.repository.GeometryRepository;

public class TestUtil {
	public static int getOrderCountForGeometry(GeometryRepository geometryRepository, UUID geometryId) {
		List<GeometryOrderCount> orderCountList = geometryRepository.getGeometryOrderCountList();
		GeometryOrderCount geometryOrderCount = orderCountList.stream().filter(o -> o.getGeometryid().equals(geometryId.toString())).findFirst().get();
		return geometryOrderCount.getOrderCount();
	}
	
	public static int getOrderCountForGeometryBetweenDates(GeometryRepository geometryRepository,UUID geometryId, Timestamp startDate, Timestamp endDate) {
		List<GeometryOrderCount> orderCountList = geometryRepository.getGeometryOrderCountListBetweenDates(startDate, endDate);
		GeometryOrderCount geometryOrderCount = orderCountList.stream().filter(o -> o.getGeometryid().equals(geometryId.toString())).findFirst().get();
		return geometryOrderCount.getOrderCount();
	}
}
