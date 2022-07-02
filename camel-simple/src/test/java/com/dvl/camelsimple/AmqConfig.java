package com.dvl.camelsimple;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.camel.PropertyInject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;

@Configuration
public class AmqConfig {

    @PropertyInject(value = "route.produce-message-on-amq.to")
    private String queueUri;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

    @Bean
    public Queue createQueue(){
        String queueName = queueUri.substring(queueUri.lastIndexOf(":") + 1);
        return new ActiveMQQueue(queueName);
    }
}
