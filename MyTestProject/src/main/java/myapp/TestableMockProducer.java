package myapp;

import java.util.concurrent.Future;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.serialization.Serializer;

public class TestableMockProducer<K,V> extends MockProducer<String, SpecificRecord> {
	private boolean throwException = false;
	
	public boolean isThrowException() {
		return throwException;
	}

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	/**
	 * 
	 */
	public TestableMockProducer() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param autoComplete
	 * @param partitioner
	 * @param keySerializer
	 * @param valueSerializer
	 */
	public TestableMockProducer(boolean autoComplete, Partitioner partitioner, Serializer<String> keySerializer,
			Serializer<SpecificRecord> valueSerializer) {
		super(autoComplete, partitioner, keySerializer, valueSerializer);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param autoComplete
	 * @param keySerializer
	 * @param valueSerializer
	 */
	public TestableMockProducer(boolean autoComplete, Serializer<String> keySerializer,
			Serializer<SpecificRecord> valueSerializer) {
		super(autoComplete, keySerializer, valueSerializer);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cluster
	 * @param autoComplete
	 * @param partitioner
	 * @param keySerializer
	 * @param valueSerializer
	 */
	public TestableMockProducer(Cluster cluster, boolean autoComplete, Partitioner partitioner,
			Serializer<String> keySerializer, Serializer<SpecificRecord> valueSerializer) {
		super(cluster, autoComplete, partitioner, keySerializer, valueSerializer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public synchronized Future<RecordMetadata> send(ProducerRecord<String, SpecificRecord> record) {
		// TODO Auto-generated method stub
		if(throwException) {
			super.abortTransaction();
		}
		return super.send(record);
		
	}

	
}
