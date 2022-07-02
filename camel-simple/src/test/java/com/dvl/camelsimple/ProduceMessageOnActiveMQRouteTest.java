package com.dvl.camelsimple;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;

@Testcontainers
@SpringBootTest(properties = "route.produce-message-on-amq.auto-startup=true")
@CamelSpringBootTest
@EnableAutoConfiguration(exclude = ActiveMQAutoConfiguration.class)
class ProduceMessageOnActiveMQRouteTest {

    @Container
    public static GenericContainer<?> activeMqContainer = new GenericContainer<>("rmohr/activemq:latest")
            .withExposedPorts(61616);

    @Produce(value = "seda:trigger")
    ProducerTemplate producerTemplate;

    @TestConfiguration
    public static class ActiveMQConnectionFactoryOverride {
        @Bean
        public ActiveMQConnectionFactory activeMQConnectionFactory() {
            int port = activeMqContainer.getMappedPort(61616);
            return new ActiveMQConnectionFactory("tcp://localhost:" + port);
        }
    }

    @Autowired
    ActiveMQConnectionFactory activeMQConnectionFactory;

    @Autowired
    Queue queue;

    private MessageConsumer consumer;

    @PostConstruct
    public void postConstruct() throws JMSException {
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        consumer = session.createConsumer(queue);
    }

    @Test
    void receivedAMessageOnActiveMQ() {
        Date date = new Date();
        producerTemplate.sendBodyAndHeader("", "firedTime", date);

        Awaitility.await()
                .with().pollInterval(fibonacci(TimeUnit.SECONDS))
                .atMost(Duration.of(10, ChronoUnit.SECONDS))
                .until(() -> messageFound(date));
    }

    private boolean messageFound(Date date) throws JMSException {
        Message message = consumer.receiveNoWait();
        return message instanceof TextMessage && ((TextMessage)message).getText().equals("Current time is " + date);
    }
}
