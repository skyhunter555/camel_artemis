package ru.syntez.camel.artemis.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import ru.syntez.camel.artemis.entities.RoutingDocument;
import ru.syntez.camel.artemis.exceptions.RouterException;

import javax.xml.bind.JAXBContext;

/**
 * Маршрутиразиця сообщений:
 * Из входящей очереди в исходящую, с обработкой в бине - в транзакционном режиме
 * Обработчик пропускает первые 10 сообщений, затем выдает ошибку.
 * После этого происходит перерассылка и остальные сообщения успешно обрабатываются.
 *
 * @author Skyhunter
 * @date 28.01.2021
 */
public class InputToOutputRouteBuilder extends RouteBuilder {

    private final String queueInputEndpoint;
    private final String queueOutputEndpoint;
    private final Integer redeliveryCount;
    private final Integer redeliveryDelayMs;

    public InputToOutputRouteBuilder(
            String queueInputEndpoint,
            String queueOutputEndpoint,
            Integer redeliveryCount,
            Integer redeliveryDelayMs
    ) {
        this.queueInputEndpoint = queueInputEndpoint;
        this.queueOutputEndpoint = queueOutputEndpoint;
        this.redeliveryCount = redeliveryCount;
        this.redeliveryDelayMs = redeliveryDelayMs;
    }

    private JaxbDataFormat xmlDataFormat = new JaxbDataFormat();

    @Override
    public void configure() throws Exception {

        JAXBContext context = JAXBContext.newInstance(RoutingDocument.class);
        xmlDataFormat.setContext(context);

        onException(Exception.class).process(new CamelErrorProcessor()).log("******** ERROR ON ROUTING ")
                .handled(true)
                .redeliveryDelay(redeliveryDelayMs)
                .maximumRedeliveries(redeliveryCount)
                .rollback();

        from(queueInputEndpoint)
                //.log("******** ROUTING FROM INPUT QUEUE TO OUTPUT")
                .transacted()
                .doTry()
                .unmarshal(xmlDataFormat)
                .bean("camelConsumer", "execute(${body})");
                //.log("******** CONSUME MESSAGE AND SEND TO OUTPUT QUEUE")
               // .to(queueOutputEndpoint);


    }
}
