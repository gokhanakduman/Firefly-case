package com.firefly.orderManagement.repository.outbox;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firefly.orderManagement.domain.event.model.DomainEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "outbox")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DomainEventEntityModel implements DomainEvent{
	@Id
	@GeneratedValue
	@Column(name="event_id")
	private UUID eventId;
	
	@Column(name="event_name")
	private String eventName;
	
	@Column(name="event_time")
	//@CreationTimestamp
	private Timestamp eventTime;
	
	@Column(name="aggragate_type", nullable = false)
	private String aggragateType;
	
	@Column(name="aggragate_id")
	private UUID aggragateId;
	
	@Column(name="aggragate_version", nullable = false)
	private Long aggragateVersion;
	
	@Column
	private String payload;

	public DomainEventEntityModel(DomainEvent domainEvent) throws Exception {
		if(domainEvent.getAggregateId() != null) {
			this.aggragateId = UUID.fromString(domainEvent.getAggregateId());
		}
		this.eventTime = domainEvent.getEventTime();
		this.aggragateType = domainEvent.getAggregateType();
		this.eventName = domainEvent.getEventName();
		this.aggragateVersion = domainEvent.getAggregateVersion();
		this.payload = domainEvent.getPayload();
	}
	
	@Override
	public String getEventName() {
		return eventName.toString();
	}

	@Override
	public String getAggregateType() {
		return aggragateType;
	}

	@Override
	public String getAggregateId() {
		return aggragateId.toString();
	}

	@Override
	public Long getAggregateVersion() {
		return aggragateVersion;
	}

	@Override
	public String getPayload() throws Exception {
		return payload;
	}

	@Override
	public String getMessageId() {
		return eventId.toString();
	}
}
