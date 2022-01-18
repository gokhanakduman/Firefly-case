package com.firefly.reporting.controller.rest.dto;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title="Start end date DTO", description = "ReportDateIntervalDTO")
public class ReportDateIntervalDTO {
	@NotNull
	@Schema(description = "Start date. Format: yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Timestamp startDate;
	
	@NotNull
	@Schema(description = "End date. Format: yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Timestamp endDate;
}
