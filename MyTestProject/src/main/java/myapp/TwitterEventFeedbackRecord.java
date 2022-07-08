package myapp;

import myapp.generated.avro.TwitterEventFeedbackDto;

public class TwitterEventFeedbackRecord {
	private String messageKey;
	private TwitterEventFeedbackDto messageValue;
	public String getMessageKey() {
		return messageKey;
	}
	
	
	/**
	 * @param messageKey
	 * @param messageValue
	 */
	public TwitterEventFeedbackRecord(String messageKey, TwitterEventFeedbackDto messageValue) {
		this.messageKey = messageKey;
		this.messageValue = messageValue;
	}


	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	public TwitterEventFeedbackDto getMessageValue() {
		return messageValue;
	}
	public void setMessageValue(TwitterEventFeedbackDto messageValue) {
		this.messageValue = messageValue;
	}
	

}
