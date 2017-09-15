package com.datadoghq.agent.integration;

import static org.assertj.core.api.Assertions.assertThat;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class JMSInstrumentationTest {

  @ClassRule public static EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();
  private static Session session;
  private static ActiveMQQueue destination;

  @BeforeClass
  public static void start() throws JMSException {

    broker.start();
    final ActiveMQConnectionFactory connectionFactory = broker.createConnectionFactory();

    destination = new ActiveMQQueue("someQueue");
    final Connection connection = connectionFactory.createConnection();
    connection.start();
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  @Test
  public void test() throws Exception {

    final MessageProducer producer = session.createProducer(destination);
    final MessageConsumer consumer = session.createConsumer(destination);

    assertThat(producer.getClass().getSimpleName()).isEqualTo("TracingMessageProducer");
    assertThat(consumer.getClass().getSimpleName()).isEqualTo("TracingMessageConsumer");
  }
}
