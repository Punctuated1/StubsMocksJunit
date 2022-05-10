package myapp;

public class EventConfirmation {
	private String eventId;
	private String confirmationStatus;
	private String confirmationStatusExplanation;
	private String confirmationId;
	private String eventDescription;
	private String eventDate;
	private String venueReservationStatus;
	private String venueReservationStatusExplanation;
	private String venueReservationConfirmationNumber;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getConfirmationStatus() {
		return confirmationStatus;
	}

	public void setConfirmationStatus(String confirmationStatus) {
		this.confirmationStatus = confirmationStatus;
	}


	public String getConfirmationStatusExplanation() {
		return confirmationStatusExplanation;
	}

	public void setConfirmationStatusExplanation(String confirmationStatusExplanation) {
		this.confirmationStatusExplanation = confirmationStatusExplanation;
	}

	public String getConfirmationId() {
		return confirmationId;
	}

	public void setConfirmationId(String confirmationId) {
		this.confirmationId = confirmationId;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public EventConfirmation() {
		
	}

	public String getVenueReservationStatus() {
		return venueReservationStatus;
	}

	public void setVenueReservationStatus(String venueReservationStatus) {
		this.venueReservationStatus = venueReservationStatus;
	}

	public String getVenueReservationStatusExplanation() {
		return venueReservationStatusExplanation;
	}

	public void setVenueReservationStatusExplanation(String venueReservationStatusExplanation) {
		this.venueReservationStatusExplanation = venueReservationStatusExplanation;
	}

	public String getVenueReservationConfirmationNumber() {
		return venueReservationConfirmationNumber;
	}

	public void setVenueReservationConfirmationNumber(String venueReservationConfirmationNumber) {
		this.venueReservationConfirmationNumber = venueReservationConfirmationNumber;
	}

}
