package com.firefly.reporting.domain.event;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DomainEvent {
	private UUID 		eventId;
	private String 		eventName;
	private Timestamp 	eventTime;
	private String 		aggragateType;
	private UUID 		aggragateId;
	private Long 		aggragateVersion;
	private String 		payload;
}