package com.firefly.orderManagement.containers;

import org.flywaydb.core.Flyway;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabasePostgresqlDocker {
    public static PostgreSQLContainer postgreSQLContainer = null;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        	if (postgreSQLContainer == null) {
        		postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
        			      .withDatabaseName("integration-tests-db")
        			      .withUsername("sa")
        			      .withPassword("sa");
        		postgreSQLContainer.start();
        		 /*Flyway flyway = Flyway.configure().dataSource(
        				 postgreSQLContainer.getJdbcUrl(), 
        				 postgreSQLContainer.getUsername(), 
        				 postgreSQLContainer.getPassword()
        				 ).load();
        				 */
        		 //flyway.migrate();

        	}
        	
            TestPropertyValues.of(
              "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
              "spring.datasource.username=" + postgreSQLContainer.getUsername(),
              "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}