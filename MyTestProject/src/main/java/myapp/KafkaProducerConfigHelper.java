package myapp;

import java.util.Properties;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;

@Service
public class KafkaProducerConfigHelper {

	@Autowired
	@Qualifier("myKafkaProducer")
	private MyKafkaProducerInterface myKafkaProducer;

	@Value("${kafka.bootstrap.server}")
	private String kafkaBootstrapServer;

    @Value("${kafka.ack.config}") 
    private String kafkaAckConfig; 

    @Value("${kafka.retries.config}")
    private int kafkaRetriesConfig;

    @Value("${kafka.batch.size.config}")
    private int kafkaBatchSizeConfig;

    @Value("${kafka.linger.ms.config}")
    private int kafkaLingerMsConfig;


    @Value("${kafka.buffer.memory.config}")
    private long kafkaBufferMemoryConfig;

    @Value("${kafka.producer.tweet.key.serializer.class.config}")
    private String kafkaProducerTweetKeySerializerClassConfig;

    @Value("${kafka.producer.tweet.value.serializer.class.config}")
    private String kafkaProducerTweetValueSerializerClassConfig;

    @Value("${kafka.auto.register.schemas}")
    private boolean kafkaAutoRegisterSchemas;

    @Value("${kafka.schema.registry.url}")
    private String kafkaSchemaRegistryUrl;
	
    private Producer<String, SpecificRecord> myProducer;
	
	public Producer<String, SpecificRecord> getMyKafkaProducerInit() throws Exception{
		Properties props = setupProducerProperties();
		try {
			myProducer =  myKafkaProducer.getMyKafkaProducer(props);
			return myProducer;
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new Exception("Create Producer Failer, see stack trace.");
		}
		
	}

	public Producer<String, SpecificRecord> getMyKafkaProducer() throws Exception{
		if(myProducer == null) {
			myProducer=getMyKafkaProducerInit();
		}
		return myProducer;
	}

	private Properties setupProducerProperties() {

		Properties props = new Properties();
		
		   //Assign bootstrap server id(s)
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
	    
	    //Set acknowledgements for producer requests.      
	    props.put(ProducerConfig.ACKS_CONFIG, kafkaAckConfig);
	    
	    //If the request fails, the producer can automatically retry,
	    props.put(ProducerConfig.RETRIES_CONFIG, kafkaRetriesConfig);
	    
	    //Specify buffer size in config
	    props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaBatchSizeConfig);
	    
	    //Reduce the no of requests less than 0   
	    props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaLingerMsConfig);
	    
	    //The buffer.memory controls the total amount of memory available to the producer for buffering.   
	    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaBufferMemoryConfig);
	    
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerTweetKeySerializerClassConfig);

	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerTweetValueSerializerClassConfig);
	    
	    props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, kafkaAutoRegisterSchemas);
	    
	    props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaSchemaRegistryUrl);
	    props.put(AbstractKafkaSchemaSerDeConfig.USE_SCHEMA_ID, "1");
	    
	    return props;
	}

}
