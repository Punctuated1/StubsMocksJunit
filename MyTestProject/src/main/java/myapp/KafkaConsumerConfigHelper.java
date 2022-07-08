package myapp;

import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import myapp.generated.avro.TwitterEventFeedbackDto;

@Component("kafkaConsumerConfigHelper")
public class KafkaConsumerConfigHelper {

	private TwitterEventFeebackConfig twitterEventFeebackConfig;

    @Autowired
	@Qualifier("myKafkaConsumer")
	private MyKafkaConsumerInterface myKafkaConsumer;

	@Value("${kafka.bootstrap.server}")
	private String kafkaBootstrapServer;

    @Value("${kafka.consumer.twittereventfeedback.key.deserializer.class.config}")
    private String kafkaConsumerTitterEventtFeedbackKeyDeserializerClassConfig;

    @Value("${kafka.consumer.group.id}")
    private String kafkaConsumerGroupId;

    @Value("${kafka.consumer.auto.offset.reset}")
    private String kafkaConsumerAutoOffsetReset;
    
    @Value("${kafka.consumer.enable.auto.commit.config}")
    private String kafkaConsumerEnableAutoCommitConfig;

    @Value("${kafka.consumer.auto.commit.interval.ms.config}")
    private String kafkaConsumerAutoCommitIntervalMsConfig;
    
    @Value("${kafka.auto.register.schemas}")
    private boolean kafkaAutoRegisterSchemas;

    @Value("${kafka.schema.registry.url}")
    private String kafkaSchemaRegistryUrl;
    
    @Value("${kafka.consumer.specific.avro.reader.config}")
    private String kafkaConsumerSpecificAvroReaderConfig;
	
	public TwitterEventFeebackConfig getMyKafkaConsumerInit(java.util.function.Consumer<Throwable> exceptionConsumer,
			java.util.function.Consumer<TwitterEventFeedbackRecord> twitterEventFeedbackRecordConsumer) throws ConsumerException{
		Properties props = setupConsumerProperties();

		try {
			Consumer<String, TwitterEventFeedbackDto> myConsumer =  myKafkaConsumer.getMyKafkaConsumer(props);
			twitterEventFeebackConfig = new TwitterEventFeebackConfig();
			twitterEventFeebackConfig.setConsumer(myConsumer);
			twitterEventFeebackConfig.setExceptionConsumer(exceptionConsumer);
			twitterEventFeebackConfig.setTwitterEventFeedbackRecordConsumer(twitterEventFeedbackRecordConsumer);
			return twitterEventFeebackConfig;
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new ConsumerException("Create Producer Failer, see stack trace.");
		}
		
	}

	public TwitterEventFeebackConfig getMyKafkaConsumer() throws ConsumerException{
		if(twitterEventFeebackConfig == null) {
			twitterEventFeebackConfig=getMyKafkaConsumerInit(null, null);
		}
		return twitterEventFeebackConfig;
	}
	
	private Properties setupConsumerProperties() {

		Properties props = new Properties();
		
		   //Assign bootstrap server id(s)
	    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);

	    props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupId);
	    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,kafkaConsumerTitterEventtFeedbackKeyDeserializerClassConfig);
	    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
	    		 KafkaAvroDeserializer.class.getName());

	    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConsumerEnableAutoCommitConfig);
	    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, kafkaConsumerAutoCommitIntervalMsConfig);
	    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerAutoOffsetReset);
	    props.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, kafkaAutoRegisterSchemas);
    
	    props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaSchemaRegistryUrl);
	    props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, kafkaConsumerSpecificAvroReaderConfig);
		
	    return props;
	}
	
	
}
