package ru.syntez.camel.artemis.config;

import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import ru.syntez.camel.artemis.component.CamelConsumer;
import ru.syntez.camel.artemis.component.FromOutputToBeanBuilder;
import ru.syntez.camel.artemis.component.InputToOutputRouteBuilder;

import javax.jms.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for camel jms
 * 1. Created pooled ConnectionFactory
 * 2. Created camel context with ConnectionFactory and custom routeBuilder
 *
 * @author Skyhunter
 * @date 28.01.2021
 */

@Configuration
public class JmsConfig {

    @Value("${camel.activemq.brokerUrl}")
    private String brokerConnector = "tcp://localhost:61616";

    @Value("${camel.activemq.user}")
    private String brokerUser = "user";

    @Value("${camel.activemq.password}")
    private String brokerPass = "user";

    @Value("${camel.activemq.concurrentConsumers}")
    private Integer concurrentConsumers = 10;

    @Value("${server.activemq.queues.input-output-endpoint}")
    private String queueInputOutputEndpoint = "inputToOutputQueue";

    @Value("${server.activemq.queues.output-endpoint}")
    private String queueOutputEndpoint = "outputQueue";

    @Value("${server.activemq.queues.input-output-bean-endpoint}")
    private String queueInputOutputBeanEndpoint = "inputToOutputBeanQueue";

    @Value("${server.activemq.queues.output-bean-endpoint}")
    private String queueOutputBeanEndpoint = "outputBeanQueue";

    @Value("${server.work-time}")
    private Integer delayMillis;

    @Value("${camel.activemq.redeliveryCount}")
    private Integer redeliveryCount;

    @Value("${camel.activemq.redeliveryDelayMs}")
    private Integer redeliveryDelayMs;

    @Value("${server.ssl.client-store-password}")
    private String sslClientStorePass = "user555";

    @Value("${server.ssl.client-consumer-trusted-store-path}")
    private String sslClientTrustedStorePath = "C:/Users/skyhunter/client_consumer_ts.p12";

    @Value("${server.ssl.client-consumer-key-store-path}")
    private String sslClientKeyStorePath = "C:/Users/skyhunter/client_consumer_ks.p12";

    private static Logger LOG = LogManager.getLogger(JmsConfig.class);

    @Bean
    public ConnectionFactory connectionFactory() throws Exception {
        ActiveMQSslConnectionFactory artemisConnectionFactory = new ActiveMQSslConnectionFactory();
        artemisConnectionFactory.setBrokerURL(brokerConnector);
        artemisConnectionFactory.setTrustStore(sslClientTrustedStorePath);
        artemisConnectionFactory.setTrustStorePassword(sslClientStorePass);
        artemisConnectionFactory.setKeyStore(sslClientKeyStorePath);
        artemisConnectionFactory.setKeyStorePassword(sslClientStorePass);
        artemisConnectionFactory.setUserName(brokerUser);
        artemisConnectionFactory.setPassword(brokerPass);
        return artemisConnectionFactory;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory(ConnectionFactory connectionFactory) {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setMaxConnections(8);
        pooledConnectionFactory.setConnectionFactory(connectionFactory);
        return pooledConnectionFactory;
    }

    @Bean
    public JmsConfiguration jmsConfiguration(PooledConnectionFactory pooledConnectionFactory) {
        JmsConfiguration jmsConfiguration = new JmsConfiguration();
        jmsConfiguration.setConnectionFactory(pooledConnectionFactory);
        jmsConfiguration.setTransacted(false);
        jmsConfiguration.setConcurrentConsumers(10);
        return jmsConfiguration;
    }

    @Bean
    public ActiveMQComponent jmsComponent(JmsConfiguration jmsConfiguration) {
        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
        activeMQComponent.setConfiguration(jmsConfiguration);
        return activeMQComponent;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(connectionFactory());
        return transactionManager;
    }

    @Bean
    public CamelContext camelContext(ActiveMQComponent jmsComponent, PlatformTransactionManager transactionManager) {
        SimpleRegistry registry = new SimpleRegistry();
        Map<Class<?>, Object> beanTransactionMap = new HashMap<>();
        beanTransactionMap.put(PlatformTransactionManager.class, transactionManager);
        registry.put("transactionManager", beanTransactionMap);
        Map<Class<?>, Object> beanMap = new HashMap<>();
        beanMap.put(CamelConsumer.class, new CamelConsumer(delayMillis));
        registry.put("camelConsumer", beanMap);

        CamelContext camelContext = new DefaultCamelContext(registry);
        camelContext.addComponent("jmsComponent", jmsComponent);

        InputToOutputRouteBuilder inputToOutputRoute = new InputToOutputRouteBuilder(queueInputOutputEndpoint, queueOutputEndpoint, redeliveryCount, redeliveryDelayMs);
        //FromOutputToBeanBuilder outputToBeanRoute = new FromOutputToBeanBuilder(queueInputOutputBeanEndpoint, queueOutputBeanEndpoint, redeliveryCount, redeliveryDelayMs);
        try {
            camelContext.addRoutes(inputToOutputRoute);
            camelContext.start();
        } catch (Exception e) {
            LOG.error("Error camel context start");
            e.printStackTrace();
        }
        return camelContext;
    }

}

