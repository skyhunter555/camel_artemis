package ru.syntez.camel.artemis.component;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import ru.syntez.camel.artemis.exceptions.RouterException;

/**
 * Configuration custom CamelErrorProcessor
 *
 * @author Skyhunter
 * @date 28.01.2021
 */
//@Component
public class CamelErrorProcessor implements Processor {

    private static Logger LOG = LogManager.getLogger(CamelConsumer.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        if (cause == null) {
            return;
        }
        if (cause instanceof RouterException) {
            LOG.error("******** SOME TEST EXCEPTION FOR TEST TRANSACTION: ");
        } else {
            LOG.error("******** UNKNOWN ERROR: ", cause);
            //TODO sending Error message to client
            Message msg = exchange.getIn().getBody(Message.class);
            try {

            } catch (Exception e) {
            }
        }
    }
}