package ru.syntez.camel.artemis.component;

import org.apache.camel.*;
import org.apache.camel.support.DefaultConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageConsumer extends DefaultConsumer {

    private static Logger LOG = LogManager.getLogger(MessageConsumer.class);
    private final Endpoint endpoint;
    private final Processor processor;

    private boolean moreOpeners = true;

    public MessageConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        this.processor = processor;
    }

    @Override
    public void doneUoW(Exchange exchange)
    {
        //long deliveryTag = envelope.getDeliveryTag();
        // positively acknowledge a single delivery, the message will
        // be discarded
        //channel.basicAck(deliveryTag, false);
        super.doneUoW(exchange);
    }

    @Override
    protected void doStart() throws Exception {

        int countRead=0; // number of bytes read
        super.doStart();
      //do {
      //    countRead++;
      //    String msg = String.valueOf(countRead)+" "+System.currentTimeMillis();
      //    Exchange ex = endpoint.createExchange(ExchangePattern.InOnly);
      //    ex.getIn().setBody(msg);
      //    getAsyncProcessor().process(ex, new AsyncCallback() {
      //        @Override
      //        public void done(boolean doneSync) {
      //            LOG.info("Message was processed " + (doneSync ? "synchronously" : "asynchronously"));

      //        }
      //    });
      //    // This is an echo server so echo request back to requester
      //} while (moreOpeners);
    }

    @Override
    protected void doStop() throws Exception {
        moreOpeners = false;
        LOG.debug("Message processor is shutdown");
    }

}