# StubsMocksJunit
Junit Testing with Stubs and Mocks in Java

Our SUT is an event scheduler. 
The SUT accepts an input object that defines an event to be scheduled
 
The input object contains a description of the event, and email distribution list, a point of contact email address for the event coordinator, the venue for the event, a payment instrument to pay deposit on the venue, amount of the deposit; and a number of business days in the future the event is to be scheduled on.
 
1.	The SUT calls a SOAP web service to verify the selected venue for the event meets the requirements for the event:
o	We test expectations for:
•	The crowd capacity for the venue is greater than the expected attendance
•	The venue includes a large screen backdrop on stage for slides and videos
•	An exception is thrown by the SOAP access attempt
2.	The SUT calls a collaborator utility that is used by several other services that accepts a number representing business days in the future
o	The collaborator performs boundary checks on the input number. If the input number fails any of the boundary checks it throws a InputException, or a subtype of InputException
•	The number must be greater that zero
•	The number cannot be greater than 30
o	If the number passes boundary validations it makes one or more calls to an external dependency, a database that contains our corporate calendar - this is a relatively slow process and we want to process over 10,000 unit tests in under 5 minutes so we decide to use a Test Double, since this is a share utility several of our test cases will utilize we elect to build s custom stub
•	We model our stub modeled on November 2028, where the base date the business days in future are computed from is October 31
	There are two holidays - our simple calendar has 22 and 23 for Thanksgiving
	Weekends are 4-5, 11-12, 18-19, 25 - 26, and 2-3 and 9-10 in next month
•	The stub returns the date that is the specified number of business days from our base date (10-31-2028)
3.	The SUT next calls a REST web service to verify the venue is available for the planned date and reserve it if available with a deposit
o	We test expectations for:
•	Date is available and reservation is successful
•	Date is not available and reservation is denied
•	Connection fails
•	Invalid credit card
•	Expired credits card
•	Invalid CVC
•	Deposit amount exceeds available credit, transaction denied
•	Credit approved
4.	The SUT then creates a database record for the event 
o	The database call is relatively slow and we elect to mock the database with Mockito
•	We set expectations for: 
	Successful persistence of the appointment
	Failed persistence of the appointment due to illegal argument exception
5.	Finally, we send an email invitation to the event to our distribution list - we do not want to actually send emails and this is our only email process so we use Mockito to create a Test Double  
<ul>	We set expectations that test:
<li>	Email address list is not valid,
<li>	Sender is not valid
<li>	Subject is missing
<li>	Subject is too long
</ul>
Thin Slices Tests to implement:
1.	SOAP WS with Spring MockWebServiceServer
2.	Shared collaborator as a custom stub
3.	Rest API with Spring MockRestServiceServer
4.	Database dependency with Mockito
5.	Non-shared collaborator with Mockito
