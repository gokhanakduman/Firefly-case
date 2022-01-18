package com.firefly.reporting.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.RabbitMQContainer;

public class RabbitMqDocker {
	
	public static RabbitMQContainer rabbitMqContainer = null;

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        	if (rabbitMqContainer == null) {
        		rabbitMqContainer = new RabbitMQContainer("rabbitmq:3.9.12");
        		rabbitMqContainer.start();
        	}
        	
            TestPropertyValues.of(
              "spring.rabbitmq.host=" + rabbitMqContainer.getHost()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
