package ru.syntez.camel.artemis.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import ru.syntez.camel.artemis.component.CamelConsumer;
import ru.syntez.camel.artemis.component.FromOutputToBeanBuilder;
import ru.syntez.camel.artemis.component.InputToOutputRouteBuilder;

import javax.jms.Session;
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

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerConnector);
        connectionFactory.setUserName(brokerUser);
        connectionFactory.setPassword(brokerPass);
        connectionFactory.setNonBlockingRedelivery(true);
        return connectionFactory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(connectionFactory());
        return transactionManager;
    }

    @Bean
    public CamelContext camelContext() {

        SimpleRegistry registry = new SimpleRegistry();
        Map<Class<?>, Object> beanTransactionMap = new HashMap<>();
        beanTransactionMap.put(PlatformTransactionManager.class, transactionManager());
        registry.put("transactionManager", beanTransactionMap);

        Map<Class<?>, Object> beanMap = new HashMap<>();
        beanMap.put(CamelConsumer.class, new CamelConsumer(delayMillis));
        registry.put("camelConsumer", beanMap);

        CamelContext camelContext = new DefaultCamelContext(registry);
        JmsConfiguration jmsConfiguration = new JmsConfiguration(connectionFactory());
        jmsConfiguration.setConcurrentConsumers(concurrentConsumers); //Пул потоков JMS слушателей для обслуживания входящих сообщений
        jmsConfiguration.setTransacted(true);
        jmsConfiguration.setAcknowledgementMode(Session.CLIENT_ACKNOWLEDGE); //Подтверждение будет отправлено только тогда, когда код консюмера в явном виде вызовет метод Message.acknowledge ().
        jmsConfiguration.setCacheLevelName("CACHE_AUTO"); //Пример конфигураци кеша

        ActiveMQComponent activeMQComponent = ActiveMQComponent.activeMQComponent();
        activeMQComponent.setConfiguration(jmsConfiguration);
        camelContext.addComponent("jmsComponent", activeMQComponent);

        InputToOutputRouteBuilder inputToOutputRoute = new InputToOutputRouteBuilder(queueInputOutputEndpoint, queueOutputEndpoint, redeliveryCount, redeliveryDelayMs);
        FromOutputToBeanBuilder outputToBeanRoute = new FromOutputToBeanBuilder(queueInputOutputBeanEndpoint, queueOutputBeanEndpoint, redeliveryCount, redeliveryDelayMs);
        try {
            camelContext.addRoutes(inputToOutputRoute);
         //   camelContext.addRoutes(outputToBeanRoute);
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camelContext;
    }

}

