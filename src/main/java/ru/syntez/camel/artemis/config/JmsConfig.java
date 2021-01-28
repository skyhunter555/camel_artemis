package ru.syntez.camel.artemis.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.syntez.camel.artemis.component.CamelConsumer;
import ru.syntez.camel.artemis.component.CamelRouteBuilder;

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

    @Value("${server.activemq.queues.input-endpoint}")
    private String queueInputEndpoint = "inputqueue";

    @Value("${server.activemq.queues.output-order-endpoint}")
    private String queueOutputOrderEndpoint = "outputorder";

    @Value("${server.work-time}")
    private Integer delayMillis;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerConnector);
        connectionFactory.setUserName(brokerUser);
        connectionFactory.setPassword(brokerPass);
        return connectionFactory;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(connectionFactory());
        pooledConnectionFactory.setConnectionTimeout(5000);
        pooledConnectionFactory.setMaxConnections(10);
        return pooledConnectionFactory;
    }

    @Bean
    public CamelContext camelContext() {

        SimpleRegistry registry = new SimpleRegistry();
        Map<Class<?>, Object> beanMap = new HashMap<>();
        beanMap.put(CamelConsumer.class, new CamelConsumer(delayMillis));
        registry.put("camelConsumer", beanMap);

        CamelContext camelContext = new DefaultCamelContext(registry);
        JmsConfiguration jmsConfiguration = new JmsConfiguration(pooledConnectionFactory());
        jmsConfiguration.setConcurrentConsumers(concurrentConsumers); //Пул потоков JMS слушателей для обслуживания входящих сообщений
        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
        activeMQComponent.setConfiguration(jmsConfiguration);
        camelContext.addComponent("jmsComponent", activeMQComponent);

        CamelRouteBuilder routeBuilder = new CamelRouteBuilder(queueInputEndpoint, queueOutputOrderEndpoint);
        try {
            camelContext.addRoutes(routeBuilder);
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camelContext;
    }

}

