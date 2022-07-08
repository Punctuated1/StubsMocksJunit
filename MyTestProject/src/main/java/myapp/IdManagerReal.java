package myapp;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("idManager")
@Profile({"prod","qa","int","dev"})
public class IdManagerReal implements IdManagerInterface {

	@Override
	public String getUniqueEventId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUniqueConfirmationId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUniqueFeedbackId() {
		// TODO Auto-generated method stub
		return null;
	}

}
