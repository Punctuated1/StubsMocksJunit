package myapp;

import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import myapp.generated.avro.TwitterEventFeedbackDto;

@Component("myKafkaConsumer")
@Profile({"test"})
public class MyMockKafkaConsumer implements MyKafkaConsumerInterface {

	public Consumer<String, TwitterEventFeedbackDto> getMyKafkaConsumer(Properties props) throws ConsumerException{
		try {
			Consumer<String, TwitterEventFeedbackDto> myMockConsumer = new MockConsumer<String, TwitterEventFeedbackDto>(OffsetResetStrategy.EARLIEST);
			return myMockConsumer; 
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new ConsumerException("create mock consumer failed");
		}
	}


}
