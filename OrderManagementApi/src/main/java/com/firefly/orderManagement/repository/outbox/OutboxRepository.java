package com.firefly.orderManagement.repository.outbox;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends CrudRepository<DomainEventEntityModel, UUID> {
	List<DomainEventEntityModel> findTop10ByOrderByEventTimeDesc();
}
