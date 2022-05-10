package myapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyTests {

	@Test
	void dateAddTestPositive() {
		//Arrange 
		long daysInFuture = 10;
		String baselineDate = "2022-03-31";
		String expectedReturnDate = "2022-04-10";
		SimpleDateCalculator simpleDateCalculator = new SimpleDateCalculator(baselineDate);
		
		//Act
		LocalDate actualReturnDate = null;
		try {
			actualReturnDate = simpleDateCalculator.getDateForSpecifiedDayInFuture(daysInFuture);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//Assert
		assertEquals(expectedReturnDate, actualReturnDate.toString());
		
	}

	@Test
	void dateAddTestNegative() {
		//Arrange 
		long daysInFuture = -10;
		String baselineDate = "2022-03-31";
		String expectedReturnDate = "N/A";
		String expectedExceptionMessage="Days must be greater than zero.";
		String expectedExceptionType="NumericRangeException";
		SimpleDateCalculator simpleDateCalculator = new SimpleDateCalculator(baselineDate);
		
		//Act
		Exception exception = assertThrows(InputException.class, () -> {
			LocalDate actualReturnDate = simpleDateCalculator.getDateForSpecifiedDayInFuture(daysInFuture);
		});
		
		//Assert
		String actualExceptionType = exception.getClass().getSimpleName();
		String actualexceptionMessage = exception.getMessage();
		
		assertEquals(expectedExceptionMessage, actualexceptionMessage);
		}

}
