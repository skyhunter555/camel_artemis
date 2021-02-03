package ru.syntez.camel.artemis.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import ru.syntez.camel.artemis.entities.RoutingDocument;
import javax.xml.bind.JAXBContext;

/**
 * Маршрутиразиця сообщений:
 * 1. Из входящей очереди в исходящую - без обработки
 * 2. Из исходящей очереди выборка сообщений в бин - в транзакционном режиме
 * Обработчик пропускает первые 10 сообщений, затем выдает ошибку.
 * После этого происходит перерассылка и остальные сообщения успешно обрабатываются.
 *
 * @author Skyhunter
 * @date 28.01.2021
 */
public class FromOutputToBeanBuilder extends RouteBuilder {

    private final String queueInputToOutputBeanEndpoint;
    private final String queueOutputBeanEndpoint;
    private final Integer redeliveryCount;
    private final Integer redeliveryDelayMs;

    public FromOutputToBeanBuilder(
            String queueInputToOutputBeanEndpoint,
            String queueOutputBeanEndpoint,
            Integer redeliveryCount,
            Integer redeliveryDelayMs
    ) {
        this.queueInputToOutputBeanEndpoint = queueInputToOutputBeanEndpoint;
        this.queueOutputBeanEndpoint = queueOutputBeanEndpoint;
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
                .maximumRedeliveries(redeliveryCount);

        from(queueInputToOutputBeanEndpoint)
            .log("******** ROUTING FROM INPUT QUEUE TO OUTPUT")
            .to(queueOutputBeanEndpoint);

        from(queueOutputBeanEndpoint)
            .transacted()
            .log("******** PROCESS MESSAGE FROM OUTPUT QUEUE")
            .bean("camelConsumer", "execute(${body})");
    }
}
