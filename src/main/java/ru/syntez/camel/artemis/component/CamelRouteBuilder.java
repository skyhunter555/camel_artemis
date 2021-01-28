package ru.syntez.camel.artemis.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import ru.syntez.camel.artemis.entities.RoutingDocument;
import javax.xml.bind.JAXBContext;

/**
 * Configuration custom CamelRouteBuilder
 *
 * @author Skyhunter
 * @date 28.01.2021
 */
public class CamelRouteBuilder extends RouteBuilder {

    private String queueInputEndpoint;
    private String queueOutputOrderEndpoint;

    public CamelRouteBuilder(String queueInputEndpoint, String queueOutputOrderEndpoint) {
        this.queueInputEndpoint = queueInputEndpoint;
        this.queueOutputOrderEndpoint = queueOutputOrderEndpoint;
    }

    private JaxbDataFormat xmlDataFormat = new JaxbDataFormat();

    @Override
    public void configure() throws Exception {

        JAXBContext context = JAXBContext.newInstance(RoutingDocument.class);
        xmlDataFormat.setContext(context);

        from(queueInputEndpoint)
            .log("******** ROUTING FROM INPUT QUEUE TO OUTPUT")
            .to(queueOutputOrderEndpoint);

        from(queueOutputOrderEndpoint)
            .doTry().unmarshal(xmlDataFormat)
            .log("******** PROCESS MESSAGE FROM OUTPUT QUEUE")
            .to("bean:camelConsumer?method=execute(${body})");
    }
}
