package myapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.concurrent.Future;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.SerializationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatchers;
import org.springframework.ws.test.client.ResponseCreators;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;

import com.example.consumingwebservice.wsdl.CheckVenueMeetsRequirmentsResponse;
import com.example.consumingwebservice.wsdl.VenueRequirementsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import myapp.generated.avro.TweetDto;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import myapp.generated.avro.TwitterEventFeedbackDto;



@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=SpringTestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
public class EventSchedulerTests {
	
	@MockBean private EventRepository eventRepository;
	@MockBean private EventFeedbackRepository eventFeedbackRepository;
	
	@MockBean private EmailGatewayInterface emailGateway;

    @Autowired
	private RestTemplate restTemplate;
    
    @Autowired
    private VenueRequirementsCheckerClient venueRequirementsCheckerClient;
    
    private MockRestServiceServer mockRestServer;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    private KafkaProducerConfigHelper kafkaProducerConfigHelper;
    
    private TestableMockProducer<String, SpecificRecord> myMockProducer;

    @Value("${venue.reservation.url}")
	private String venueReservationUrlString;
    
 	@Autowired
	private MyEventScheduler myEventScheduler;

	private EventInformation eventInformation = new EventInformation();	
	private HttpHeaders headers = new HttpHeaders();
	private URI venueReservationUrl = null;
	private String expectedEventDate = "2028-11-03";
	
    @Value("${kafka.topic.tweet}")
    private String kafkaTopicTweet;

	@Autowired
	@Qualifier("kafkaConsumerConfigHelper")
	private KafkaConsumerConfigHelper kafkaConsumerConfigHelper;
	
	private MockConsumer<String, TwitterEventFeedbackDto> mockConsumer;

	private Throwable pollException;
	
	private List<TwitterEventFeedbackRecord> updates;
	
    @Value("${kafka.consumer.topic.twitter}")
    private String kafkaTopicTwitterEventFeedback;

 	@Autowired
    private MyTwitterEventFeedbackProcessor eventFeedbackProcessor;
    
	@Autowired
	@Qualifier("idManager")
	private IdManagerInterface idManager;

    @Value("${exchange}")
    private String exchange;

    @Value("${queue}")
    private String queue;

	
	@BeforeAll
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			venueReservationUrl = new URI(venueReservationUrlString);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

    
    @BeforeEach
    public void init() {
		RestGatewaySupport gatewaySupport = new RestGatewaySupport();
		gatewaySupport.setRestTemplate(restTemplate);
		mockRestServer = MockRestServiceServer.createServer(gatewaySupport);
		try {
			myMockProducer = (TestableMockProducer<String, SpecificRecord>) kafkaProducerConfigHelper.getMyKafkaProducerInit();
			pollException = null;
			updates = new ArrayList<TwitterEventFeedbackRecord>();
			TwitterEventFeebackConfig twitterEventFeebackConfig =
					kafkaConsumerConfigHelper.getMyKafkaConsumerInit
						(re -> this.pollException = re, updates::add);
			mockConsumer = (MockConsumer<String,TwitterEventFeedbackDto>)twitterEventFeebackConfig.getConsumer();
		} catch(Exception ex) {
			ex.printStackTrace();
		}

    }

	public EventSchedulerTests() {
		//TODO Auto-generated constructor stub
	}

	@Test
	void negativeVenueRequirementsCheckerCapacity() {
		//arrange 

		populateEventInformation(10);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerCapacityRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerCapacityResponseString());
		eventInformation.setExpectedNumberAttendees(1000);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer
			.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(AppConstants.VENUE_REQ_CAPACITY_INSUFFICIENT,eventConfirmation.getConfirmationStatus()); 
		mockWebServiceServer.verify();
	}

	@Test
	void negativeVenueRequirementsBigScreen() {
		//arrange 
		
		populateEventInformation(10);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerBigScreenRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerBigScreenResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(true);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(AppConstants.VENUE_REQ_NO_BIG_SCREEN,eventConfirmation.getConfirmationStatus()); 
		mockWebServiceServer.verify();
	}

	@Test
	void negativeVenueRequirementsException() {
		//arrange 
		
		populateEventInformation(10);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreWhitespace(true);
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerExceptionPayloadString());
		eventInformation.setExpectedNumberAttendees(101);
		eventInformation.setLargeScreenNeeded(false);
		RuntimeException runtimeException = new RuntimeException("An exception occured.");

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withException(runtimeException));
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(AppConstants.VENUE_REQ_EXCEPTION_OCCURED,eventConfirmation.getConfirmationStatus()); 
		mockWebServiceServer.verify();
	}

	private String buildVenueRequirementsCheckerExceptionPayloadString() {
		String requestCapacityTooGreat = 
				"<checkVenueMeetsRequirments xmlns=\"http://soapy1\">"+
					"<venueRequirements>"+
						"<nameOfVenue>Junit test hall</nameOfVenue>"+
						"<expectedNumberAttendees>101</expectedNumberAttendees>"+
						"<largeScreenNeeded>false</largeScreenNeeded>"+
					"</venueRequirements>"+
				"</checkVenueMeetsRequirments>";
		return requestCapacityTooGreat;
	}
	
	private String buildVenueRequirementsCheckerCapacityRequestString() {
		String requestCapacityTooGreat = 
				"<checkVenueMeetsRequirments xmlns=\"http://soapy1\">"+
					"<venueRequirements>"+
						"<nameOfVenue>Junit test hall</nameOfVenue>"+
						"<expectedNumberAttendees>1000</expectedNumberAttendees>"+
						"<largeScreenNeeded>false</largeScreenNeeded>"+
					"</venueRequirements>"+
				"</checkVenueMeetsRequirments>";
		return requestCapacityTooGreat;
	}
	
	private String buildVenueRequirementsCheckerCapacityResponseString() {
		String responseCapacityTooGreat = 
				"<checkVenueMeetsRequirmentsResponse xmlns=\"http://soapy1\">"+
					"<checkVenueMeetsRequirmentsReturn>"+
						"<responseCode>"+AppConstants.VENUE_REQ_CAPACITY_INSUFFICIENT+"</responseCode> " +
						"<responseCodeExplanation>Because it is too small for the crowd</responseCodeExplanation>"+
					"</checkVenueMeetsRequirmentsReturn>"+
				"</checkVenueMeetsRequirmentsResponse>" ;
		return responseCapacityTooGreat;
	}
	
	private String buildVenueRequirementsCheckerBigScreenRequestString() {
		String requestCapacityTooGreat = 
				"<checkVenueMeetsRequirments xmlns=\"http://soapy1\">"+
					"<venueRequirements>"+
						"<nameOfVenue>Junit test hall</nameOfVenue>"+
						"<expectedNumberAttendees>100</expectedNumberAttendees>"+
						"<largeScreenNeeded>true</largeScreenNeeded>"+
					"</venueRequirements>"+
				"</checkVenueMeetsRequirments>";
		return requestCapacityTooGreat;
	}
	
	private String buildVenueRequirementsCheckerBigScreenResponseString() {
		String responseCapacityTooGreat = 
				"<checkVenueMeetsRequirmentsResponse xmlns=\"http://soapy1\">"+
					"<checkVenueMeetsRequirmentsReturn>"+
						"<responseCode>"+AppConstants.VENUE_REQ_NO_BIG_SCREEN+"</responseCode> " +
						"<responseCodeExplanation>No big screen on stage.</responseCodeExplanation>"+
					"</checkVenueMeetsRequirmentsReturn>"+
				"</checkVenueMeetsRequirmentsResponse>" ;
		return responseCapacityTooGreat;
	}

	@Test
	void negativeDateInputLowerBound() {
		//arrange 
		populateEventInformation(0);

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertTrue(eventConfirmation.getConfirmationStatus().contains("InputError") 
				&& eventConfirmation.getConfirmationStatusExplanation().contains("must be greater than zero and less than"));

		mockWebServiceServer.verify();
	}

	@Test
	void negativeDateInputUpperBound() {
		//arrange 
		populateEventInformation(31);

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertTrue(eventConfirmation.getConfirmationStatus().contains("InputError") 
				&& eventConfirmation.getConfirmationStatusExplanation().contains("must be greater than zero and less than"));

		mockWebServiceServer.verify();
	}

	@Test
	public void positiveDatePlus3() {
		//arrange 
		populateEventInformation(3);
		expectedEventDate = "2028-11-03";

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponse();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		// setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		String emailResult=AppConstants.SUCCESS;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);
		

		String tweetMsg = "Please join us from 9 AM to 5 PM on "
				+ expectedEventDate	+ ". " + eventInformation.getEventDescription()
				+ "Click the Zoom link to join:" + eventInformation.getZoomMeeting();
		
	    SpecificRecord tweetDto = (SpecificRecord) new TweetDto(eventInformation.getTwitterAccount(),eventInformation.getTwitterHashTags(),tweetMsg);
	    String producerKey = "CID-321";    
	    ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
	    		kafkaTopicTweet, producerKey, (SpecificRecord)tweetDto);
	    Future<RecordMetadata> record = myMockProducer.send(producerRecord);
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		mockRestServer.verify();

		//Assert
		assertEquals(expectedEventDate,eventConfirmation.getEventDate()); 
		
		mockWebServiceServer.verify();
	}

	@Test
	void positiveDatePlus10() {
		//arrange 
		populateEventInformation(10);
		expectedEventDate = "2028-11-14";
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponse();

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		String emailResult=AppConstants.SUCCESS;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);
		
		String tweetMsg = "Please join us from 9 AM to 5 PM on "
				+ expectedEventDate	+ ". " + eventInformation.getEventDescription()
				+ "Click the Zoom link to join:" + eventInformation.getZoomMeeting();
		
	    SpecificRecord tweetDto = (SpecificRecord) new TweetDto(eventInformation.getTwitterAccount(),eventInformation.getTwitterHashTags(),tweetMsg);
	    String producerKey = "CID-321";    
	    ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
	    		kafkaTopicTweet, producerKey, (SpecificRecord)tweetDto);
	    Future<RecordMetadata> record = myMockProducer.send(producerRecord);

		// setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(expectedEventDate,eventConfirmation.getEventDate()); 
		mockWebServiceServer.verify();
	}

	@Test
	void positiveDatePlus30() {
		//arrange 
		populateEventInformation(30);
		expectedEventDate = "2028-12-14";

		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponse();
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		String emailResult=AppConstants.SUCCESS;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);
		
		String tweetMsg = "Please join us from 9 AM to 5 PM on "
				+ expectedEventDate	+ ". " + eventInformation.getEventDescription()
				+ "Click the Zoom link to join:" + eventInformation.getZoomMeeting();
		
		SpecificRecord tweetDto = (SpecificRecord) new TweetDto(eventInformation.getTwitterAccount(),eventInformation.getTwitterHashTags(),tweetMsg);
	    String producerKey = "CID-321";    
	    ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
	    		kafkaTopicTweet, producerKey, (SpecificRecord)tweetDto);
	    Future<RecordMetadata> record = myMockProducer.send(producerRecord);

		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(expectedEventDate,eventConfirmation.getEventDate()); 

		mockWebServiceServer.verify();
	}

	@Test
	void negativeReserveVenueConnectNotSuccessful() {
		//arrange 

		//create request entity
		expectedEventDate = "2028-11-14";
		populateEventInformationDuplicate(10); 
		
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponse();
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.REQUEST_TIMEOUT)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.VENUE_CONNECT_FAILED);

		mockWebServiceServer.verify();
	}

	@Test
	void negativeReserveVenueDateBookedAlready() {
		//arrange 

		//create request entity
		expectedEventDate = "2028-11-14";
		populateEventInformationDuplicate(10); 
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponseDup();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(AppConstants.VENUE_ALREADY_BOOKED_FOR_DATE, eventConfirmation.getVenueReservationStatus());

		mockWebServiceServer.verify();
	}

	@Test
	void negativeReserveVenueInvalidCreditCard() {
		//arrange 

		//create request entity
		expectedEventDate = "2028-11-14";
		populateEventInformation(10); 
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponseInvalidPaymentCard();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(AppConstants.VENUE_PAYMENT_CARD_INVALID, eventConfirmation.getVenueReservationStatus());
		mockWebServiceServer.verify();
	}

	@Test
	void negativeReserveVenueInvalidCreditCardCvc() {
		//arrange 

		//create request entity
		expectedEventDate = "2028-11-14";
		populateEventInformation(10); 
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponseInvalidPaymentCardCvc();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(AppConstants.VENUE_PAYMENT_CARD_CVC_INVALID, eventConfirmation.getVenueReservationStatus());

		mockWebServiceServer.verify();
	}

	@Test
	void negativeReserveVenuePaymentCardExpired() {
		//arrange 

		//create request entity
		expectedEventDate = "2028-11-14";
		populateEventInformation(10); 
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponsePaymentCardExpired();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(AppConstants.VENUE_PAYMENT_CARD_EXPIRED, eventConfirmation.getVenueReservationStatus());

		mockWebServiceServer.verify();
	}

	@Test
	void negativeReserveVenueDepositAmtExceedsAvailableCredit() {
		//arrange 

		//create request entity
		expectedEventDate = "2028-11-14";
		populateEventInformation(10); 
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponseVenueDepositAmtExceedsAvailableCredit();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(AppConstants.VENUE_DEPOSIT_AMT_EXCEEDS_AVAILABLE_CREDIT, eventConfirmation.getVenueReservationStatus());

		mockWebServiceServer.verify();
	}

	@Test
	void positiveReserveVenue() {
		//arrange 

		//create request entity
		expectedEventDate = "2028-11-14";
		populateEventInformationDuplicate(10); 
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponse();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		String emailResult=AppConstants.SUCCESS;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);

		String tweetMsg = "Please join us from 9 AM to 5 PM on "
				+ expectedEventDate	+ ". " + eventInformation.getEventDescription()
				+ "Click the Zoom link to join:" + eventInformation.getZoomMeeting();
		
	    SpecificRecord tweetDto = (SpecificRecord) new TweetDto(eventInformation.getTwitterAccount(),eventInformation.getTwitterHashTags(),tweetMsg);
	    String producerKey = "CID-321";    
	    ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
	    		kafkaTopicTweet, producerKey, (SpecificRecord)tweetDto);
	    Future<RecordMetadata> record = myMockProducer.send(producerRecord);

		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.SUCCESS);

		mockWebServiceServer.verify();
	}

	@Test
	void negativeSaveEventRepository() {
		//arrange 
		//Create Response object
		populateEventInformation(30);
		expectedEventDate = "2028-12-14";

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponse();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup repository expectations
		when(eventRepository.save(any(EventDetail.class))) 
				.thenThrow(IllegalArgumentException.class);
		
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertTrue(eventConfirmation.getConfirmationStatus().contains(AppConstants.SAVE_ERROR) 
				&& eventConfirmation.getConfirmationStatusExplanation().contains("IllegalArgumentException"));

		mockWebServiceServer.verify();
	}

	@Test
	void positiveSaveEventRepository() {
		//arrange 

		populateEventInformation(30);
		expectedEventDate = "2028-12-14";
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponse();
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);

		String emailResult=AppConstants.SUCCESS;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);
		String tweetMsg = "Please join us from 9 AM to 5 PM on "
				+ expectedEventDate	+ ". " + eventInformation.getEventDescription()
				+ "Click the Zoom link to join:" + eventInformation.getZoomMeeting();
		
	    SpecificRecord tweetDto = (SpecificRecord) new TweetDto(eventInformation.getTwitterAccount(),eventInformation.getTwitterHashTags(),tweetMsg);
	    String producerKey = "CID-321";    
	    ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
	    		kafkaTopicTweet, producerKey, (SpecificRecord)tweetDto);
	    Future<RecordMetadata> record = myMockProducer.send(producerRecord);

		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.SUCCESS);

		mockWebServiceServer.verify();
	}

	@Test
	void negativeSendEmailInvalidEmailDistributionList() {
		//arrange 
		arrangeBaseline();

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		
		String emailResult=AppConstants.EMAIL_INVALID_DISTRIBUTION_LIST;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);

		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.EMAIL_INVALID_DISTRIBUTION_LIST);

		mockWebServiceServer.verify();
	}
	
	@Test
	void negativeSendEmailInvalidEmailSenderAddress() {
		//arrange 
		arrangeBaseline();

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		String emailResult=AppConstants.EMAIL_INVALID_SENDER_ADDRESS;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);

		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.EMAIL_INVALID_SENDER_ADDRESS);
		
		mockWebServiceServer.verify();
	}

	@Test
	void negativeSendEmailInvalidEmailNoBody() {
		//arrange 
		arrangeBaseline();
		
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		String emailResult=AppConstants.EMAIL_NO_BODY;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);

		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.EMAIL_NO_BODY);
		
		mockWebServiceServer.verify();
	}
	
	@Test
	void negativeSendEmailInvalidEmailNoSubject() {
		//arrange 
		arrangeBaseline();

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		String emailResult=AppConstants.EMAIL_NO_SUBJECT;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);

		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		
		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.EMAIL_NO_SUBJECT);
		
		mockWebServiceServer.verify();
	}

	
	
	@Test
	void negativeSendEmailInvalidEmailSubjectTooLong() {
		//arrange 
		arrangeBaseline();

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		String emailResult=AppConstants.EMAIL_SUBJECT_TOO_LONG;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);

		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);

		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.EMAIL_SUBJECT_TOO_LONG);
		
		mockWebServiceServer.verify();
	}

	@Test
	void negativeTweetSchemaInvalid() {
		//arrange 
		arrangeBaseline();
		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		String emailResult=AppConstants.SUCCESS;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);
		
		String tweetMsg = "Please join us from 9 AM to 5 PM on "
				+ expectedEventDate	+ ". " + eventInformation.getEventDescription()
				+ "Click the Zoom link to join:" + eventInformation.getZoomMeeting();
		
	    SpecificRecord tweetDto = (SpecificRecord) new TweetDto(eventInformation.getTwitterAccount(),eventInformation.getTwitterHashTags(),tweetMsg);
	    String producerKey = "CID-321";    
	    ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
	    		kafkaTopicTweet, producerKey, (SpecificRecord)tweetDto);
	    Future<RecordMetadata> record = myMockProducer.send(producerRecord);

	    myMockProducer.setThrowException(true);

	    //Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		//Assert
		System.out.println("Neg Tweet: "+eventConfirmation.getConfirmationStatus()+ " -- "+eventConfirmation.getConfirmationStatusExplanation());
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.TWEET_FAILED);
		mockWebServiceServer.verify();
	}

	@Test
	void positiveTweet() {
		//arrange 
		arrangeBaseline();

		MockWebServiceServer mockWebServiceServer = MockWebServiceServer.createServer(venueRequirementsCheckerClient);
		Source requestPayload = new StringSource(buildVenueRequirementsCheckerSuccessfulRequestString());
		Source responsePayload = new StringSource(buildVenueRequirementsCheckerSuccessfulResponseString());
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);

		mockWebServiceServer.expect(RequestMatchers.payload(requestPayload))
			.andRespond(ResponseCreators.withPayload(responsePayload));
		
		String emailResult=AppConstants.SUCCESS;
		when(emailGateway.sendEmailToDistributionList(Mockito.any(EmailPackage.class)))
			.thenReturn(emailResult);
		
		String tweetMsg = "Please join us from 9 AM to 5 PM on "
				+ expectedEventDate	+ ". " + eventInformation.getEventDescription()
				+ "Click the Zoom link to join:" + eventInformation.getZoomMeeting();
		
	    SpecificRecord tweetDto = (SpecificRecord) new TweetDto(eventInformation.getTwitterAccount(),eventInformation.getTwitterHashTags(),tweetMsg);
	    String producerKey = "CID-321";    
	    ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
	    		kafkaTopicTweet, producerKey, (SpecificRecord)tweetDto);
	    Future<RecordMetadata> record = myMockProducer.send(producerRecord);
		//Act
		EventConfirmation eventConfirmation = myEventScheduler.scheduleMyEvent(eventInformation);
		//Assert
		assertEquals(eventConfirmation.getConfirmationStatus(),AppConstants.SUCCESS);
		mockWebServiceServer.verify();
	}

	@Test
	void positiveConsumeTwitterEventFeedback() {
		//given 
		mockConsumer.schedulePollTask(() -> {
			mockConsumer.rebalance(Collections.singleton(new TopicPartition(kafkaTopicTwitterEventFeedback, 0)));
			mockConsumer.addRecord(record(kafkaTopicTwitterEventFeedback,0,"TweetforEvent-"+idManager.getUniqueEventId()+"-1001"));
		});
		mockConsumer.schedulePollTask(() -> eventFeedbackProcessor.stop());
		
		HashMap<TopicPartition, Long> startOffsets = new HashMap<TopicPartition, Long>();
		TopicPartition tp = new TopicPartition(kafkaTopicTwitterEventFeedback, 0);
		startOffsets.put(tp, 0L);
		mockConsumer.updateBeginningOffsets(startOffsets);
		FeedbackDetail returnFeedbackDetail = new FeedbackDetail();
		returnFeedbackDetail.setId(idManager.getUniqueFeedbackId());
		returnFeedbackDetail.setMessageValue("{\"eventId\": \"EID-123\", \"twitterAccount\": \"JUnitEventsFan@testsrus.com\", \"twitterHashTags\": \"#ILLBETHERE\", \"twitterMessage\": \"Will be there on Zoom.\"}");

		when(eventFeedbackRepository.save(Mockito.any(FeedbackDetail.class))) 
			.thenReturn(returnFeedbackDetail);
		
		//when
		eventFeedbackProcessor.consumeIt();
		
		//then
		assertThat(updates).hasSize(1);
		assertThat(mockConsumer.closed()).isTrue();
		
	}

	@Test
	void negativeConsumeTwitterEventFeedbackExceptionHandledCorrectly() {
		//given 
		mockConsumer.schedulePollTask(() -> mockConsumer.setPollException(new KafkaException("poll exception")));
		mockConsumer.schedulePollTask(() -> eventFeedbackProcessor.stop());
		
		HashMap<TopicPartition, Long> startOffsets = new HashMap<TopicPartition, Long>();
		TopicPartition tp = new TopicPartition(kafkaTopicTwitterEventFeedback, 0);
		startOffsets.put(tp, 0L);
		mockConsumer.updateBeginningOffsets(startOffsets);
		FeedbackDetail returnFeedbackDetail = new FeedbackDetail();
		returnFeedbackDetail.setId(idManager.getUniqueFeedbackId());
		returnFeedbackDetail.setMessageValue("{\"eventId\": \"EID-123\", \"twitterAccount\": \"JUnitEventsFan@testsrus.com\", \"twitterHashTags\": \"#ILLBETHERE\", \"twitterMessage\": \"Will be there on Zoom.\"}");

		when(eventFeedbackRepository.save(Mockito.any(FeedbackDetail.class))) 
			.thenReturn(returnFeedbackDetail);
		
		//when
		eventFeedbackProcessor.consumeIt();
		
		//then
		assertThat(pollException).isInstanceOf(KafkaException.class).hasMessage("poll exception");
		assertThat(mockConsumer.closed()).isTrue();
		
	}

	@Test
	void negativeConsumeTwitterEventFeedbackPersistFailed() {
		//given 
		mockConsumer.schedulePollTask(() -> {
			mockConsumer.rebalance(Collections.singleton(new TopicPartition(kafkaTopicTwitterEventFeedback, 0)));
			mockConsumer.addRecord(record(kafkaTopicTwitterEventFeedback,0,"TweetforEvent-"+idManager.getUniqueEventId()+"-1001"));
		});
		mockConsumer.schedulePollTask(() -> eventFeedbackProcessor.stop());
		
		HashMap<TopicPartition, Long> startOffsets = new HashMap<TopicPartition, Long>();
		TopicPartition tp = new TopicPartition(kafkaTopicTwitterEventFeedback, 0);
		startOffsets.put(tp, 0L);
		mockConsumer.updateBeginningOffsets(startOffsets);
		FeedbackDetail returnFeedbackDetail = new FeedbackDetail();
		returnFeedbackDetail.setId(idManager.getUniqueFeedbackId());
		returnFeedbackDetail.setMessageValue("{\"eventId\": \"EID-123\", \"twitterAccount\": \"JUnitEventsFan@testsrus.com\", \"twitterHashTags\": \"#ILLBETHERE\", \"twitterMessage\": \"Will be there on Zoom.\"}");

		when(eventFeedbackRepository.save(Mockito.any(FeedbackDetail.class))) 
			.thenThrow(IllegalArgumentException.class);
		
		//when
		eventFeedbackProcessor.consumeIt();
		
		//then
		assertThat(pollException).isInstanceOf(FeedbackRepoException.class)
			.hasMessage("feedbackrepo operation failed");
		assertThat(mockConsumer.closed()).isTrue();
		
	}




	private ConsumerRecord<String, TwitterEventFeedbackDto > record(String topic, int partition, String key) {
		TwitterEventFeedbackDto feedbackDto = new TwitterEventFeedbackDto(idManager.getUniqueEventId(),
				"JUnitEventsFan@testsrus.com",
				"#ILLBETHERE",
				"Will be there on Zoom.");
		return new ConsumerRecord<>(topic, partition, 0, key, feedbackDto);
	}

	private void arrangeBaseline() {
		populateEventInformation(30);
		expectedEventDate = "2028-12-14";
		//Create Response object
		VenueReservationResponse venueReservationResponse = populateVenueReservationResponse();
		
		//set mock expectations for venue web service
		try {
			mockRestServer.expect(ExpectedCount.once(),
				requestTo(venueReservationUrl))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(mapper.writeValueAsString(venueReservationResponse)));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		//setup mock repository expectation since we pass the date check
		EventDetail returnEventDetail = populateReturnEventDetail();
		when(eventRepository.save(Mockito.any(EventDetail.class))) 
			.thenReturn(returnEventDetail);
		
	}

	private void populateEventInformation(long eventBusinessDaysInFuture) {
		eventInformation.setEventDescription("Junit testing summit");
		eventInformation.setEventVenue("Junit test hall");
		eventInformation.setDepositAmount(new BigDecimal("100.00"));
		eventInformation.setEventBusinessDaysInFuture(eventBusinessDaysInFuture);
		eventInformation.setEventCoordinatorEmail(AppConstants.TWEET_ACCOUNT);
		eventInformation.setEventCoordinatorName("Q Tester");
		eventInformation.setInvitationDistributionListEmail("rrtesters@acme-testing.com");
		eventInformation.setPaymentCardForDeposit("1234123412341234");
		eventInformation.setPaymentCardExpirationDate("12/28");
		eventInformation.setPaymentCardCVCCode("123");
		eventInformation.setExpectedNumberAttendees(100);
		eventInformation.setLargeScreenNeeded(false);
		eventInformation.setTwitterAccount(AppConstants.TWEET_ACCOUNT);
		eventInformation.setTwitterHashTags(AppConstants.TWEET_HASHTAG);
		eventInformation.setZoomMeeting("https://us02web.zoom.us/j/123?pwd=SmokeAndMirrors");
	}

	private void populateEventInformationDuplicate(long eventBusinessDaysInFuture) {
		populateEventInformation(eventBusinessDaysInFuture);
		eventInformation.setEventVenue("Junit test hall duplicate");
	}
	
	private VenueReservationResponse populateVenueReservationResponse() {
		VenueReservationResponse venueReservationResponse = new VenueReservationResponse();
		venueReservationResponse.setVenueReservationConfirmationNumber(expectedEventDate+"-VC001");
		venueReservationResponse.setVenueReservationStatus(AppConstants.SUCCESS);
		venueReservationResponse.setVenueReservationStatusExplanation("Your venue, "+eventInformation.getEventVenue()+", is confirmed for , "+expectedEventDate );
		return venueReservationResponse;
	}

	private VenueReservationResponse populateVenueReservationResponseDup() {
		VenueReservationResponse venueReservationResponse = new VenueReservationResponse();
		venueReservationResponse.setVenueReservationConfirmationNumber("N/A");
		venueReservationResponse.setVenueReservationStatus(AppConstants.VENUE_ALREADY_BOOKED_FOR_DATE);
		venueReservationResponse.setVenueReservationStatusExplanation("Your venue, "+eventInformation.getEventVenue()+", is not available for the requested date, "+expectedEventDate );
		return venueReservationResponse;
	}

	private VenueReservationResponse populateVenueReservationResponseInvalidPaymentCard() {
		VenueReservationResponse venueReservationResponse = new VenueReservationResponse();
		venueReservationResponse.setVenueReservationConfirmationNumber("N/A");
		venueReservationResponse.setVenueReservationStatus(AppConstants.VENUE_PAYMENT_CARD_INVALID);
		venueReservationResponse.setVenueReservationStatusExplanation("Your credit card is not valid." );
		return venueReservationResponse;
	}

	private VenueReservationResponse populateVenueReservationResponseInvalidPaymentCardCvc() {
		VenueReservationResponse venueReservationResponse = new VenueReservationResponse();
		venueReservationResponse.setVenueReservationConfirmationNumber("N/A");
		venueReservationResponse.setVenueReservationStatus(AppConstants.VENUE_PAYMENT_CARD_CVC_INVALID);
		venueReservationResponse.setVenueReservationStatusExplanation("Your credit card CVC Code is not valid." );
		return venueReservationResponse;
	}

	private VenueReservationResponse populateVenueReservationResponsePaymentCardExpired() {
		VenueReservationResponse venueReservationResponse = new VenueReservationResponse();
		venueReservationResponse.setVenueReservationConfirmationNumber("N/A");
		venueReservationResponse.setVenueReservationStatus(AppConstants.VENUE_PAYMENT_CARD_EXPIRED);
		venueReservationResponse.setVenueReservationStatusExplanation("Your credit card is expired." );
		return venueReservationResponse;
	}

	private VenueReservationResponse populateVenueReservationResponseVenueDepositAmtExceedsAvailableCredit() {
		VenueReservationResponse venueReservationResponse = new VenueReservationResponse();
		venueReservationResponse.setVenueReservationConfirmationNumber("N/A");
		venueReservationResponse.setVenueReservationStatus(AppConstants.VENUE_DEPOSIT_AMT_EXCEEDS_AVAILABLE_CREDIT);
		venueReservationResponse.setVenueReservationStatusExplanation("The charge amount exceeds available credit." );
		return venueReservationResponse;
	}
	
	private String buildVenueRequirementsCheckerSuccessfulRequestString() {
		String requestCapacityTooGreat = 
				"<checkVenueMeetsRequirments xmlns=\"http://soapy1\">"+
					"<venueRequirements>"+
						"<nameOfVenue>"+eventInformation.getEventVenue()+"</nameOfVenue>"+
						"<expectedNumberAttendees>100</expectedNumberAttendees>"+
						"<largeScreenNeeded>false</largeScreenNeeded>"+
					"</venueRequirements>"+
				"</checkVenueMeetsRequirments>";
		return requestCapacityTooGreat;
	}

	private String buildVenueRequirementsCheckerSuccessfulResponseString() {
		String responseCapacityTooGreat = 
				"<checkVenueMeetsRequirmentsResponse xmlns=\"http://soapy1\">"+
					"<checkVenueMeetsRequirmentsReturn>"+
						"<responseCode>"+AppConstants.SUCCESS+"</responseCode> " +
						"<responseCodeExplanation>Because it is too small for the crowd</responseCodeExplanation>"+
					"</checkVenueMeetsRequirmentsReturn>"+
				"</checkVenueMeetsRequirmentsResponse>" ;
		return responseCapacityTooGreat;
	}
		
	private EventDetail populateReturnEventDetail() {
			EventDetail edt = new EventDetail();
			edt.setEventCoordinatorEmail(eventInformation.getEventCoordinatorEmail());
			edt.setEventCoordinatorName(eventInformation.getEventCoordinatorName());
			edt.setEventDate(expectedEventDate);
			edt.setEventDescription(eventInformation.getEventDescription());
			edt.setEventId(expectedEventDate+"-001");
			edt.setEventVenue(eventInformation.getEventVenue());
			edt.setInvitationDistributionListEmail(eventInformation.getInvitationDistributionListEmail());
			edt.setScheduledDate(expectedEventDate);
			return edt;
	}
}
