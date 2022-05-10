package myapp;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("emailGateway")
@Profile({"prod","qa","int","dev"})
public class EmailGateway implements EmailGatewayInterface {

	public EmailGateway() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String sendEmailToDistributionList(EmailPackage emailPackage) {
		// TODO Auto-generated method stub
		return null;
	}

}
