package com.firefly;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@SpringBootApplication
@EnableAutoConfiguration
public class GatewayApiApplication {

	@Value("${route.uri.order-api}")
	String orderApiUri;
	
	@Value("${route.uri.reporting-api}")
	String reportingApiUri;
	
	public static void main(String[] args) {
		SpringApplication.run(GatewayApiApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		System.out.println("orderApiUri");
		System.out.println(orderApiUri);
		System.out.println("reportingApiUri");
		System.out.println(reportingApiUri);
		//@formatter:off
		// String uri = "http://httpbin.org:80";
		// String uri = "http://localhost:9080";
		return builder.routes()
				.route(r -> r.path("/order","/order/**").uri(orderApiUri))
				.route(r -> r.path("/report").uri(reportingApiUri))
				.build();
	}
}
