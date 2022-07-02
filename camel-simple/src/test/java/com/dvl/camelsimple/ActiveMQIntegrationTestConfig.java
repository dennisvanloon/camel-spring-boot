package com.dvl.camelsimple;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.camel.PropertyInject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;

import static com.dvl.camelsimple.routes.Constants.ENDPOINT_ACTIVE_MQ;

@Configuration
public class ActiveMQIntegrationTestConfig {

    @PropertyInject(value = ENDPOINT_ACTIVE_MQ)
    private String queueUri;

    @Bean
    public Queue queue(){
        String queueName = queueUri.substring(queueUri.lastIndexOf(":") + 1);
        return new ActiveMQQueue(queueName);
    }

    @Bean
    public JmsTemplate jmsTemplate(ActiveMQConnectionFactory activeMQConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(activeMQConnectionFactory);
        return jmsTemplate;
    }
}
