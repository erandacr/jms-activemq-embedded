package org.jms.samples;

public class App {
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";

    public static void main(String[] args) throws Exception {
        ActiveMQBroker activeMQBroker = null;
        QueueConsumer queueConsumer = null;
        QueueProducer queueProducer = null;
        Thread t = null;

        try {

            activeMQBroker = new ActiveMQBroker("tcp://localhost:61616");
            activeMQBroker.startBroker();

            // Start consumer
            queueConsumer = new QueueConsumer(USERNAME, PASSWORD);
            t = new Thread(queueConsumer);
            t.start();

            // Publish messages
            queueProducer = new QueueProducer(USERNAME, PASSWORD);
            queueProducer.classicPublish();

        } catch (Exception e) {
            throw new Exception("Error starting the broker", e);
        } finally {
            if (queueConsumer != null && t != null) {
                queueConsumer.stopConsumer();
                t.join();
            }
            activeMQBroker.stopBroker();
        }
    }
}
