//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.05.04 at 11:39:52 AM EDT 
//


package com.example.consumingwebservice.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="venueRequirements" type="{http://soapy1}VenueRequirements"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "venueRequirements"
})
@XmlRootElement(name = "checkVenueMeetsRequirments")
public class CheckVenueMeetsRequirments {

    @XmlElement(required = true)
    protected VenueRequirements venueRequirements;

    /**
     * Gets the value of the venueRequirements property.
     * 
     * @return
     *     possible object is
     *     {@link VenueRequirements }
     *     
     */
    public VenueRequirements getVenueRequirements() {
        return venueRequirements;
    }

    /**
     * Sets the value of the venueRequirements property.
     * 
     * @param value
     *     allowed object is
     *     {@link VenueRequirements }
     *     
     */
    public void setVenueRequirements(VenueRequirements value) {
        this.venueRequirements = value;
    }

}
