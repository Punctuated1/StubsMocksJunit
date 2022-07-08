package myapp;

import java.util.Properties;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import myapp.generated.avro.TweetDto;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;

@Component("myKafkaProducer")
@Profile({"prod","qa","int","dev"})
public class MyKafkaProducer implements MyKafkaProducerInterface {
	private KafkaProducer<String, SpecificRecord> myKafkaProducer;
	@Override
	public Producer<String, SpecificRecord> getMyKafkaProducer(Properties props) throws ProducerException {
		try {
				if(myKafkaProducer == null) {
					Class<Serializer<Object>> keySerializer
						= (Class<Serializer<Object>>) Class.forName(props.getProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
					Class<Serializer<Object>> valueSerializer
					= (Class<Serializer<Object>>) Class.forName(props.getProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
					myKafkaProducer = new KafkaProducer<String, SpecificRecord>(props);
				}
				return myKafkaProducer; 
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new ProducerException("create serializer failed");
		}
		
	}


}
