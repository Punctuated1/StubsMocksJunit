package myapp;

import java.math.BigDecimal;

public class EventInformation {
	private String eventDescription;
	private String invitationDistributionListEmail;
	private String eventCoordinatorEmail;
	private String eventCoordinatorName;
	private String emailSubject;
	private String emailBody;	
	private String eventVenue;
	private String paymentCardForDeposit;
	private String paymentCardExpirationDate;
	private String paymentCardCVCCode;
	private BigDecimal depositAmount;
	private long eventBusinessDaysInFuture;
	private int expectedNumberAttendees;
	private boolean largeScreenNeeded;
	
	
	public EventInformation() {
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public String getInvitationDistributionListEmail() {
		return invitationDistributionListEmail;
	}

	public void setInvitationDistributionListEmail(String invitationDistributionListEmail) {
		this.invitationDistributionListEmail = invitationDistributionListEmail;
	}

	public String getEventCoordinatorEmail() {
		return eventCoordinatorEmail;
	}

	public void setEventCoordinatorEmail(String eventCoordinatorEmail) {
		this.eventCoordinatorEmail = eventCoordinatorEmail;
	}

	public String getEventCoordinatorName() {
		return eventCoordinatorName;
	}

	public void setEventCoordinatorName(String eventCoordinatorName) {
		this.eventCoordinatorName = eventCoordinatorName;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getEventVenue() {
		return eventVenue;
	}

	public void setEventVenue(String eventVenue) {
		this.eventVenue = eventVenue;
	}

	public String getPaymentCardForDeposit() {
		return paymentCardForDeposit;
	}

	public void setPaymentCardForDeposit(String paymentCardForDeposit) {
		this.paymentCardForDeposit = paymentCardForDeposit;
	}

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public long getEventBusinessDaysInFuture() {
		return eventBusinessDaysInFuture;
	}

	public void setEventBusinessDaysInFuture(long eventBusinessDaysInFuture) {
		this.eventBusinessDaysInFuture = eventBusinessDaysInFuture;
	}

	public String getPaymentCardExpirationDate() {
		return paymentCardExpirationDate;
	}

	public void setPaymentCardExpirationDate(String paymentCardExpirationDate) {
		this.paymentCardExpirationDate = paymentCardExpirationDate;
	}

	public String getPaymentCardCVCCode() {
		return paymentCardCVCCode;
	}

	public void setPaymentCardCVCCode(String paymentCardCVCCode) {
		this.paymentCardCVCCode = paymentCardCVCCode;
	}

	public int getExpectedNumberAttendees() {
		return expectedNumberAttendees;
	}

	public void setExpectedNumberAttendees(int expectedNumberAttendees) {
		this.expectedNumberAttendees = expectedNumberAttendees;
	}

	public boolean isLargeScreenNeeded() {
		return largeScreenNeeded;
	}

	public void setLargeScreenNeeded(boolean largeScreenNeeded) {
		this.largeScreenNeeded = largeScreenNeeded;
	}

}
