package myapp;

import java.math.BigDecimal;

public class VenueReservationRequest {
	private String eventVenue;
	private String eventDate;
	private String eventDescription;
	private String eventCoordinatorEmail;
	private String eventCoordinatorName;
	private String paymentCardForDeposit;
	private String paymentCardExpirationDate;
	private String paymentCardCVCCode;
	private BigDecimal depositAmount;


	public VenueReservationRequest() {
	}

	public String getEventVenue() {
		return eventVenue;
	}

	public void setEventVenue(String eventVenue) {
		this.eventVenue = eventVenue;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
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

	public String getPaymentCardForDeposit() {
		return paymentCardForDeposit;
	}

	public void setPaymentCardForDeposit(String paymentCardForDeposit) {
		this.paymentCardForDeposit = paymentCardForDeposit;
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

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

}
