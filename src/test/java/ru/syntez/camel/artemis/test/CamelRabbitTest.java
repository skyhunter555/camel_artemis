package ru.syntez.camel.artemis.test;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CamelRabbitTest {

    /*
    @Configuration
    public static class Config {

        @Value("${camel.component.rabbitmq.host}")
        private String brokerHost = "localhost";

        @Value("${camel.component.rabbitmq.port}")
        private Integer brokerPort = 5672;

        @Value("${camel.component.rabbitmq.username}")
        private String username = "user";

        @Value("${camel.component.rabbitmq.password}")
        private String password = "user";

        @Bean
        public ConnectionFactory connectionFactory() {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            connectionFactory.setPort(brokerPort);
            connectionFactory.setUsername(username);
            connectionFactory.setPassword(password);
            connectionFactory.setVirtualHost(username);
            connectionFactory.setRequestedChannelMax(1);
            return connectionFactory;
        }
    }

    private String queueInputEndpoint = "inputqueue";

    @Autowired
    ConnectionFactory connectionFactory;

    @Test
    public void sendMessageToInputQueueTest() {
        try {
            String messageXml = new String(Files.readAllBytes(Paths.get(getClass().getResource("/router_doc_1.xml").toURI())));
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueInputEndpoint, true, false, false, null);
            channel.basicPublish("", queueInputEndpoint, null, messageXml.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
