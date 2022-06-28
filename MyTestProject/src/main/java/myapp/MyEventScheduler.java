package myapp;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.example.consumingwebservice.wsdl.CheckVenueMeetsRequirments;
import com.example.consumingwebservice.wsdl.CheckVenueMeetsRequirmentsResponse;
import com.example.consumingwebservice.wsdl.VenueRequirements;
import com.example.consumingwebservice.wsdl.VenueRequirementsResponse;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import myapp.generated.avro.TweetDto;


@Service
public class MyEventScheduler {
	@Autowired
	@Qualifier("corporateCalendar")
	private CorporateCalendarInterface corporateCalendar;

	@Autowired
	@Qualifier("idManager")
	private IdManagerInterface idManager;
	
	@Autowired
	private KafkaProducerConfigHelper kafkaProducerConfigHelper;
	
	@Autowired
	private EmailGatewayInterface emailGateway;
	
	@Autowired 
	private EventRepository eventRepository;

    @Autowired
	private RestTemplate restTemplate;
    
    @Autowired
    private VenueRequirementsCheckerClient venueCheckerWebServiceTemplate;
	
    private boolean errorOccurred = false;
    
    private EventConfirmation eventConfirmation;

	@Value("${venue.reservation.url}")
	private String venueReservationUrlString;
	
	@Value("${venue.checker.port}")
	private int venueCheckerPort;
 
    @Value("${kafka.topic.tweet}")
    private String kafkaTopicTweet;
     
	private Map<String, String> emailResponseMap = new HashMap<String, String>();
    
	public MyEventScheduler() {
		emailResponseMap.put(AppConstants.EMAIL_INVALID_DISTRIBUTION_LIST, "The distribution List is malformed.");
		emailResponseMap.put(AppConstants.EMAIL_INVALID_SENDER_ADDRESS, "The sender's email address is malformed.");
		emailResponseMap.put(AppConstants.EMAIL_NO_SUBJECT, "The subject for the email is null or empty. A subject for the email is required.");
		emailResponseMap.put(AppConstants.EMAIL_SUBJECT_TOO_LONG, "The subject is too long. The subject can be no longer than 40 characters.");
		emailResponseMap.put(AppConstants.EMAIL_NO_BODY, "The body for the email is null or empty. A body is required for the email.");
		emailResponseMap.put(AppConstants.SUCCESS, "The event has been created, the venue reserved, and the invitations sent via email.");
		
	}

	public EventConfirmation scheduleMyEvent(EventInformation eventInformation) { 
		
		eventConfirmation = populateBasicConfirmation(eventInformation);
		eventConfirmation.setConfirmationStatus("success");
		errorOccurred=false;
		
		checkVenueMeetsEventRequirements(eventInformation);
		getEventDateFromCorporateCalendar(eventInformation.getEventBusinessDaysInFuture());
		reserveVenue(eventInformation);
		persistEvent(eventInformation);
		sendEmail(eventInformation);
		tweetIt(eventInformation);

		return eventConfirmation;
	}
	
	private EventDetail populateEventDetail(EventInformation ei) {
		EventDetail edt = new EventDetail();
		edt.setEventCoordinatorEmail(ei.getEventCoordinatorEmail());
		edt.setEventCoordinatorName(ei.getEventCoordinatorName());
		edt.setEventDate(eventConfirmation.getEventDate());
		edt.setEventDescription(ei.getEventDescription());
		edt.setEventId(eventConfirmation.getEventId());
		edt.setEventVenue(ei.getEventVenue());
		edt.setInvitationDistributionListEmail(ei.getInvitationDistributionListEmail());
		edt.setScheduledDate(corporateCalendar.getBaselineDate().toString());
		return edt;
	}
	
	private EventConfirmation populateBasicConfirmation(EventInformation eventInformation) {
		EventConfirmation eventConfirmation = new EventConfirmation();
		eventConfirmation.setEventDescription(eventInformation.getEventDescription());
		eventConfirmation.setEventId(idManager.getUniqueEventId());
		eventConfirmation.setConfirmationId(idManager.getUniqueConfirmationId());
		return eventConfirmation;
	}
	
	private void checkVenueMeetsEventRequirements(EventInformation eventInformation) {
		VenueRequirements venueRequirements = new VenueRequirements();
		venueRequirements.setExpectedNumberAttendees(eventInformation.getExpectedNumberAttendees());
		venueRequirements.setLargeScreenNeeded(eventInformation.isLargeScreenNeeded());
		venueRequirements.setNameOfVenue(eventInformation.getEventVenue());
		CheckVenueMeetsRequirments checkVenueMeetsRequirments = new CheckVenueMeetsRequirments();
		checkVenueMeetsRequirments.setVenueRequirements(venueRequirements);
		
		try {
			CheckVenueMeetsRequirmentsResponse checkVenueMeetsRequirmentsResponse = (CheckVenueMeetsRequirmentsResponse) 
				venueCheckerWebServiceTemplate.getVenueRequirementsResponse(checkVenueMeetsRequirments); 
			VenueRequirementsResponse venueRequirementsResponse = checkVenueMeetsRequirmentsResponse.getCheckVenueMeetsRequirmentsReturn();
			eventConfirmation.setConfirmationStatus(venueRequirementsResponse.getResponseCode());
			eventConfirmation.setConfirmationStatusExplanation(venueRequirementsResponse.getResponseCodeExplanation());
			if(!eventConfirmation.getConfirmationStatus().equalsIgnoreCase(AppConstants.SUCCESS)) {
				errorOccurred=true;
			}
		} catch(Exception ex) {
			errorOccurred=true;
			eventConfirmation.setConfirmationStatus(AppConstants.VENUE_REQ_EXCEPTION_OCCURED);
			eventConfirmation.setConfirmationStatusExplanation(AppConstants.VENUE_REQ_EXCEPTION_OCCURED+ ": "+ex.getClass().getSimpleName()+" -- "+ex.getMessage());
		}
	}
	
	private void getEventDateFromCorporateCalendar(long eventBusinessDaysInFuture) {
		
		String responseString = "";
		if(!errorOccurred) {
			try {
				LocalDate eventDate = corporateCalendar.getDateForNumberOfBusinessDaysInFuture(eventBusinessDaysInFuture);
				responseString=eventDate.toString();
				eventConfirmation.setEventDate(responseString);
			} catch(Exception ex) {
				responseString="InputException Error: "+ex.getMessage();
			}
			if(responseString.contains("Error")) {
				errorOccurred = true;
				eventConfirmation.setConfirmationStatus("InputError");
				eventConfirmation.setConfirmationStatusExplanation(responseString);
			} 
		}
	}
	
	public void reserveVenue(EventInformation eventInformation) {
		
		if(!errorOccurred) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			URI venueReservationUrl = null;
			try {
				venueReservationUrl = new URI(venueReservationUrlString);
			} catch(Exception ex) {
				ex.printStackTrace();
			}

			VenueReservationRequest venueReservationRequest = 
					populateVenueReservationRequest(eventInformation);
			HttpEntity<VenueReservationRequest> venueReservationRequestEntity = 
					new HttpEntity<>(venueReservationRequest, headers);			
			ResponseEntity<VenueReservationResponse> venueReservationResponseEntity;
			String responseResult;
			try {
				venueReservationResponseEntity =
						restTemplate.postForEntity(venueReservationUrl, venueReservationRequestEntity, VenueReservationResponse.class);
				responseResult=venueReservationResponseEntity.getStatusCode().toString();
				VenueReservationResponse venueReservationResponse = venueReservationResponseEntity.getBody();
				eventConfirmation.setVenueReservationConfirmationNumber(venueReservationResponse.getVenueReservationConfirmationNumber());
				eventConfirmation.setVenueReservationStatus(venueReservationResponse.getVenueReservationStatus());
				eventConfirmation.setVenueReservationStatusExplanation(venueReservationResponse.getVenueReservationStatusExplanation());

				if(!(eventConfirmation.getVenueReservationStatus().equalsIgnoreCase(AppConstants.SUCCESS))) {
					errorOccurred=true;
				}
			} catch(Exception ex) {
				responseResult=ex.getClass().getSimpleName()+" -- "+ex.getMessage();
			}
			if(!(responseResult.equalsIgnoreCase(HttpStatus.OK.toString()))) {
				errorOccurred = true;
				eventConfirmation.setVenueReservationConfirmationNumber("N/A");
				eventConfirmation.setVenueReservationStatus(AppConstants.VENUE_CONNECT_FAILED);
				eventConfirmation.setVenueReservationStatusExplanation(responseResult);
			}
			eventConfirmation.setConfirmationStatus(eventConfirmation.getVenueReservationStatus());
		}
	}
	
	private VenueReservationRequest populateVenueReservationRequest(EventInformation eventInformation) {
		VenueReservationRequest venueReservationRequest = new VenueReservationRequest();

		venueReservationRequest.setEventCoordinatorEmail(eventInformation.getEventCoordinatorEmail());
		venueReservationRequest.setEventCoordinatorName(eventInformation.getEventCoordinatorName());
		venueReservationRequest.setEventDate(eventConfirmation.getEventDate());
		venueReservationRequest.setEventDescription(eventInformation.getEventDescription());
		venueReservationRequest.setEventVenue(eventInformation.getEventVenue());
		venueReservationRequest.setDepositAmount(eventInformation.getDepositAmount());
		venueReservationRequest.setPaymentCardForDeposit(eventInformation.getPaymentCardForDeposit());
		venueReservationRequest.setPaymentCardExpirationDate(eventInformation.getPaymentCardExpirationDate());
		venueReservationRequest.setPaymentCardCVCCode(eventInformation.getPaymentCardCVCCode());
		
		return venueReservationRequest;
	}
	
	private void persistEvent(EventInformation eventInformation) {
		if(!errorOccurred) {
			EventDetail eventDetail = populateEventDetail(eventInformation);
			String saveEventResult = saveEventToRepository(eventDetail);
			if(saveEventResult.contains("Error")) {
				errorOccurred = true;
				eventConfirmation.setConfirmationStatus(AppConstants.SAVE_ERROR);
				eventConfirmation.setConfirmationStatusExplanation(saveEventResult);
			} else {
				eventConfirmation.setConfirmationStatusExplanation("database save worked");
			}
		}
	
	}
	
	private String saveEventToRepository(EventDetail eventDetail) {
		String saveResult = "success";
		
		try {
			EventDetail returnEventDetail = eventRepository.save(eventDetail);
		} catch(Exception ex) {
			saveResult="Error: "+" --Exception Type: "+ex.getClass().getSimpleName()+" --Message: "+ex.getMessage();
			ex.printStackTrace();
		}
		
		return saveResult;
	}
	
	public void sendEmail(EventInformation eventInformation) {
		
		if(!errorOccurred) {
			EmailPackage emailPackage = populateEmailPackage(eventInformation);
			String emailResult = emailGateway.sendEmailToDistributionList(emailPackage);
			eventConfirmation.setConfirmationStatus(emailResult);
			eventConfirmation.setConfirmationStatusExplanation(emailResponseMap.get(emailResult));
		}
	}

	private EmailPackage populateEmailPackage(EventInformation eventInformation) {
		EmailPackage emailPackage = new EmailPackage();
		emailPackage.setEmailBody(eventInformation.getEmailBody());
		emailPackage.setEmailDsitributionList(eventInformation.getInvitationDistributionListEmail());
		emailPackage.setEmailSender(eventInformation.getEventCoordinatorEmail());
		emailPackage.setEmailSubject(eventInformation.getEmailSubject());
		return emailPackage;		
	}

	public void tweetIt(EventInformation eventInformation) {
		
		if(!errorOccurred) {
			try {
				Producer<String, SpecificRecord>  myProducer = kafkaProducerConfigHelper.getMyKafkaProducer();
				
				String tweetMsg = "Please join us from 9 AM to 5 PM on "
						+ eventConfirmation.getEventDate()
						+ ". "
						+ eventInformation.getEventDescription()
						+ "Click the Zoom link to join:"
						+ eventInformation.getZoomMeeting();
				
			    SpecificRecord tweetDto = (SpecificRecord) new TweetDto(eventInformation.getTwitterAccount(),eventInformation.getTwitterHashTags(),tweetMsg);

			    String producerKey = eventConfirmation.getConfirmationId();    
			    ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
			    		kafkaTopicTweet, producerKey, (SpecificRecord)tweetDto);

					 
			     myProducer.send(producerRecord);
			     
			     eventConfirmation.setConfirmationStatus(AppConstants.SUCCESS);
			     eventConfirmation.setConfirmationId(idManager.getUniqueConfirmationId());
			     eventConfirmation.setConfirmationStatusExplanation("Tweet completed");
			     eventConfirmation.setEventId(idManager.getUniqueEventId());
				
			} catch(Exception ex) {
				ex.printStackTrace();
				errorOccurred = true;
				eventConfirmation.setConfirmationStatus(AppConstants.TWEET_FAILED);
				eventConfirmation.setConfirmationStatusExplanation(ex.getMessage());
			}
			
		}
	}
	

}
