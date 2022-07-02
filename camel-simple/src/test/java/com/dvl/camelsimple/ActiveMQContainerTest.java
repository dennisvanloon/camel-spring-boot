package com.dvl.camelsimple;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

@SpringBootTest
@Testcontainers
public class ActiveMQContainerTest {

    @Container
    private final GenericContainer<?> activeMqContainer = new GenericContainer<>("rmohr/activemq:latest")
            .withExposedPorts(8161, 61616);

    @Test
    public void validateActiveMqTestContainer() throws JMSException {
        int port = activeMqContainer.getMappedPort(61616);

        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:" + port);

        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("HELLO.WORLD");

        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        TextMessage producedMessage = session.createTextMessage("Hello World");
        producer.send(producedMessage);

        MessageConsumer consumer = session.createConsumer(destination);
        Message receivedMessage = consumer.receive(1000);

        Assertions.assertNotNull(receivedMessage);
        Assertions.assertTrue(receivedMessage instanceof TextMessage);
        Assertions.assertEquals("Hello World", ((TextMessage)receivedMessage).getText());

        session.close();
        connection.close();
    }
}
