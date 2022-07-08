package myapp;

import java.util.Collections;
import java.util.stream.StreamSupport;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import myapp.generated.avro.TwitterEventFeedbackDto;

@Service
public class MyTwitterEventFeedbackProcessor {
	
	@Autowired 
	private EventFeedbackRepository eventFeedbackRepository;

	@Autowired
	@Qualifier("idManager")
	private IdManagerInterface idManager;
	
	@Autowired
	@Qualifier("kafkaConsumerConfigHelper")
	private KafkaConsumerConfigHelper kafkaConsumerConfigHelper;

    @Value("${kafka.consumer.topic.twitter}")
    private String kafkaTopicTwitterEventFeedback;
    
    @Value("${exchange}")
    private String exchange;

    @Value("${queue}")
    private String queue;
    
	private Consumer<String, TwitterEventFeedbackDto> consumer;
    private java.util.function.Consumer<Throwable> exceptionConsumer;
    private java.util.function.Consumer<TwitterEventFeedbackRecord> twitterEventFeedbackRecordConsumer;
	
	
    @Value("${kafka.consumer.topic.twitter}")
    private String kafkaConsumerTopicTwitter;
    
    public MyTwitterEventFeedbackProcessor() {
   
    }

    void consumeIt() {
    	try {
    		TwitterEventFeebackConfig twitterEventFeebackConfig = kafkaConsumerConfigHelper.getMyKafkaConsumer();
    		this.consumer=twitterEventFeebackConfig.getConsumer();
    		this.exceptionConsumer=twitterEventFeebackConfig.getExceptionConsumer();
    		this.twitterEventFeedbackRecordConsumer=twitterEventFeebackConfig.getTwitterEventFeedbackRecordConsumer();
    		consumeTwitterEventFeedback(() -> 
    			this.consumer.subscribe(Collections.singleton(kafkaConsumerTopicTwitter)));
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    }
    
    public void consumeTwitterEventFeedback(Runnable beforePollingTask)  throws ConsumerException {
    		try {
        		beforePollingTask.run();
            	while(true) {
	    			ConsumerRecords<String, TwitterEventFeedbackDto> records = consumer.poll(100);
	    			StreamSupport.stream(records.spliterator(), false)
	                .map(record -> processTwitterEventFeedback(new TwitterEventFeedbackRecord(record.key(), record.value())))
	                .forEach(twitterEventFeedbackRecordConsumer);
	    			consumer.commitSync();
            	}
    		} catch(WakeupException ex) {
    			System.out.println("shutting down");
    		} catch(RuntimeException re) {
    			exceptionConsumer.accept(re);
    	
    		} finally {
    			consumer.close();
    		}
    }
    
    private TwitterEventFeedbackRecord processTwitterEventFeedback(TwitterEventFeedbackRecord twitterEventFeedbackRecord) {
    	try {
    		String messageValue =  twitterEventFeedbackRecord.getMessageValue().toString();
    		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!"+messageValue);
    		persistEvent(messageValue);
    	} catch(Exception ex) {

    		System.out.println(ex.getClass().getSimpleName());
    		AmqpException aex = new AmqpException("error writing to RabbitMQ");
    		exceptionConsumer.accept(aex);
    	}
    	
    	
    	return twitterEventFeedbackRecord;
    }
    
   
	private void persistEvent(String messageValue) {
		try {
			FeedbackDetail feedbackDetail = new FeedbackDetail();
			feedbackDetail.setMessageValue(messageValue);
			
			FeedbackDetail returnFeedbackDetail = eventFeedbackRepository.save(feedbackDetail);
			System.out.println("FeedbackDetail Id: "+returnFeedbackDetail.getId()+" value "+messageValue);
		} catch(Exception ex) {
			FeedbackRepoException repoException = new FeedbackRepoException("feedbackrepo operation failed");
			exceptionConsumer.accept(repoException);
		}
	}

    
    public void stop() {
    	consumer.wakeup();
    }

}
