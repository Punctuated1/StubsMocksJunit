package myapp;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import com.example.consumingwebservice.wsdl.CheckVenueMeetsRequirments;
import com.example.consumingwebservice.wsdl.CheckVenueMeetsRequirmentsResponse;
import com.example.consumingwebservice.wsdl.VenueRequirementsResponse;

public class VenueRequirementsCheckerClient extends WebServiceGatewaySupport {

	public VenueRequirementsCheckerClient() {
	}

	public VenueRequirementsCheckerClient(WebServiceMessageFactory messageFactory) {
		super(messageFactory);
	}

	public CheckVenueMeetsRequirmentsResponse getVenueRequirementsResponse(CheckVenueMeetsRequirments checkVenueMeetsRequirments) throws Exception{
		Source source = makeMeAStringSource(checkVenueMeetsRequirments);
		Result result = new StringResult(); 

		getWebServiceTemplate().sendSourceAndReceiveToResult(source, result);
		System.out.println("result: "+result.toString());
		return makeMeAnObject(result);
	}
	
	private Source makeMeAStringSource(CheckVenueMeetsRequirments checkVenueMeetsRequirments) {
		Jaxb2Marshaller m1 = new Jaxb2Marshaller();
		m1.setContextPath("com.example.consumingwebservice.wsdl");

		StringResult result = new StringResult();
		
		m1.marshal(checkVenueMeetsRequirments, result);
		System.out.println("Source: "+result.toString());
		return new StringSource(result.toString()); 

	}
	private CheckVenueMeetsRequirmentsResponse makeMeAnObject(Result result) {
		Jaxb2Marshaller m1 = new Jaxb2Marshaller();
		m1.setContextPath("com.example.consumingwebservice.wsdl");

		CheckVenueMeetsRequirmentsResponse checkVenueMeetsRequirmentsResponse = (CheckVenueMeetsRequirmentsResponse)m1.unmarshal(new StringSource(result.toString()));
		return checkVenueMeetsRequirmentsResponse;
	}
}
