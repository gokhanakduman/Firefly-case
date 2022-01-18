package com.firefly.reporting.integration.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firefly.reporting.containers.*;
import com.firefly.reporting.repository.GeometryOrdersEntity;
import com.firefly.reporting.repository.GeometryRepository;
import com.firefly.reporting.util.TestConstants;
import com.firefly.reporting.util.TestUtil;

@DataJpaTest
@ContextConfiguration(initializers = { DatabasePostgresqlDocker.Initializer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GeometryRepositoryIntegrationTest {
	@Resource
	GeometryRepository geometryRepository;
	
	@BeforeEach
	public void clearTables() {
		geometryRepository.deleteAll();
	}
	
	@Test
	public void whenGetGeoIdFromLatLng_thenSuccess() {
		 assertEquals(TestConstants.ANKARA_GEOMETRY_ID.toString(), 
				 geometryRepository.getGeometryIdFromLatLong(TestConstants.ANKARA_ORDER_DETAILS.getLatitude(), 
						 TestConstants.ANKARA_ORDER_DETAILS.getLongitude()));
		 
		 assertEquals(TestConstants.KONYA_GEOMETRY_ID.toString(), 
				 geometryRepository.getGeometryIdFromLatLong(TestConstants.KONYA_ORDER_DETAILS.getLatitude(), 
						 TestConstants.KONYA_ORDER_DETAILS.getLongitude()));
	}
	
	@Test
	public void whenGetGeometryOrderCountList_thenSuccess() throws JsonProcessingException {
		saveAnkaraOrder();
		assertEquals(1, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID));
		assertEquals(0, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.KONYA_GEOMETRY_ID));
		
		saveKonyaOrder();
		assertEquals(1, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID));
		assertEquals(1, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.KONYA_GEOMETRY_ID));
	}
	
	@Test
	public void whenGetGeometryOrderCountListBetweenDates_thenSuccess() throws JsonProcessingException {
		saveAnkaraOrder();
		GeometryOrdersEntity entity = geometryRepository.findById(TestConstants.ANKARA_ORDER_ID).get();
		Timestamp beforeAnkaraOrderDate1 = new Timestamp(entity.getOrderTime().getTime() - 2000 );
		Timestamp beforeAnkaraOrderDate2 = new Timestamp(entity.getOrderTime().getTime() - 1000 );
		Timestamp afterAnkaraOrderDate = new Timestamp(entity.getOrderTime().getTime() + 1000 );
		
		assertEquals(1, TestUtil.getOrderCountForGeometryBetweenDates(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID, beforeAnkaraOrderDate1, afterAnkaraOrderDate));
		assertEquals(0, TestUtil.getOrderCountForGeometryBetweenDates(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID, beforeAnkaraOrderDate1, beforeAnkaraOrderDate2));
	}
	
	@Test
	public void whenSaveGeometryOrder_thenSuccessAndCountIncrease() throws JsonProcessingException {
		saveAnkaraOrder();
		Optional<GeometryOrdersEntity> savedEntityOptional = geometryRepository.findById(TestConstants.ANKARA_ORDER_ID);
		assertTrue(savedEntityOptional.isPresent());
		assertEquals(1, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID));		
	}
	
	@Test
	public void whenUpdateGeometryOrder_thenSuccessAndCountChange() throws JsonProcessingException {
		saveAnkaraOrder();
		saveKonyaOrder();
		// Update Ankara order to Konya location
		geometryRepository.updateOrder(TestConstants.KONYA_GEOMETRY_ID, 2l, TestConstants.ANKARA_ORDER_ID
				, TestConstants.KONYA_ORDER_DETAILS.getLatitude(), TestConstants.KONYA_ORDER_DETAILS.getLongitude());
		
		assertEquals(0, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID));
		assertEquals(2, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.KONYA_GEOMETRY_ID));
	}
	
	@Test
	public void whenDeleteOrder_thenSuccess() throws JsonProcessingException {
		saveAnkaraOrder();
		saveKonyaOrder();
		
		geometryRepository.deleteById(TestConstants.ANKARA_ORDER_ID);
		assertEquals(0, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.ANKARA_GEOMETRY_ID));
		assertEquals(1, TestUtil.getOrderCountForGeometry(geometryRepository, TestConstants.KONYA_GEOMETRY_ID));
	}
	
	
	
	private void saveAnkaraOrder() throws JsonProcessingException {
		geometryRepository.save(TestConstants.ANKARA_GEOMETRY_ORDERS_ENTITY());
	}
	
	private void saveKonyaOrder() throws JsonProcessingException {
		geometryRepository.save(TestConstants.KONYA_GEOMETRY_ORDERS_ENTITY());
	}
	
	
}
