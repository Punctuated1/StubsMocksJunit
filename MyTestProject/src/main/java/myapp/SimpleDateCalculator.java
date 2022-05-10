package myapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SimpleDateCalculator {
	private LocalDate baselineDate; 
	
	public SimpleDateCalculator() {
		baselineDate = LocalDate.now();
	}
	public SimpleDateCalculator(String dateString) {
		baselineDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
	}
	
	public LocalDate getDateForSpecifiedDayInFuture(long daysInFuture) throws NumericRangeException {
		if(daysInFuture < 1) {
			throw new NumericRangeException("Days must be greater than zero.");
		} 
		return baselineDate.plusDays(daysInFuture);
	}
	
}
