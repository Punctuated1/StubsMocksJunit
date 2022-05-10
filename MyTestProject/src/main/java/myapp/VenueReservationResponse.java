package myapp;

public class VenueReservationResponse {
	private String venueReservationConfirmationNumber;
	private String venueReservationStatus;
	private String venueReservationStatusExplanation;
	
	public VenueReservationResponse() {
		
	}

	public String getVenueReservationConfirmationNumber() {
		return venueReservationConfirmationNumber;
	}

	public void setVenueReservationConfirmationNumber(String venueReservationConfirmationNumber) {
		this.venueReservationConfirmationNumber = venueReservationConfirmationNumber;
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

}
