package com.firefly.reporting.integration.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firefly.reporting.containers.*;
import com.firefly.reporting.repository.GeometryOrdersEntity;
import com.firefly.reporting.repository.GeometryRepository;
import com.firefly.reporting.service.ReportingService;
import com.firefly.reporting.util.TestConstants;
import com.firefly.reporting.util.TestUtil;


@SpringBootTest
@ContextConfiguration(initializers = {DatabasePostgresqlDocker.Initializer.class, RabbitMqDocker.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReportingServiceIntegrationTest {
	@Autowired
	ReportingService reportingService;
	
	@Autowired 
	GeometryRepository geometryRepository;
	
	@BeforeEach
	public void clearDb() {
		geometryRepository.deleteAll();
	}
	
	@Test
	public void whenOrderCreateEvent_thenSuccess() throws JsonProcessingException {
		reportingService.handleOrderCreatedEvent(TestConstants.GET_ANKARA_ORDER_CREATED_EVENT());
		Optional<GeometryOrdersEntity> entityOptional = geometryRepository.findById(TestConstants.ANKARA_ORDER_ID);
		assertTrue(entityOptional.isPresent());
		assertEquals(entityOptional.get().getGeometryId(), TestConstants.ANKARA_GEOMETRY_ID);		
	}
	
	@Test
	public void whenOrderUpdateEvent_thenSuccess() throws JsonProcessingException {
		reportingService.handleOrderCreatedEvent(TestConstants.GET_ANKARA_ORDER_CREATED_EVENT());
		reportingService.handleOrderUpdatedEvent(TestConstants.GET_ANKARA_ORDER_UPDATED_TO_KONYA());
		Optional<GeometryOrdersEntity> entityOptional = geometryRepository.findById(TestConstants.ANKARA_ORDER_ID);
		assertTrue(entityOptional.isPresent());
		assertEquals(entityOptional.get().getGeometryId(), TestConstants.KONYA_GEOMETRY_ID);		
	}
	
	@Test
	public void whenOrderDeletedEvent_thenSuccess() throws JsonProcessingException {
		reportingService.handleOrderCreatedEvent(TestConstants.GET_KONYA_ORDER_CREATED_EVENT());
		assertTrue(geometryRepository.findById(TestConstants.KONYA_ORDER_ID).isPresent());
		reportingService.handleOrderDeletedEvent(TestConstants.GET_KONYA_ORDER_DELETED_EVENT());
		assertTrue(geometryRepository.findById(TestConstants.KONYA_ORDER_ID).isEmpty());
	}
	
	@Test
	public void whenGetGeometryOrderCountList_thenSuccess() throws JsonProcessingException {
		reportingService.handleOrderCreatedEvent(TestConstants.GET_ANKARA_ORDER_CREATED_EVENT());
		reportingService.handleOrderCreatedEvent(TestConstants.GET_KONYA_ORDER_CREATED_EVENT());
		
		assertEquals(1, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID));
		assertEquals(1, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.KONYA_GEOMETRY_ID));
		
		reportingService.handleOrderUpdatedEvent(TestConstants.GET_ANKARA_ORDER_UPDATED_TO_KONYA());
		assertEquals(0, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID));
		assertEquals(2, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.KONYA_GEOMETRY_ID));
	}
	
	@Test
	public void whenGetGeometryOrderCountListWithDates_thenSuccess() throws JsonProcessingException {
		reportingService.handleOrderCreatedEvent(TestConstants.GET_ANKARA_ORDER_CREATED_EVENT());
		
		GeometryOrdersEntity entity = geometryRepository.findById(TestConstants.ANKARA_ORDER_ID).get();
		Timestamp beforeAnkaraOrderDate1 = new Timestamp(entity.getOrderTime().getTime() - 2000 );
		Timestamp beforeAnkaraOrderDate2 = new Timestamp(entity.getOrderTime().getTime() - 1000 );
		Timestamp afterAnkaraOrderDate = new Timestamp(entity.getOrderTime().getTime() + 1000 );
		
		assertEquals(1, TestUtil.getOrderCountForGeometryBetweenDates(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID, beforeAnkaraOrderDate1, afterAnkaraOrderDate));
		assertEquals(0, TestUtil.getOrderCountForGeometryBetweenDates(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID, beforeAnkaraOrderDate1, beforeAnkaraOrderDate2));
	}
	
}
