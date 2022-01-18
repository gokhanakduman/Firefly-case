package com.firefly.reporting.controller.rest.dto;

import com.fasterxml.jackson.annotation.JsonGetter;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title="Respective order counts for Geometries", description = "GeometryOrderCount")
public interface GeometryOrderCount {
	@Schema(description = "Geometry id")
	@JsonGetter("geometryId")
	String getGeometryid();
	
	@Schema(description = "Geometry name")
	String getGeometryName();
	
	@Schema(description = "order count")
	int getOrderCount();
}
