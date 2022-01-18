package com.firefly.reporting.controller.rest;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.firefly.reporting.controller.rest.dto.GeometryOrderCount;
import com.firefly.reporting.controller.rest.dto.ReportDateIntervalDTO;
import com.firefly.reporting.service.ReportingService;
import com.firefly.reporting.util.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Reporting Api")
@RestController
public class ReportingRestController {
	
	ReportingService reportingService;
	
	@Autowired
	public ReportingRestController(ReportingService reportingService) {
		this.reportingService = reportingService;
	}
	
	@GetMapping(value= "/report", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get total report")
	public @ResponseBody List<GeometryOrderCount> getGeometryOrderList() {
		return reportingService.getGeometryOrderCountList();
    }
	
	@PostMapping(value= "/report", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get report for a specific time frame")
	public @ResponseBody List<GeometryOrderCount> getGeometryOrderList(
			//@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			//@Valid 
			@RequestBody
			@Parameter(description = "Start & end dates")
			ReportDateIntervalDTO reportDateIntervalDTO) {
		if(reportDateIntervalDTO.getEndDate().compareTo(reportDateIntervalDTO.getStartDate()) < 0) {
			throw new ConstraintViolationException(Constants.ERROR_MESSAGE_START_DATE_GREATER_THAN_END_DATE, null);
		}	
		return reportingService.getGeometryOrderCountList(reportDateIntervalDTO);
    }
}
