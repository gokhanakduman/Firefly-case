package com.firefly.reporting.controller.event;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;

import com.firefly.reporting.configuration.RabbitMQConfig;
import com.firefly.reporting.domain.event.DomainEvent;
import com.rabbitmq.client.Channel;

public class RabbitEventListener {

	@Autowired
	OrderEventConsumer orderEventConsumer;
	
	@RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE_NAME)
    public void onMessageReceived(DomainEvent domainEvent, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try{
	        if (domainEvent != null) {
	        	orderEventConsumer.handle(domainEvent);
			}
            channel.basicAck(tag, false);   
        }catch (Exception e) {
        	e.printStackTrace();
            channel.basicNack(tag, false, true);
        }
    }
}
