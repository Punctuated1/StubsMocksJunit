package myapp;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("idManager")
@Profile("test")
public class IdManagerStub implements IdManagerInterface {

	@Override
	public String getUniqueEventId() {
		return "EID-123";
	}

	@Override
	public String getUniqueConfirmationId() {
		return "CID-321";
	}

	@Override
	public String getUniqueFeedbackId() {
		return "FID-1001";
	}
	

}
