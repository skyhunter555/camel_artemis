package ru.syntez.camel.artemis.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.syntez.camel.artemis.entities.RoutingDocument;
import ru.syntez.camel.artemis.exceptions.RouterException;

import java.util.Date;

/**
 * Configuration custom CamelConsumer
 *
 * @author Skyhunter
 * @date 28.01.2021
 */
public class CamelConsumer {

    private static Logger LOG = LogManager.getLogger(CamelConsumer.class);

    private Integer delayMillis;
    public CamelConsumer(Integer delayMillis) {
        this.delayMillis = delayMillis;
    }

    private Integer consumedDocumentCount = 0;
    private Date startTime = new Date();

    public void execute(RoutingDocument document) {

       //LOG.info("START CONSUME MESSAGE, docId: {} docType: {}", document.getDocId(), document.getDocType());
        //if (consumedDocumentCount >= 10) {
        //    //Сброс счетчика, для повторной отправки
        //    if (consumedDocumentCount >= 19) {
        //        consumedDocumentCount = 0;
        //    }
        //    throw new RouterException("ConsumedDocumentCount > 10. Send to redelivery");
        //}
        //try {
        //    Thread.sleep(delayMillis); //имитация обработки
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        Date totalTime = new Date();
        long seconds =  (totalTime.getTime() - startTime.getTime()) / 1000;
        consumedDocumentCount++;
        LOG.info("FINISH CONSUME MESSAGE, docId: {} docType: {}. Total consumed: {} seconds {}", document.getDocId(), document.getDocType(), consumedDocumentCount, seconds);
    }
}
