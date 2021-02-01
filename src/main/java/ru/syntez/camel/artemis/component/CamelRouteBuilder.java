package ru.syntez.camel.artemis.component;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.activemq.ActiveMQComponent;
import org.apache.camel.component.bean.BeanEndpoint;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.support.DefaultProducer;
import ru.syntez.camel.artemis.entities.RoutingDocument;
import ru.syntez.camel.artemis.exceptions.RouterException;

import javax.xml.bind.JAXBContext;

/**
 * Configuration custom CamelRouteBuilder
 *
 * @author Skyhunter
 * @date 28.01.2021
 */
public class CamelRouteBuilder extends RouteBuilder {

    private final String queueInputEndpoint;
    private final String queueOutputOrderEndpoint;

    public CamelRouteBuilder(String queueInputEndpoint, String queueOutputOrderEndpoint) {
        this.queueInputEndpoint = queueInputEndpoint;
        this.queueOutputOrderEndpoint = queueOutputOrderEndpoint;
    }

    private JaxbDataFormat xmlDataFormat = new JaxbDataFormat();

    @Override
    public void configure() throws Exception {

        JAXBContext context = JAXBContext.newInstance(RoutingDocument.class);
        xmlDataFormat.setContext(context);

        onException(RouterException.class).process(new CamelErrorProcessor()).log("******** ERROR ON ROUTING ").handled(true);

        from(queueInputEndpoint)
            .log("******** ROUTING FROM INPUT QUEUE TO OUTPUT")
            .to(queueOutputOrderEndpoint);

        from(queueOutputOrderEndpoint)
            .transacted()
            .doTry().unmarshal(xmlDataFormat)
            .log("******** PROCESS MESSAGE FROM OUTPUT QUEUE")
            .doTry()
                .bean("camelConsumer", "execute(${body})")
            .doCatch(RouterException.class)
                .rollback()
            .end();
    }
}
