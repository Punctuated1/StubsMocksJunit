package myapp;

import org.apache.kafka.clients.consumer.Consumer;

import myapp.generated.avro.TwitterEventFeedbackDto;

public class TwitterEventFeebackConfig {
	private Consumer<String, TwitterEventFeedbackDto> consumer;
    private java.util.function.Consumer<Throwable> exceptionConsumer;
    private java.util.function.Consumer<TwitterEventFeedbackRecord> twitterEventFeedbackRecordConsumer;

    public Consumer<String, TwitterEventFeedbackDto> getConsumer() {
		return consumer;
	}
	public void setConsumer(Consumer<String, TwitterEventFeedbackDto> consumer) {
		this.consumer = consumer;
	}
	public java.util.function.Consumer<Throwable> getExceptionConsumer() {
		return exceptionConsumer;
	}
	public void setExceptionConsumer(java.util.function.Consumer<Throwable> exceptionConsumer) {
		this.exceptionConsumer = exceptionConsumer;
	}
	public java.util.function.Consumer<TwitterEventFeedbackRecord> getTwitterEventFeedbackRecordConsumer() {
		return twitterEventFeedbackRecordConsumer;
	}
	public void setTwitterEventFeedbackRecordConsumer(
			java.util.function.Consumer<TwitterEventFeedbackRecord> twitterEventFeedbackRecordConsumer) {
		this.twitterEventFeedbackRecordConsumer = twitterEventFeedbackRecordConsumer;
	}


}
