package com.firefly.orderManagement.repository.order;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntityModel, UUID> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value= "SELECT o FROM OrderEntityModel o WHERE o.id = :id")
	@QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "500")})
	Optional<OrderEntityModel> findByIdForUpdating(@Param("id") UUID id);
}
