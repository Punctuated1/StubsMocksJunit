package myapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("corporateCalendar")
@Profile("test")
public class CorporateCalendarStub implements CorporateCalendarInterface {
	private final static LocalDate baseLineDate = LocalDate.parse("2028-10-31",DateTimeFormatter.ISO_DATE);
	public CorporateCalendarStub() {
	}

	// Test Double
	public LocalDate getDateForNumberOfBusinessDaysInFuture(long numberOfBusinessDaysInFuture) throws InputException {
		if(numberOfBusinessDaysInFuture<1 || numberOfBusinessDaysInFuture > 30) {
			throw new InputException("numberOfBusinessDays in future must be greater than zero and less than 31.");
		}
		long daysToAdd = numberOfBusinessDaysInFuture;
		if(numberOfBusinessDaysInFuture == 10) {
			//includes one weekend
			daysToAdd = daysToAdd + 4; 
		} else if(numberOfBusinessDaysInFuture == 30) {
			//includes 6 weekends and two days for thanksgiving;
			daysToAdd = daysToAdd + 14;
		}
		
		return baseLineDate.plusDays(daysToAdd);
	}
	
	public String identifySelf() {
		return "Test Double";
	}

	@Override
	public LocalDate getBaselineDate() {
		return baseLineDate;
	}

}
