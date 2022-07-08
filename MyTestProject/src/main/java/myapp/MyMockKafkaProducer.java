package myapp;

import java.util.Properties;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("myKafkaProducer")
@Profile({"test"})
public class MyMockKafkaProducer implements MyKafkaProducerInterface {

	@SuppressWarnings("deprecation")
	public Producer<String, SpecificRecord> getMyKafkaProducer(Properties props) throws ProducerException{
		try {
			Class<Serializer<Object>> keySerializerClass
				= (Class<Serializer<Object>>) Class.forName(props.getProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
			Class<Serializer<Object>> valueSerializerClass
			= (Class<Serializer<Object>>) Class.forName(props.getProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
			
			Serializer keySerializer = (Serializer) keySerializerClass.newInstance();
			Serializer valueSerializer = (Serializer) valueSerializerClass.newInstance();
			
			Producer<String, SpecificRecord> myMockProducer = new TestableMockProducer<String, SpecificRecord>(true, 
					keySerializer, valueSerializer);
			return myMockProducer; 
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new ProducerException("create producer failed");
		}
	}


}
