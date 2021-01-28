package ru.syntez.camel.artemis.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.syntez.camel.artemis.entities.RoutingDocument;
import javax.xml.bind.JAXBContext;

/**
 * Configuration custom CamelRouteBuilder
 *
 * @author Skyhunter
 * @date 28.01.2021
 */
@Component
public class CamelRouteBuilder extends RouteBuilder {

    @Value("${server.activemq.queues.input-endpoint}")
    private String queueInputEndpoint = "inputqueue";

    @Value("${server.activemq.queues.output-order-endpoint}")
    private String queueOutputOrderEndpoint = "outputorder";

    private final CamelErrorProcessor errorProcessor;
    public CamelRouteBuilder(CamelErrorProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
    }
    private JaxbDataFormat xmlDataFormat = new JaxbDataFormat();

    @Override
    public void configure() throws Exception {

        JAXBContext context = JAXBContext.newInstance(RoutingDocument.class);
        xmlDataFormat.setContext(context);

        onException(Exception.class).process(errorProcessor).log("******** ERROR ON ROUTING ").handled(true);

        from(queueInputEndpoint)
            .log("******** ROUTING FROM INPUT QUEUE TO OUTPUT")
            .to(queueOutputOrderEndpoint);

        from(queueOutputOrderEndpoint)
            .doTry().unmarshal(xmlDataFormat)
            .log("******** PROCESS MESSAGE FROM OUTPUT QUEUE")
            .to("bean:camelConsumer?method=execute(${body})");
    }
}
