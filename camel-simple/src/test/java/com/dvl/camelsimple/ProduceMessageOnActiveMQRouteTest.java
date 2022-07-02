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

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;

@SpringBootTest(properties = "route.produce-message-on-amq.auto-startup=true")
@CamelSpringBootTest
@EnableAutoConfiguration(exclude = ActiveMQAutoConfiguration.class)
class ProduceMessageOnActiveMQRouteTest {

    @Produce(uri = "seda:trigger")
    ProducerTemplate producerTemplate;

    @Autowired
    ActiveMQConnectionFactory activeMQConnectionFactory;

    @Autowired
    Queue queue;

    @Test
    void receivedAMessageOnActiveMQ() throws JMSException {
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(queue);

        producerTemplate.sendBody("");

        Awaitility.with().pollInterval(fibonacci(TimeUnit.SECONDS))
                .await()
                .atMost(Duration.of(10, ChronoUnit.SECONDS))
                .until(() -> consumer.receiveNoWait() != null);
    }
}
