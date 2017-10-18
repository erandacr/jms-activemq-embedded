package org.jms.samples;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.security.JaasAuthenticationPlugin;

/**
 * A simple jms server class using Activemq embedded with authentication support
 */
public class ActiveMQBroker {

    private BrokerService broker;

    public ActiveMQBroker(String url) throws Exception {
        System.setProperty("java.security.auth.login.config",
                getClass().getClassLoader().getResource("conf/login.config").getPath());

        broker = new BrokerService();
        broker.setBrokerName("activemq-jms-localhost");
        broker.addConnector(url);
        broker.setPlugins(new BrokerPlugin[] { new JaasAuthenticationPlugin() });
    }

    public void startBroker() throws Exception {
        broker.start();
    }

    public void stopBroker() throws Exception {
        broker.stop();
    }
}
