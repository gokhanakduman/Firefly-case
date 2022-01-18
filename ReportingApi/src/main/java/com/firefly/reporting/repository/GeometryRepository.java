package com.firefly.reporting.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.firefly.reporting.controller.rest.dto.GeometryOrderCount;

@Repository
public interface GeometryRepository extends CrudRepository<GeometryOrdersEntity, UUID>{
	@Query(value= "SELECT Cast(id as varchar) FROM geometries g where ST_WITHIN(ST_SetSRID(ST_Point(:latitude,:longitude),4326), g.geometry_polygon)", nativeQuery = true)
	public String getGeometryIdFromLatLong(@Param("latitude") Double latitude, @Param("longitude") Double longitude);
	
	@Query(value= "select Cast(g.id as varchar) as geometryid, g.geometry_name as geometryName, goc.order_count as orderCount "
			+ "from geometry_order_counts goc , geometries g where g.id = goc.geometry_id ", nativeQuery = true)
	public List<GeometryOrderCount> getGeometryOrderCountList();
	
	@Query(value= "select Cast(g.id as varchar) as geometryId, g.geometry_name as geometryName, CASE WHEN goc.order_count IS NULL THEN 0 ELSE goc.order_count END as orderCount from geometries g left outer join (\n"
			+ "	select count(*) as order_count, geometry_id from geometry_orders where order_time <= :endDate and order_time >= :startDate group by geometry_id \n"
			+ ") as goc on g.id = goc.geometry_id", nativeQuery = true)
	public List<GeometryOrderCount> getGeometryOrderCountListBetweenDates(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
	
	@Modifying
	@Query(value= "UPDATE GeometryOrdersEntity SET geometryId= :geometryId, orderVersion = :orderVersion, latitude = :latitude, longitude = :longitude "
			+ "WHERE orderVersion < :orderVersion and orderId= :orderId")
	public int updateOrder(@Param("geometryId") UUID geometryId, @Param("orderVersion") Long orderVersion, 
			@Param("orderId") UUID orderId, @Param("latitude") Double latitude, @Param("longitude") Double longitude);
}
