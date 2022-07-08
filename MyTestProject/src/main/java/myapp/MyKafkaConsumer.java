package myapp;

import java.util.Properties;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import myapp.generated.avro.TwitterEventFeedbackDto;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;

@Component("myKafkaConsumer")
@Profile({"prod","qa","int","dev"})
public class MyKafkaConsumer implements MyKafkaConsumerInterface {
	private Consumer<String, TwitterEventFeedbackDto> myKafkaConsumer;
	@Override
	public Consumer<String, TwitterEventFeedbackDto> getMyKafkaConsumer(Properties props) throws ConsumerException {
		try {
				if(myKafkaConsumer == null) {
					myKafkaConsumer = new KafkaConsumer<String, TwitterEventFeedbackDto>(props);
				}
				return myKafkaConsumer; 
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new ConsumerException("create serializer failed");
		}
		
	}


}
