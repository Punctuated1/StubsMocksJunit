/**
 * 
 */
package myapp;

import java.time.LocalDate;

/**
 * @author hmt_9
 *
 */
public interface CorporateCalendarInterface {
	public LocalDate getDateForNumberOfBusinessDaysInFuture(long numberOfBusinessDaysInFuture) throws InputException;
	public String identifySelf();
	public LocalDate getBaselineDate();
}
