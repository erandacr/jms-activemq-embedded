package org.jms.samples;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class QueueConsumer implements Runnable {
    private static final String DEFAULT_CONNECTION_FACTORY = "QueueConnectionFactory";
    private static final String DEFAULT_DESTINATION = "MyQueue";
    private static final String INITIAL_CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    private static final String PROVIDER_URL = "tcp://localhost:61616";

    private String username;
    private String password;

    private boolean stateStoppped = false;

    public QueueConsumer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void startConsumer() throws Exception {
        Connection connection = null;
        Context initialContext = null;
        try {
            // Step 1. Create an initial context to perform the JNDI lookup.
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put("java.naming.provider.url", PROVIDER_URL);
            env.put("queue." + DEFAULT_DESTINATION, DEFAULT_DESTINATION);
            initialContext = new InitialContext(env);

            // Step 2. perform a lookup on the Queue
            Queue queue = (Queue) initialContext.lookup(DEFAULT_DESTINATION);

            // Step 3. perform a lookup on the Connection Factory
            ConnectionFactory cf = (ConnectionFactory) initialContext.lookup(DEFAULT_CONNECTION_FACTORY);

            // Step 4. Create a JMS Connection
            connection = cf.createConnection(username, password);

            // Step 5. Create a JMS Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Step 6. Create a JMS Message Consumer
            MessageConsumer messageConsumer = session.createConsumer(queue);

            // Step 7. Start the Connection
            connection.start();
            System.out.println("JMS consumer stated on the queue " + DEFAULT_DESTINATION + "\n");

            //Clear the queue, if there is any previous messages in the queue
            TextMessage tempMessage;

            int i = 0;
            do {
                tempMessage = (TextMessage) messageConsumer.receive(100);
                i++;
                if (tempMessage != null) {
                    System.out.println("Message " + i + " : " + tempMessage.getText());
                }
            } while (!stateStoppped);

        } finally {
            // Step 9. Close JMS resources
            if (connection != null) {
                connection.close();
            }

            // Also the initialContext
            if (initialContext != null) {
                initialContext.close();
            }
        }
    }

    public void run() {
        try {
            startConsumer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopConsumer() {
        this.stateStoppped = true;
    }
}
