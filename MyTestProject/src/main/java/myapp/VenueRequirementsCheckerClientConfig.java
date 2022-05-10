package myapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.example.consumingwebservice.wsdl.CheckVenueMeetsRequirmentsResponse;
import com.example.consumingwebservice.wsdl.VenueRequirements;
import com.example.consumingwebservice.wsdl.VenueRequirementsResponse;

@Configuration
public class VenueRequirementsCheckerClientConfig {
	 @Value("${venue.checker.url}")
	private String venueRequirementCheckerUri; 
	 @Value("${venue.checker.port}")
	private String venueRequirementCheckerPort; 
	 
	private String venueRequirementCheckerUriString; 

	
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPaths("com.example.consumingwebservice.wsdl");
//		marshaller.setClassesToBeBound(VenueRequirements.class, VenueRequirementsResponse.class);
		return marshaller;
	}

	@Bean
	public Jaxb2Marshaller unMarshaller() {
		Jaxb2Marshaller unMarshaller = new Jaxb2Marshaller();
		unMarshaller.setContextPaths("com.example.consumingwebservice.wsdl");
//		unMarshaller.setClassesToBeBound(CheckVenueMeetsRequirmentsResponse.class);
		return unMarshaller;
	}

//	@Bean
	
	@Bean
	public VenueRequirementsCheckerClient venueRequirementsCheckerClient(Jaxb2Marshaller marshaller, Jaxb2Marshaller unMarshaller) {
		VenueRequirementsCheckerClient venueRequirementsCheckerClient = new VenueRequirementsCheckerClient();
		venueRequirementCheckerUriString = venueRequirementCheckerUri+":"+venueRequirementCheckerPort;
		venueRequirementsCheckerClient.setDefaultUri(venueRequirementCheckerUriString);
		venueRequirementsCheckerClient.setMarshaller(marshaller);
		venueRequirementsCheckerClient.setUnmarshaller(unMarshaller);
		return venueRequirementsCheckerClient;
	}
}
