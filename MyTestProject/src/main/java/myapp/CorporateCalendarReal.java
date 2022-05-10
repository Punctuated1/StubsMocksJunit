package myapp;

import java.time.LocalDate;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
@Component("corporateCalendar")
@Profile({"prod","qa","int","dev"})
public class CorporateCalendarReal implements CorporateCalendarInterface {

	public CorporateCalendarReal() {
	}

	@Override
	public LocalDate getDateForNumberOfBusinessDaysInFuture(long numberOfBusinessDaysInFuture) throws InputException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String identifySelf() {
		return "Real Thing";
	}

	@Override
	public LocalDate getBaselineDate() {
		// TODO Auto-generated method stub
		return null;
	}

}
