package org.jms.samples;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by eranda on 3/14/15.
 */
public class QueueProducer {

    public static final String NAMING_FACTORY_INITIAL = "java.naming.factory.initial";
    public static final String PROVIDER_URL = "java.naming.provider.url";
    public static final String QUEUE_PREFIX = "queue.";
    private static final String connectionFac = "QueueConnectionFactory";
    private static final String queueName = "MyQueue";
    private static final String url = "tcp://localhost:61616";
    private static final String broker = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private ConnectionFactory connectionFactory;

    private Destination destination;

    private Context context;

    private String username;
    private String password;

    public QueueProducer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void classicPublish() throws NamingException, JMSException {
        Properties properties = new Properties();
        properties.put(NAMING_FACTORY_INITIAL, broker);
        properties.put(PROVIDER_URL, url);
        properties.put(QUEUE_PREFIX + queueName, queueName);

        context = new InitialContext(properties);

        destination = (Destination) context.lookup(queueName);

        connectionFactory = (ConnectionFactory) context.lookup(connectionFac);

        Connection connection;
        connection = connectionFactory.createConnection(username, password);
        Session session = null;
        MessageProducer producer = null;

        try {
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            producer = session.createProducer(null);

            for (int i = 0; i < 1; i++) {
                TextMessage message = session.createTextMessage("{\"JMS\" :\"Hello World!\"}");
                producer.send(destination, message);
            }
        } finally {
            if (producer != null)
                producer.close();
            if (session != null)
                session.close();
            connection.close();
        }
    }

}
