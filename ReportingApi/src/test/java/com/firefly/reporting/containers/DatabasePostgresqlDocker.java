package com.firefly.reporting.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgisContainerProvider;

public class DatabasePostgresqlDocker {
    public static JdbcDatabaseContainer postgreSQLContainer = null;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        	
        	if (postgreSQLContainer == null) {
        		postgreSQLContainer = new PostgisContainerProvider().newInstance()
        			      .withDatabaseName("integration-tests-db")
        			      .withUsername("sa")
        			      .withPassword("sa");
        		postgreSQLContainer.start();
        	}
        	
            TestPropertyValues.of(
              "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
              "spring.datasource.username=" + postgreSQLContainer.getUsername(),
              "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}