package ru.syntez.camel.artemis.entities;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * RoutingDocument model
 *
 * @author Skyhunter
 * @date 26.01.2021
 */
@XmlRootElement(name = "routingDocument")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class RoutingDocument {

    private DocumentTypeEnum docType; //TODO enum: invoice, order
    private int docId;

}
