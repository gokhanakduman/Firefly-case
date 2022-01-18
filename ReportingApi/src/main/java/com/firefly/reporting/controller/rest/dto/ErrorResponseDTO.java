package com.firefly.reporting.controller.rest.dto;

import java.time.LocalDateTime;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "Error Response Object", description = "ErrorResponseDTO")
public class ErrorResponseDTO {
	@Schema(description = "Error message")
	String message;
	@Schema(description = "Time of the error")
	String timestamp;
	@Schema(description = "Additional fields about error")
	Map<String, String> additionalFields;
	
	public ErrorResponseDTO(String message) {
		this.message = message;
		this.timestamp = LocalDateTime.now().toString();
	}
}
