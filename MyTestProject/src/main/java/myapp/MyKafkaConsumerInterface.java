package myapp;

import java.util.Properties;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.Consumer;

import myapp.generated.avro.TwitterEventFeedbackDto;

public interface MyKafkaConsumerInterface {
	public Consumer<String, TwitterEventFeedbackDto> getMyKafkaConsumer(Properties props) throws ConsumerException;

}
