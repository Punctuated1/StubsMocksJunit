/**
 * 
 */
package myapp;

import java.util.Properties;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.Producer;

import myapp.generated.avro.TweetDto;

/**
 * @author hmt_9
 *
 */
public interface MyKafkaProducerInterface {
	public Producer<String, SpecificRecord> getMyKafkaProducer(Properties props) throws ProducerException;
}
