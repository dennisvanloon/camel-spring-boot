package com.dvl.camelsimple;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.jms.Queue;
import javax.jms.TextMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(properties = "route.consume-message-from-amq.auto-startup=true")
@CamelSpringBootTest
@EnableAutoConfiguration(exclude = ActiveMQAutoConfiguration.class)
class ConsumeMessageFromActiveMQRouteTest {

    @Container
    public static GenericContainer<?> activeMqContainer = new GenericContainer<>("rmohr/activemq:latest")
            .withExposedPorts(61616);

    @TestConfiguration
    public static class ActiveMQConnectionFactoryOverride {
        @Bean
        public ActiveMQConnectionFactory activeMQConnectionFactory() {
            int port = activeMqContainer.getMappedPort(61616);
            return new ActiveMQConnectionFactory("tcp://localhost:" + port);
        }
    }

    private final String testMessage = "Hello World";

    @EndpointInject("mock:result")
    MockEndpoint mockEndpoint;

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    Queue queue;

    @Test
    void receivedAMessageOnActiveMQ() throws InterruptedException {
        jmsTemplate.send(queue, messageCreator -> {
            TextMessage message = messageCreator.createTextMessage();
            message.setText(testMessage);
            return message;
        });

        mockEndpoint.setExpectedCount(1);
        mockEndpoint.assertIsSatisfied();

        assertEquals(testMessage, mockEndpoint.getExchanges().get(0).getMessage().getBody());
    }

}
