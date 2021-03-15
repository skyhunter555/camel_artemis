package ru.syntez.camel.artemis.test;

import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.syntez.camel.artemis.config.JmsConfig;

import javax.jms.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CamelArtemisTest {

    private static Logger LOG = LogManager.getLogger(JmsConfig.class);

    @Value("${server.activemq.queues.input-output-endpoint}")
    private String queueInputOutputEndpoint = "inputToOutputQueue";

    @Configuration
    public static class Config {

        @Value("${camel.activemq.brokerUrl}")
        private String brokerConnector = "tcp://localhost:61616";

        @Value("${server.ssl.client-store-password}")
        private String sslClientStorePass = "user555";

        @Value("${server.ssl.client-sender-trusted-store-path}")
        private String sslClientTrustedStorePath = "C:/Users/skyhunter/client_sender_ts.p12";

        @Value("${server.ssl.client-sender-key-store-path}")
        private String sslClientKeyStorePath = "C:/Users/skyhunter/client_sender_ks.p12";

        @Value("${camel.activemq.user}")
        private String brokerUser = "user";

        @Value("${camel.activemq.password}")
        private String brokerPass = "user";

        @Value("${camel.activemq.concurrentConsumers}")
        private Integer concurrentConsumers = 10;

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
    }

    @Autowired
    ConnectionFactory connectionFactory;

    @Test
    public void sendMessageToInputQueueTest() {
        try {
            String[] queueInputOutput = queueInputOutputEndpoint.split(":");
            String messageXml = new String(Files.readAllBytes(Paths.get(getClass().getResource("/router_doc_1.xml").toURI())));
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer msgProducer = session.createProducer(session.createQueue(queueInputOutput[2]));
            for (int i = 0; i < 200; i++) {
                TextMessage textMessage = session.createTextMessage(messageXml);
                msgProducer.send(textMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
