//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.05.04 at 11:39:52 AM EDT 
//


package com.example.consumingwebservice.wsdl;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.example.consumingwebservice.wsdl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.example.consumingwebservice.wsdl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VenueRequirementsResponse }
     * 
     */
    public VenueRequirementsResponse createVenueRequirementsResponse() {
        return new VenueRequirementsResponse();
    }

    /**
     * Create an instance of {@link CheckVenueMeetsRequirments }
     * 
     */
    public CheckVenueMeetsRequirments createCheckVenueMeetsRequirments() {
        return new CheckVenueMeetsRequirments();
    }

    /**
     * Create an instance of {@link CheckVenueMeetsRequirmentsResponse }
     * 
     */
    public CheckVenueMeetsRequirmentsResponse createCheckVenueMeetsRequirmentsResponse() {
        return new CheckVenueMeetsRequirmentsResponse();
    }

    /**
     * Create an instance of {@link VenueRequirements }
     * 
     */
    public VenueRequirements createVenueRequirements() {
        return new VenueRequirements();
    }

}
