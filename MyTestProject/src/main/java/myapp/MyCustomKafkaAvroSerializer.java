package myapp;

import java.util.Map;

import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Value;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import myapp.generated.avro.TweetDto;

public class MyCustomKafkaAvroSerializer extends KafkaAvroSerializer {
	   @Value("${kafka.topic.tweet}")
	    private String kafkaTopicTweet;
	 	
	
    public MyCustomKafkaAvroSerializer() {
        super();
        super.schemaRegistry = prepareMockSchemaRegistryClient();

    }

    public MyCustomKafkaAvroSerializer(SchemaRegistryClient client) {
        super();
        super.schemaRegistry = prepareMockSchemaRegistryClient();
    }

    public MyCustomKafkaAvroSerializer(SchemaRegistryClient client, Map<String, ?> props) {
        super();
        super.schemaRegistry = prepareMockSchemaRegistryClient();
    }
    
    private MockSchemaRegistryClient prepareMockSchemaRegistryClient() {
        this.useSchemaId=1;
    	MockSchemaRegistryClient mockSchemaRegistryClient = new MockSchemaRegistryClient();
        final Schema eventTweetSchema = TweetDto.SCHEMA$;
        final ParsedSchema parsedSchema = new AvroSchema(eventTweetSchema);
        try {
        	int id = mockSchemaRegistryClient.register(kafkaTopicTweet+"-value", parsedSchema,1,1);
        	
        } catch(Exception ex) {
        	ex.printStackTrace();
        }
        return mockSchemaRegistryClient;
    }
}
